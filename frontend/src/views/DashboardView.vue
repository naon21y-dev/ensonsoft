<script setup>
import { computed, onMounted, ref } from 'vue'
import { getDashboard } from '../api/Dashboard'
import DashboardStatusPanel from '../components/DashboardStatusPanel.vue'

const dashboard = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const authenticationRequired = ref(false)
const number = (value) => value.toLocaleString('ko-KR')
const date = (value) => value ? value.replace('T', ' ').slice(0, 19) : '-'
const palette = { normal: '#219b83', warning: '#e5a434', error: '#e06270', maintenance: '#6089dc', offline: '#7b8499', inactive: '#bec7d5' }
const equipmentLabels = { NORMAL: '정상', WARNING: '주의', ERROR: '장애', MAINTENANCE: '점검중', OFFLINE: '오프라인', INACTIVE: '운영 중지' }
const maintenanceLabels = { REPORTED: '접수', IN_PROGRESS: '처리중', COMPLETED: '완료' }
const eventLabels = { UNPROCESSED: '미처리', PROCESSING: '처리중', COMPLETED: '완료' }
const eventTypes = { ACCIDENT: '사고 감지', STOPPED_VEHICLE: '정지 차량', WRONG_WAY: '역주행', PEDESTRIAN: '보행자 감지', CONGESTION: '혼잡', ETC: '기타' }
const severityLabels = { NORMAL: '일반', WARNING: '주의', CRITICAL: '긴급' }
const kpis = computed(() => dashboard.value ? [
  { label: '전체 현장', value: dashboard.value.totalSites, unit: '개소', note: '등록된 전체 현장', to: '/sites', tone: 'blue', icon: '▦' },
  { label: '전체 장비', value: dashboard.value.totalEquipments, unit: '대', note: '모든 상태의 장비', to: '/equipments', tone: 'blue', icon: '▤' },
  { label: '정상 장비', value: dashboard.value.normalEquipments, unit: '대', note: '정상 상태 장비', to: '/equipments', tone: 'green', icon: '✓' },
  { label: '장애 장비', value: dashboard.value.errorEquipments, unit: '대', note: '장애 상태 장비', to: '/equipments', tone: 'red', icon: '!' },
  { label: '미처리 유지보수', value: dashboard.value.pendingMaintenances, unit: '건', note: '접수 + 처리중', to: '/maintenance', tone: 'amber', icon: '↻' },
  { label: '관제 이벤트', value: dashboard.value.totalEvents, unit: '건', note: '전체 영상분석 이벤트', to: '/monitoring', tone: 'violet', icon: '◉' }
] : [])

function items(counts, labels, colors) {
  return Object.entries(labels).map(([key, label]) => ({ key, label, count: counts[key], color: colors[key] }))
}
const panels = computed(() => {
  if (!dashboard.value) return []
  const colors = { NORMAL: palette.normal, WARNING: palette.warning, ERROR: palette.error, MAINTENANCE: palette.maintenance, OFFLINE: palette.offline, INACTIVE: palette.inactive }
  return [
    { title: '장비 상태별 현황', subtitle: '현장에 연결된 전체 장비', to: '/equipments', items: items(dashboard.value.equipmentStatuses, equipmentLabels, colors) },
    { title: '현장 상태별 현황', subtitle: '등록 현장의 현재 운영 상태', to: '/sites', items: items(dashboard.value.siteStatuses, { NORMAL: '정상', WARNING: '주의', ERROR: '장애', MAINTENANCE: '점검중', INACTIVE: '운영 중지' }, colors) },
    { title: '유지보수 처리 현황', subtitle: '접수부터 처리 완료까지', to: '/maintenance', items: items(dashboard.value.maintenanceStatuses, maintenanceLabels, { REPORTED: palette.warning, IN_PROGRESS: palette.maintenance, COMPLETED: palette.normal }) },
    { title: '관제 이벤트 현황', subtitle: '영상분석 이벤트의 처리 상태', to: '/monitoring', items: items(dashboard.value.eventStatuses, eventLabels, { UNPROCESSED: palette.error, PROCESSING: palette.maintenance, COMPLETED: palette.normal }) }
  ]
})
const recentMaintenances = computed(() => Array.isArray(dashboard.value?.recentMaintenances) ? dashboard.value.recentMaintenances : [])
const recentEvents = computed(() => Array.isArray(dashboard.value?.recentEvents) ? dashboard.value.recentEvents : [])

async function loadDashboard() {
  if (loading.value) return
  loading.value = true
  errorMessage.value = ''
  authenticationRequired.value = false
  try {
    const response = await getDashboard()
    const data = response.data
    if (String(response.headers?.['content-type'] || '').includes('text/html')) {
      authenticationRequired.value = true
      dashboard.value = null
      throw new Error('로그인 화면이 반환되었습니다. 다시 로그인한 뒤 대시보드를 열어주세요.')
    }
    // 인증 만료 등으로 HTML이나 잘못된 응답이 와도 0건으로 오인하지 않습니다.
    const fields = ['totalSites', 'totalEquipments', 'normalEquipments', 'errorEquipments', 'pendingMaintenances', 'totalEvents']
    const statusKeys = { equipmentStatuses: Object.keys(equipmentLabels), siteStatuses: ['NORMAL', 'WARNING', 'ERROR', 'MAINTENANCE', 'INACTIVE'], maintenanceStatuses: Object.keys(maintenanceLabels), eventStatuses: Object.keys(eventLabels) }
    if (!data || fields.some(key => !Number.isFinite(data[key]) || data[key] < 0)
      || Object.entries(statusKeys).some(([key, keys]) => keys.some(status => !Number.isFinite(data[key]?.[status]) || data[key][status] < 0))) {
      throw new Error('대시보드 응답 형식을 확인할 수 없습니다. 로그인 상태와 서버를 확인해주세요.')
    }
    dashboard.value = data
  } catch (error) {
    if (error.response?.status === 401) {
      authenticationRequired.value = true
      dashboard.value = null
    }
    errorMessage.value = error.response?.data?.message || error.message || '대시보드 데이터를 불러오지 못했습니다.'
  } finally { loading.value = false }
}
onMounted(loadDashboard)
</script>

<template>
  <main class="dashboard-page" :aria-busy="loading">
    <header class="dashboard-header">
      <div class="header-copy"><p class="eyebrow"><span aria-hidden="true"></span> SMART MOBILITY / OVERVIEW</p><h1>운영 대시보드</h1><p class="description">현장부터 장비, 장애 대응까지 한눈에 확인하세요.</p></div>
      <div class="header-actions"><button :disabled="loading" @click="loadDashboard"><span aria-hidden="true">↻</span> {{ loading ? '조회 중...' : '새로고침' }}</button><p v-if="dashboard">집계 시각 {{ date(dashboard.generatedAt) }}</p><p v-else>전체 기간 · 현재 DB 기준</p></div>
    </header>

    <div v-if="errorMessage" class="error-banner" role="alert"><div><strong>대시보드 조회에 실패했습니다.</strong><p>{{ errorMessage }}</p><p v-if="dashboard">이전에 조회한 데이터를 표시하고 있습니다.</p></div><RouterLink v-if="authenticationRequired" to="/login">다시 로그인 →</RouterLink><button v-else :disabled="loading" @click="loadDashboard">다시 시도</button></div>
    <div v-if="!dashboard && loading" class="loading-state" role="status"><span class="loading-symbol" aria-hidden="true">◌</span><h2>운영 현황을 집계하고 있습니다</h2><p>현장 · 장비 · 유지보수 · 관제 이벤트</p></div>

    <template v-if="dashboard">
      <div class="section-heading"><h2>주요 운영 지표</h2><span>전체 기간 누적 · 조회 시점 기준</span></div>
      <div class="kpi-grid"><RouterLink v-for="kpi in kpis" :key="kpi.label" :to="kpi.to" class="kpi-card" :class="kpi.tone">
        <div class="kpi-top"><span>{{ kpi.label }}</span><span class="kpi-icon" aria-hidden="true">{{ kpi.icon }}</span></div>
        <div class="kpi-value"><strong>{{ number(kpi.value) }}</strong><span>{{ kpi.unit }}</span></div>
        <div class="kpi-bottom"><span>{{ kpi.note }}</span><span aria-hidden="true">↗</span></div>
      </RouterLink></div>

      <div class="section-heading"><h2>운영 상태 현황</h2><span>상태별 건수 및 비율</span></div>
      <div class="status-grid"><DashboardStatusPanel v-for="panel in panels" :key="panel.title" v-bind="panel" /></div>

      <div class="section-heading"><h2>최근 발생 내역</h2><span>발생 시간순 · 각 최대 5건</span></div>
      <div class="recent-grid">
        <section class="recent-panel"><header><div><span class="panel-tag amber">MAINTENANCE</span><h3>최근 유지보수 장애</h3></div><RouterLink to="/maintenance">전체 보기 ↗</RouterLink></header>
          <p v-if="!recentMaintenances.length" class="empty-state">접수된 유지보수 장애가 없습니다.</p>
          <ul v-else class="recent-list"><li v-for="item in recentMaintenances" :key="item.id">
            <div class="recent-top"><h4>{{ item.title }}</h4><span class="badge" :class="item.status">{{ maintenanceLabels[item.status] }}</span></div>
            <p class="location">{{ item.siteName }} <span aria-hidden="true">/</span> {{ item.equipmentName }}</p>
            <div class="recent-meta"><span>접수자 {{ item.reportedBy }}</span><time :datetime="item.occurredAt">{{ date(item.occurredAt) }}</time></div>
          </li></ul>
        </section>
        <section class="recent-panel"><header><div><span class="panel-tag blue">MONITORING</span><h3>최근 관제 이벤트</h3></div><RouterLink to="/monitoring">전체 보기 ↗</RouterLink></header>
          <p v-if="!recentEvents.length" class="empty-state">발생한 관제 이벤트가 없습니다.</p>
          <ul v-else class="recent-list"><li v-for="item in recentEvents" :key="item.id">
            <div class="recent-top"><h4><span class="severity-dot" :class="item.severity" aria-hidden="true"></span>{{ eventTypes[item.eventType] }}</h4><span class="badge" :class="item.status">{{ eventLabels[item.status] }}</span></div>
            <p class="location">{{ item.location }} <span aria-hidden="true">/</span> {{ item.cameraId }}</p>
            <div class="recent-meta"><span class="severity-text" :class="item.severity">{{ severityLabels[item.severity] }}</span><time :datetime="item.occurredAt">{{ date(item.occurredAt) }}</time></div>
          </li></ul>
        </section>
      </div>
      <footer class="dashboard-footer">미처리 유지보수는 접수·처리중 건수의 합계입니다. 새로고침하면 최신 DB 현황을 조회합니다.</footer>
    </template>
  </main>
</template>

<style scoped>
.dashboard-page { background: #f3f6fa; color: #24344c; text-align: left; padding: 28px; min-height: 85vh; font-size: 14px; color-scheme: light; }
.dashboard-page * { box-sizing: border-box; } h1,h2,h3,h4,p { margin: 0; } a { color: inherit; text-decoration: none; } button { font: inherit; cursor: pointer; } button:disabled { opacity: .65; cursor: wait; } :focus-visible { outline: 3px solid #60a5fa; outline-offset: 4px; }
.dashboard-header { padding: 30px; border-radius: 18px; background: linear-gradient(115deg,#132238,#203c61); color: #fff; display: flex; align-items: center; justify-content: space-between; gap: 24px; position: relative; overflow: hidden; }
.dashboard-header::after { content: ''; position: absolute; width: 240px; height: 240px; border: 1px solid #ffffff0d; border-radius: 50%; right: 150px; top: -100px; pointer-events: none; box-shadow: 0 0 0 35px #ffffff04, 0 0 0 70px #ffffff03; }
.header-copy,.header-actions { z-index: 1; } .eyebrow { color: #aabed9; letter-spacing: 1.7px; font-size: 10px; display: flex; align-items: center; gap: 8px; } .eyebrow span { width: 6px; height: 6px; border-radius: 50%; background: #75c4be; }
h1 { color: #fff; font-size: 30px; font-weight: 700; letter-spacing: -.8px; margin: 13px 0 9px; line-height: 1.3; } .description { font-size: 13px; color: #b8c8dd; }
.header-actions { text-align: right; flex-shrink: 0; } .header-actions button { border: 1px solid #ffffff3b; border-radius: 8px; color: white; background: #ffffff0d; padding: 10px 16px; } .header-actions button:hover { background: #ffffff1a; } .header-actions button span { font-size: 19px; margin-right: 7px; } .header-actions p { font-size: 10px; color: #b8c8dd; margin-top: 12px; }
.section-heading { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin: 30px 2px 14px; } h2 { font-size: 16px; font-weight: 700; color: #24344c; letter-spacing: -.2px; } .section-heading>span { font-size: 11px; color: #738197; }
.kpi-grid { display: grid; grid-template-columns: repeat(3,minmax(0,1fr)); gap: 14px; } .kpi-card { background: white; padding: 20px 22px 16px; border: 1px solid #e3e9f1; border-radius: 13px; transition: border-color .15s,box-shadow .15s; } .kpi-card:hover { border-color: #9bb5d8; box-shadow: 0 4px 16px #274b7810; }
.kpi-top,.kpi-bottom { display: flex; align-items: center; justify-content: space-between; gap: 8px; } .kpi-top { font-size: 13px; font-weight: 600; } .kpi-icon { border-radius: 8px; width: 30px; height: 30px; display: grid; place-items: center; font-size: 18px; background: #eef4fe; color: #527aba; }
.kpi-value { display: flex; align-items: baseline; gap: 8px; margin: 13px 0 15px; } .kpi-value strong { font-size: 34px; line-height: 1.1; letter-spacing: -1px; font-variant-numeric: tabular-nums; color: #192b44; } .kpi-value>span { color: #7c899b; font-size: 12px; } .kpi-bottom { padding-top: 12px; border-top: 1px solid #f0f3f7; color: #738197; font-size: 11px; }
.green .kpi-icon { background: #e9f7f2; color: #20836f; } .red .kpi-icon { background: #fceef0; color: #c84e60; } .amber .kpi-icon { background: #fff5e3; color: #a87926; } .violet .kpi-icon { background: #f1edfb; color: #8366bc; } .red .kpi-value strong { color: #c14e5c; } .amber .kpi-value strong { color: #a47523; }
.status-grid,.recent-grid { display: grid; grid-template-columns: repeat(2,minmax(0,1fr)); gap: 18px; } .recent-panel { background: white; border: 1px solid #e3e9f1; border-radius: 16px; min-width: 0; overflow: hidden; } .recent-panel header { display: flex; align-items: center; justify-content: space-between; padding: 22px 24px; gap: 12px; border-bottom: 1px solid #edf1f6; } .recent-panel h3 { font-size: 16px; margin-top: 6px; } .recent-panel header a { color: #3b63a0; font-size: 12px; white-space: nowrap; } .recent-panel header a:hover { text-decoration: underline; } .panel-tag { font-size: 9px; letter-spacing: 1.4px; font-weight: 700; } .panel-tag.amber { color: #a87926; } .panel-tag.blue { color: #527aba; }
.recent-list { list-style: none; margin: 0; padding: 0 24px; } .recent-list li { padding: 19px 0; border-bottom: 1px solid #edf1f6; } .recent-list li:last-child { border-bottom: 0; } .recent-top { display: flex; align-items: flex-start; justify-content: space-between; gap: 10px; } h4 { font-size: 13px; color: #24344c; line-height: 1.6; overflow-wrap: anywhere; }
.badge { flex-shrink: 0; font-size: 10px; font-weight: 600; padding: 2px 8px; border-radius: 5px; white-space: nowrap; } .REPORTED { background: #fff4dc; color: #956c20; } .IN_PROGRESS,.PROCESSING { background: #edf3ff; color: #426eb7; } .COMPLETED { background: #e8f6ef; color: #25785d; } .UNPROCESSED { background: #fcecef; color: #b44a5a; }
.location { font-size: 12px; color: #607089; margin-top: 6px; overflow-wrap: anywhere; } .location>span { color: #bdc6d2; margin: 0 3px; } .recent-meta { font-size: 10px; color: #79879b; display: flex; flex-wrap: wrap; justify-content: space-between; gap: 6px; margin-top: 8px; } .recent-meta time { font-variant-numeric: tabular-nums; }
.severity-dot { display: inline-block; width: 7px; height: 7px; border-radius: 50%; margin-right: 8px; } .severity-dot.NORMAL { background: #219b83; } .severity-dot.WARNING { background: #e5a434; } .severity-dot.CRITICAL { background: #e06270; } .severity-text.CRITICAL { color: #b44a5a; } .severity-text.WARNING { color: #956c20; }
.empty-state { color: #738197; text-align: center; padding: 60px 20px; font-size: 13px; } .dashboard-footer { color: #738197; font-size: 11px; margin: 22px 2px 0; line-height: 1.7; }
.error-banner { display: flex; justify-content: space-between; align-items: center; gap: 14px; border: 1px solid #f2cad0; background: #fff4f5; color: #a53f50; padding: 18px; border-radius: 12px; margin-top: 20px; font-size: 13px; overflow-wrap: anywhere; } .error-banner p { margin-top: 6px; } .error-banner button { white-space: nowrap; border: 1px solid #e8b1bc; background: white; color: #a53f50; border-radius: 6px; padding: 8px 12px; }
.loading-state { padding: 90px 20px; text-align: center; color: #64748b; } .loading-symbol { font-size: 40px; color: #6089dc; } .loading-state h2 { margin: 15px 0 8px; } .loading-state p { font-size: 13px; }
@media(max-width:800px) { .dashboard-page { padding: 20px; } .dashboard-header { align-items: flex-start; flex-direction: column; padding: 24px; gap: 20px; } .header-actions { display: flex; align-items: center; justify-content: space-between; width: 100%; text-align: left; gap: 12px; } .header-actions p { margin: 0; } .status-grid,.recent-grid { grid-template-columns: 1fr; } .kpi-grid { grid-template-columns: repeat(2,minmax(0,1fr)); } }
@media(max-width:480px) { .dashboard-page { padding: 14px; } .dashboard-header { padding: 20px; } h1 { font-size: 26px; } .eyebrow { font-size: 8px; letter-spacing: 1px; } .header-actions { align-items: flex-start; flex-direction: column; } .section-heading { align-items: flex-start; flex-direction: column; gap: 4px; } .kpi-card { padding: 15px 12px; } .kpi-top { font-size: 12px; } .kpi-icon { width: 24px; height: 24px; font-size: 15px; } .kpi-value strong { font-size: 28px; } .kpi-bottom { font-size: 10px; } .recent-panel header { padding: 20px; } .recent-list { padding: 0 20px; } .error-banner { flex-direction: column; align-items: flex-start; } }
@media(prefers-reduced-motion:reduce) { .kpi-card { transition: none; } }
</style>
