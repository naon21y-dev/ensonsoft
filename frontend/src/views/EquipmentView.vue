<script setup>
import { computed, onMounted, ref } from 'vue'
import { getSites } from '../api/Site'
import { getEquipments, getEquipment, getEquipmentHistories, createEquipment, updateEquipment, updateEquipmentStatus, deleteEquipment } from '../api/Equipment'

const statuses = { NORMAL: '정상', WARNING: '주의', ERROR: '장애', MAINTENANCE: '점검중', OFFLINE: '오프라인', INACTIVE: '운영 중지' }
const types = { CCTV: 'CCTV', LPR_CAMERA: '차량번호 인식 카메라', VEHICLE_DETECTOR: '차량 검지기', TOLL_EQUIPMENT: '요금 징수 장비', VMS: '도로 전광판', NETWORK: '네트워크', SERVER: '서버', ETC: '기타' }
const equipments = ref([])
const allEquipments = ref([])
const sites = ref([])
const loading = ref(false)
const busy = ref(false)
const errorMessage = ref('')
const notice = ref('')
const filters = ref({ name: '', siteId: '', status: '', equipmentType: '' })
const selected = ref(null)
const histories = ref([])
const detailOpen = ref(false)
const detailLoading = ref(false)
const detailError = ref('')
const historyError = ref('')
const newStatus = ref('')
const reason = ref('')
const formOpen = ref(false)
const editingId = ref(null)
const formError = ref('')
const emptyForm = () => ({ siteId: '', equipmentCode: '', name: '', equipmentType: 'CCTV', manufacturer: '', modelName: '', serialNumber: '', installLocation: '', installedAt: '', ipAddress: '', description: '' })
const form = ref(emptyForm())
const optionalFields = [ ['manufacturer', '제조사', 100], ['modelName', '모델', 100], ['serialNumber', '시리얼 번호', 100], ['installLocation', '설치 위치', 200], ['ipAddress', 'IP 주소', 45] ]
const arrayData = (response) => Array.isArray(response.data) ? response.data : []
const message = (error, fallback) => error.response?.data?.message || fallback
const date = (value) => value ? value.replace('T', ' ').slice(0, 19) : '-'
const kpis = computed(() => [
  { label: '전체 장비', count: allEquipments.value.length },
  ...Object.entries(statuses).filter(([key]) => key !== 'INACTIVE').map(([key, label]) => ({ label, count: allEquipments.value.filter(e => e.status === key).length }))
])

let listRequest = 0
async function loadList() {
  const request = ++listRequest
  loading.value = true
  errorMessage.value = ''
  try {
    const params = Object.fromEntries(Object.entries(filters.value).filter(([, value]) => value !== ''))
    const response = await getEquipments(params)
    if (request === listRequest) equipments.value = arrayData(response)
  } catch (error) {
    if (request === listRequest) errorMessage.value = message(error, '장비 목록을 불러오지 못했습니다.')
  } finally {
    if (request === listRequest) loading.value = false
  }
}
async function refresh() {
  await loadList()
  try {
    const [equipmentResponse, siteResponse] = await Promise.all([getEquipments(), getSites()])
    allEquipments.value = arrayData(equipmentResponse)
    sites.value = arrayData(siteResponse)
  } catch (error) {
    errorMessage.value = message(error, '전체 장비 및 현장 정보를 불러오지 못했습니다.')
  }
}
function resetSearch() {
  filters.value = { name: '', siteId: '', status: '', equipmentType: '' }
  loadList()
}
let detailRequest = 0
async function loadHistories(id) {
  historyError.value = ''
  try {
    const response = await getEquipmentHistories(id)
    if (selected.value?.id === id) histories.value = arrayData(response)
  } catch (error) {
    if (selected.value?.id === id) historyError.value = message(error, '이력 조회에 실패했습니다. 다시 조회해주세요.')
  }
}
async function openDetail(equipment) {
  const request = ++detailRequest
  selected.value = null
  histories.value = []
  detailOpen.value = true
  detailLoading.value = true
  detailError.value = ''
  historyError.value = ''
  newStatus.value = ''
  reason.value = ''
  try {
    const response = await getEquipment(equipment.id)
    if (request !== detailRequest) return
    selected.value = response.data
    await loadHistories(equipment.id)
  } catch (error) {
    if (request === detailRequest) detailError.value = message(error, '장비 상세 조회에 실패했습니다.')
  } finally {
    if (request === detailRequest) detailLoading.value = false
  }
}
function closeDetail() {
  if (busy.value) return
  ++detailRequest
  detailOpen.value = false
  selected.value = null
}
function openForm(equipment = null) {
  editingId.value = equipment?.id ?? null
  form.value = emptyForm()
  if (equipment) {
    for (const key of Object.keys(form.value)) form.value[key] = equipment[key] ?? ''
  }
  formError.value = ''
  formOpen.value = true
}
async function save() {
  if (busy.value) return
  busy.value = true
  formError.value = ''
  notice.value = ''
  try {
    const payload = { ...form.value, siteId: Number(form.value.siteId), name: form.value.name.trim(), equipmentCode: form.value.equipmentCode.trim(), installedAt: form.value.installedAt || null }
    if (editingId.value) delete payload.equipmentCode
    const response = editingId.value ? await updateEquipment(editingId.value, payload) : await createEquipment(payload)
    if (selected.value?.id === response.data.id) selected.value = response.data
    formOpen.value = false
    notice.value = editingId.value ? '장비 정보를 수정했습니다.' : '장비를 등록했습니다.'
    await refresh()
  } catch (error) {
    formError.value = message(error, '장비 저장에 실패했습니다.')
  } finally { busy.value = false }
}
async function changeStatus() {
  if (busy.value || !selected.value || !newStatus.value) return
  busy.value = true
  detailError.value = ''
  try {
    const response = await updateEquipmentStatus(selected.value.id, newStatus.value, reason.value)
    selected.value = response.data
    newStatus.value = ''
    reason.value = ''
    notice.value = '장비 상태를 변경했습니다.'
    await loadHistories(response.data.id)
    await refresh()
  } catch (error) { detailError.value = message(error, '상태 변경에 실패했습니다.') }
  finally { busy.value = false }
}
async function removeEquipment() {
  if (busy.value || !selected.value || !window.confirm('장비와 해당 상태 변경 이력을 삭제하시겠습니까?')) return
  busy.value = true
  detailError.value = ''
  try {
    await deleteEquipment(selected.value.id)
    detailOpen.value = false
    selected.value = null
    notice.value = '장비를 삭제했습니다.'
    await refresh()
  } catch (error) { detailError.value = message(error, '장비 삭제에 실패했습니다.') }
  finally { busy.value = false }
}
onMounted(refresh)
</script>

<template>
  <div class="equipment-page">
    <header class="page-header">
      <div><p class="eyebrow">EQUIPMENT MANAGEMENT</p><h1>장비 관리</h1><p>현장별 장비의 운영 상태와 변경 이력을 관리합니다.</p></div>
      <button class="primary" :disabled="busy" @click="openForm()">+ 장비 등록</button>
    </header>
    <p v-if="notice" class="notice" role="status">{{ notice }}</p>
    <div v-if="errorMessage" class="error" role="alert">{{ errorMessage }} <button @click="refresh">다시 조회</button></div>
    <div class="kpi-grid"><div v-for="kpi in kpis" :key="kpi.label" class="kpi-card"><span>{{ kpi.label }}</span><strong>{{ kpi.count }}</strong></div></div>
    <form class="search-panel" @submit.prevent="loadList">
      <label>장비명<input v-model="filters.name" placeholder="장비명 검색" maxlength="100" /></label>
      <label>현장<select v-model="filters.siteId"><option value="">전체 현장</option><option v-for="site in sites" :key="site.id" :value="site.id">{{ site.name }} ({{ site.siteCode }})</option></select></label>
      <label>상태<select v-model="filters.status"><option value="">전체 상태</option><option v-for="(label, key) in statuses" :key="key" :value="key">{{ label }}</option></select></label>
      <label>유형<select v-model="filters.equipmentType"><option value="">전체 유형</option><option v-for="(label, key) in types" :key="key" :value="key">{{ label }}</option></select></label>
      <button class="primary" :disabled="loading">검색</button><button type="button" @click="resetSearch">초기화</button>
    </form>
    <section class="list-panel" :aria-busy="loading">
      <h2>장비 목록 <small>{{ equipments.length }}건</small></h2>
      <p v-if="loading" class="empty">장비 정보를 불러오는 중입니다...</p>
      <div v-else class="table-scroll"><table><thead><tr><th>장비 코드</th><th>장비명</th><th>소속 현장</th><th>유형</th><th>설치 위치</th><th>IP</th><th>상태</th><th>상세</th></tr></thead>
        <tbody><tr v-for="equipment in equipments" :key="equipment.id"><td>{{ equipment.equipmentCode }}</td><td>{{ equipment.name }}</td><td>{{ equipment.siteName }}</td><td>{{ types[equipment.equipmentType] }}</td><td>{{ equipment.installLocation || '-' }}</td><td>{{ equipment.ipAddress || '-' }}</td><td><span class="badge" :class="equipment.status">{{ statuses[equipment.status] }}</span></td><td><button @click="openDetail(equipment)">상세</button></td></tr>
        <tr v-if="!equipments.length"><td colspan="8" class="empty">조회된 장비가 없습니다. 검색 조건을 변경하거나 장비를 등록해주세요.</td></tr></tbody></table></div>
    </section>

    <div v-if="detailOpen" class="overlay" @click.self="closeDetail">
      <section class="modal" role="dialog" aria-modal="true" aria-labelledby="detail-title" @keydown.esc="closeDetail">
        <header class="modal-header"><h2 id="detail-title">장비 상세</h2><button :disabled="busy" aria-label="상세 닫기" @click="closeDetail">닫기</button></header>
        <p v-if="detailLoading" class="empty">상세 정보를 불러오는 중입니다...</p>
        <p v-if="detailError" class="error" role="alert">{{ detailError }}</p>
        <template v-if="selected && !detailLoading">
          <div class="detail-grid">
            <div><span>장비 코드</span>{{ selected.equipmentCode }}</div><div><span>장비명</span>{{ selected.name }}</div>
            <div><span>소속 현장</span>{{ selected.siteName }} ({{ selected.siteCode }})</div><div><span>유형</span>{{ types[selected.equipmentType] }}</div>
            <div v-for="[key, label] in optionalFields" :key="key"><span>{{ label }}</span>{{ selected[key] || '-' }}</div>
            <div><span>설치일</span>{{ date(selected.installedAt) }}</div><div><span>현재 상태</span><b class="badge" :class="selected.status">{{ statuses[selected.status] }}</b></div>
            <div><span>등록일 / 수정일</span>{{ date(selected.createdAt) }} / {{ date(selected.updatedAt) }}</div>
            <div class="wide"><span>설명</span>{{ selected.description || '-' }}</div>
          </div>
          <div class="actions"><button :disabled="busy" @click="openForm(selected)">장비 수정</button><button class="danger" :disabled="busy" @click="removeEquipment">장비 삭제</button></div>
          <section class="section"><h3>상태 변경</h3><form class="status-form" @submit.prevent="changeStatus">
            <label>변경 상태<select v-model="newStatus" required :disabled="busy"><option value="">상태 선택</option><option v-for="(label, key) in statuses" :key="key" :value="key" :disabled="key === selected.status">{{ label }}</option></select></label>
            <label>변경 사유<input v-model="reason" maxlength="500" placeholder="예: 통신장애" :disabled="busy" /></label><button class="primary" :disabled="busy || !newStatus || newStatus === selected.status">상태 변경</button>
          </form></section>
          <section class="section"><h3>상태 변경 이력</h3>
            <p v-if="historyError" class="error" role="alert">{{ historyError }} <button @click="loadHistories(selected.id)">다시 조회</button></p>
            <p v-else-if="!histories.length" class="empty">상태 변경 이력이 없습니다.</p>
            <ol class="timeline"><li v-for="history in histories" :key="history.id"><strong>{{ statuses[history.previousStatus] }} → {{ statuses[history.newStatus] }}</strong><p>변경자: {{ history.changedBy }} · {{ date(history.changedAt) }}</p><p>{{ history.reason || '변경 사유 없음' }}</p></li></ol>
          </section>
        </template>
      </section>
    </div>

    <div v-if="formOpen" class="overlay form-overlay" @click.self="!busy && (formOpen = false)">
      <section class="modal" role="dialog" aria-modal="true" aria-labelledby="form-title" @keydown.esc="!busy && (formOpen = false)">
        <header class="modal-header"><h2 id="form-title">{{ editingId ? '장비 수정' : '장비 등록' }}</h2><button :disabled="busy" @click="formOpen = false">닫기</button></header>
        <p v-if="formError" class="error" role="alert">{{ formError }}</p>
        <p v-if="!sites.length" class="error">선택할 현장이 없습니다. 현장 등록 또는 조회 상태를 확인해주세요. <RouterLink to="/sites">현장 관리</RouterLink></p>
        <form @submit.prevent="save"><fieldset :disabled="busy"><div class="form-grid">
          <label>소속 현장 *<select v-model="form.siteId" required><option value="">현장 선택</option><option v-for="site in sites" :key="site.id" :value="site.id">{{ site.name }} ({{ site.siteCode }})</option></select></label>
          <label>장비 코드 *<input v-model="form.equipmentCode" required maxlength="30" :disabled="!!editingId" placeholder="EQ-001" /></label>
          <label>장비명 *<input v-model="form.name" required maxlength="100" /></label>
          <label>장비 유형 *<select v-model="form.equipmentType" required><option v-for="(label, key) in types" :key="key" :value="key">{{ label }}</option></select></label>
          <label v-for="[key, label, max] in optionalFields" :key="key">{{ label }}<input v-model="form[key]" :maxlength="max" /></label>
          <label>설치일<input v-model="form.installedAt" type="datetime-local" step="1" /></label>
          <label class="wide">설명<textarea v-model="form.description" maxlength="500" rows="3"></textarea></label>
        </div><div class="actions"><button type="button" @click="formOpen = false">취소</button><button class="primary" :disabled="!sites.length">{{ busy ? '저장 중...' : '저장' }}</button></div></fieldset></form>
      </section>
    </div>
  </div>
</template>

<style scoped>
.equipment-page { padding: 32px; background: #f5f7fb; color: #172033; text-align: left; min-height: 80vh; color-scheme: light; font-size: 14px; }
.equipment-page * { box-sizing: border-box; }
h1 { font-size: 30px; margin: 8px 0 12px; color: #172033; } h2 { color: #172033; font-size: 20px; } h3 { font-size: 17px; }
.page-header,.modal-header,.actions { display: flex; align-items: center; justify-content: space-between; gap: 12px; } .page-header { margin-bottom: 24px; }
.eyebrow { font-size: 11px; letter-spacing: 2px; color: #64748b; } .page-header p { color: #64748b; }
button,input,select,textarea { font: inherit; border: 1px solid #d9e0ea; border-radius: 8px; padding: 10px 12px; background: white; color: #172033; }
button { cursor: pointer; white-space: nowrap; } button:disabled { opacity: .5; cursor: not-allowed; } .primary { background: #172033; color: white; border-color: #172033; } .danger { color: #b91c1c; }
label { display: flex; flex-direction: column; gap: 6px; min-width: 0; } input,select,textarea { width: 100%; } :focus-visible { outline: 2px solid #2563eb; outline-offset: 2px; }
.kpi-grid { display: grid; grid-template-columns: repeat(6,1fr); gap: 12px; margin-bottom: 22px; } .kpi-card { background: white; border: 1px solid #e7ebf0; border-radius: 12px; padding: 18px; } .kpi-card span { color: #64748b; } .kpi-card strong { display: block; font-size: 28px; margin-top: 10px; }
.search-panel { display: flex; align-items: end; flex-wrap: wrap; gap: 10px; padding: 18px; background: white; border-radius: 12px; margin-bottom: 20px; } .search-panel label { flex: 1 1 130px; }
.list-panel { padding: 20px; background: white; border: 1px solid #e7ebf0; border-radius: 12px; } small { color: #64748b; font-size: 13px; } .table-scroll { overflow-x: auto; } table { border-collapse: collapse; width: 100%; font-size: 13px; } th,td { padding: 13px 10px; border-bottom: 1px solid #e7ebf0; text-align: left; } th { white-space: nowrap; background: #f8fafc; color: #64748b; } td { overflow-wrap: anywhere; min-width: 85px; }
.badge { display: inline-block; white-space: nowrap; border-radius: 20px; padding: 4px 9px; font-size: 12px; background: #eef2f6; color: #475569; } .NORMAL { background: #dcfce7; color: #166534; } .WARNING { background: #fef3c7; color: #92400e; } .ERROR { background: #fee2e2; color: #b91c1c; } .MAINTENANCE { background: #dbeafe; color: #1e40af; }
.empty { padding: 32px; text-align: center; color: #64748b; } .error,.notice { padding: 12px; border-radius: 8px; margin-bottom: 14px; } .error { color: #b91c1c; background: #fef2f2; } .notice { color: #166534; background: #f0fdf4; }
.overlay { position: fixed; inset: 0; z-index: 100; display: flex; align-items: center; justify-content: center; padding: 20px; background: #0f172a99; } .form-overlay { z-index: 110; } .modal { width: min(850px,100%); max-height: 90vh; overflow-y: auto; background: white; padding: 26px; border-radius: 18px; box-shadow: 0 25px 70px #0003; } .modal-header { margin-bottom: 22px; }
.detail-grid,.form-grid { display: grid; grid-template-columns: repeat(2,minmax(0,1fr)); gap: 14px; } .detail-grid>div { padding: 14px; background: #fafbfd; border: 1px solid #e7ebf0; border-radius: 8px; overflow-wrap: anywhere; white-space: pre-wrap; } .detail-grid span { display: block; color: #64748b; font-size: 12px; margin-bottom: 5px; } .wide { grid-column: 1/-1; }
.actions { justify-content: flex-end; margin-top: 20px; } .section { border-top: 1px solid #e7ebf0; margin-top: 24px; padding-top: 12px; } .status-form { display: grid; grid-template-columns: 160px 1fr auto; align-items: end; gap: 10px; } .timeline { list-style: none; padding: 0 0 0 15px; } .timeline li { border-left: 2px solid #cbd5e1; padding: 0 0 24px 18px; position: relative; overflow-wrap: anywhere; } .timeline li::before { content: ''; position: absolute; width: 10px; height: 10px; border-radius: 50%; background: #172033; top: 5px; left: -6px; } .timeline p { color: #64748b; margin-top: 7px; white-space: pre-wrap; } fieldset { padding: 0; margin: 0; border: 0; min-width: 0; }
@media(max-width:900px) { .kpi-grid { grid-template-columns: repeat(3,1fr); } .equipment-page { padding: 20px; } .status-form { grid-template-columns: 1fr; } }
@media(max-width:600px) { .kpi-grid { grid-template-columns: repeat(2,1fr); } .page-header { align-items: flex-start; flex-direction: column; } .detail-grid,.form-grid { grid-template-columns: 1fr; } .modal { padding: 18px; } }
</style>
