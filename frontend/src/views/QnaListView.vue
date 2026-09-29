<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
import { useUiMessage, useFormatLocale } from '../i18n'
const formatLocale = useFormatLocale()

import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getQnaList } from '../api/Qna'

const router = useRouter()

const qnaList = ref([])
const loading = ref(false)
const errorMessage = useUiMessage()

const keyword = ref('')
const statusFilter = ref('ALL')
const categoryFilter = ref('ALL')

const categoryLabels = computed(() => ({
  GENERAL: t('m551'),
  SITE: t('m552'),
  EQUIPMENT: t('m553'),
  VEHICLE: t('m342'),
  VIDEO_ANALYSIS: t('m554'),
  MAINTENANCE: t('m555'),
  ETC: t('m096')
}))

const loadQna = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getQnaList()
    qnaList.value = response.data
  } catch (error) {
    console.error(error)
    errorMessage.value =
      error.response?.data?.message ||
      t('m556')
  } finally {
    loading.value = false
  }
}

const totalCount = computed(() => qnaList.value.length)

const waitingCount = computed(() =>
  qnaList.value.filter(qna => qna.status === 'WAITING').length
)

const answeredCount = computed(() =>
  qnaList.value.filter(qna => qna.status === 'ANSWERED').length
)

const secretCount = computed(() =>
  qnaList.value.filter(qna => qna.secret).length
)

const filteredQna = computed(() => {
  const search = keyword.value.trim().toLowerCase()

  return qnaList.value.filter(qna => {
    const keywordMatch =
      !search ||
      qna.title?.toLowerCase().includes(search) ||
      qna.username?.toLowerCase().includes(search)

    const statusMatch =
      statusFilter.value === 'ALL' ||
      qna.status === statusFilter.value

    const categoryMatch =
      categoryFilter.value === 'ALL' ||
      qna.category === categoryFilter.value

    return keywordMatch && statusMatch && categoryMatch
  })
})

const formatDate = (value) => {
  if (!value) return '-'

  return new Intl.DateTimeFormat(formatLocale.value, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).format(new Date(value))
}

const goDetail = (id) => {
  router.push(`/qna/${id}`)
}

const goWrite = () => {
  router.push('/qna/write')
}

onMounted(loadQna)
</script>

<template>
  <div class="qna-page">

    <header class="page-header">
      <div>
        <span class="eyebrow">{{ t('m557') }}</span>
        <h1>{{ t('m558') }}</h1>
        <p>
          {{ t('m559') }}
        </p>
      </div>

      <button class="write-button" @click="goWrite">
        <span>＋</span>
        {{ t('m560') }}
      </button>
    </header>

    <!-- KPI -->
    <section class="stats-grid">
      <article class="stat-card">
        <span>{{ t('m561') }}</span>
        <strong>{{ totalCount }}</strong>
        <small>{{ t('m562') }}</small>
      </article>

      <article class="stat-card waiting">
        <span>{{ t('m563') }}</span>
        <strong>{{ waitingCount }}</strong>
        <small>{{ t('m564') }}</small>
      </article>

      <article class="stat-card answered">
        <span>{{ t('m565') }}</span>
        <strong>{{ answeredCount }}</strong>
        <small>{{ t('m566') }}</small>
      </article>

      <article class="stat-card secret">
        <span>{{ t('m567') }}</span>
        <strong>{{ secretCount }}</strong>
        <small>{{ t('m568') }}</small>
      </article>
    </section>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <!-- LIST -->
    <section class="qna-panel">

      <div class="panel-header">
        <div>
          <h2>{{ t('m569') }}</h2>
          <p>{{ t('counts.inquiries', filteredQna.length) }}</p>
        </div>

        <div class="filters">
          <div class="search-box">
            <span>⌕</span>

            <input
              v-model="keyword"
              type="text"
              :placeholder="t('m571')"
            />
          </div>

          <select v-model="statusFilter">
            <option value="ALL">{{ t('m184') }}</option>
            <option value="WAITING">{{ t('m563') }}</option>
            <option value="ANSWERED">{{ t('m565') }}</option>
          </select>

          <select v-model="categoryFilter">
            <option value="ALL">{{ t('m186') }}</option>
            <option value="GENERAL">{{ t('m551') }}</option>
            <option value="SITE">{{ t('m552') }}</option>
            <option value="EQUIPMENT">{{ t('m553') }}</option>
            <option value="VEHICLE">{{ t('m342') }}</option>
            <option value="VIDEO_ANALYSIS">{{ t('m554') }}</option>
            <option value="MAINTENANCE">{{ t('m555') }}</option>
            <option value="ETC">{{ t('m096') }}</option>
          </select>
        </div>
      </div>

      <!-- 로딩 -->
      <div v-if="loading" class="state-box">
        <div class="spinner"></div>
        <strong>{{ t('m572') }}</strong>
      </div>

      <!-- 빈 목록 -->
      <div
        v-else-if="filteredQna.length === 0"
        class="state-box"
      >
        <div class="empty-icon">?</div>
        <strong>{{ t('m573') }}</strong>
        <span>{{ t('m574') }}</span>
      </div>

      <!-- 데스크톱 -->
      <div v-else class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>{{ t('m183') }}</th>
              <th>{{ t('m185') }}</th>
              <th>{{ t('m042') }}</th>
              <th>{{ t('m024') }}</th>
              <th>{{ t('m026') }}</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="qna in filteredQna"
              :key="qna.id"
              @click="goDetail(qna.id)"
            >
              <td>
                <span
                  class="status-badge"
                  :class="qna.status.toLowerCase()"
                >
                  <span class="dot"></span>

                  {{
                    qna.status === 'ANSWERED'
                      ? t('m575')
                      : t('m576')
                  }}
                </span>
              </td>

              <td>
                <span class="category-badge">
                  {{ categoryLabels[qna.category] || qna.category }}
                </span>
              </td>

              <td class="title-cell">
                <span v-if="qna.secret" class="lock">
                  🔒
                </span>

                {{ qna.title }}
              </td>

              <td>{{ qna.username }}</td>

              <td>{{ formatDate(qna.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 모바일 -->
      <div
        v-if="!loading && filteredQna.length"
        class="mobile-list"
      >
        <article
          v-for="qna in filteredQna"
          :key="`mobile-${qna.id}`"
          class="mobile-card"
          @click="goDetail(qna.id)"
        >
          <div class="mobile-top">
            <span
              class="status-badge"
              :class="qna.status.toLowerCase()"
            >
              <span class="dot"></span>

              {{
                qna.status === 'ANSWERED'
                  ? t('m575')
                  : t('m576')
              }}
            </span>

            <span class="category-badge">
              {{ categoryLabels[qna.category] }}
            </span>
          </div>

          <h3>
            <span v-if="qna.secret">🔒</span>
            {{ qna.title }}
          </h3>

          <div class="mobile-meta">
            <span>{{ qna.username }}</span>
            <span>{{ formatDate(qna.createdAt) }}</span>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.qna-page {
  width: 100%;
  padding: 36px 40px 60px;
  color: #172033;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 28px;
}

.eyebrow {
  display: block;
  margin-bottom: 8px;
  color: #3b82f6;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: .18em;
}

.page-header h1 {
  margin: 0;
  font-size: 30px;
  font-weight: 800;
  letter-spacing: -.04em;
}

.page-header p {
  margin: 9px 0 0;
  color: #7b8496;
  font-size: 13px;
}

.write-button {
  display: flex;
  align-items: center;
  gap: 7px;
  height: 42px;
  padding: 0 18px;
  border: 0;
  border-radius: 10px;
  background: #2563eb;
  color: white;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 5px 14px rgba(37, 99, 235, .18);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 15px;
  margin-bottom: 22px;
}

.stat-card {
  position: relative;
  padding: 20px;
  overflow: hidden;
  border: 1px solid #e5e9f0;
  border-radius: 14px;
  background: #fff;
}

.stat-card::before {
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: #3b82f6;
  content: '';
}

.stat-card.waiting::before {
  background: #f59e0b;
}

.stat-card.answered::before {
  background: #22a06b;
}

.stat-card.secret::before {
  background: #7c6ce7;
}

.stat-card span {
  color: #7c8697;
  font-size: 11px;
  font-weight: 700;
}

.stat-card strong {
  display: block;
  margin: 11px 0 5px;
  font-size: 27px;
}

.stat-card small {
  color: #a0a8b6;
  font-size: 10px;
}

.error-message {
  margin-bottom: 18px;
  padding: 13px 16px;
  border: 1px solid #fecaca;
  border-radius: 10px;
  background: #fff7f7;
  color: #b42318;
  font-size: 12px;
}

.qna-panel {
  overflow: hidden;
  border: 1px solid #e4e8ef;
  border-radius: 15px;
  background: #fff;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 20px 22px;
  border-bottom: 1px solid #edf0f4;
}

.panel-header h2 {
  margin: 0;
  font-size: 16px;
}

.panel-header p {
  margin: 5px 0 0;
  color: #98a1af;
  font-size: 10px;
}

.filters {
  display: flex;
  gap: 8px;
}

.search-box {
  position: relative;
}

.search-box span {
  position: absolute;
  top: 50%;
  left: 12px;
  color: #9ca5b4;
  transform: translateY(-50%);
}

.search-box input,
.filters select {
  height: 38px;
  border: 1px solid #dfe4eb;
  border-radius: 8px;
  outline: none;
  background: #fafbfd;
  color: #475267;
  font-size: 11px;
}

.search-box input {
  width: 220px;
  padding: 0 12px 0 34px;
}

.filters select {
  padding: 0 11px;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #fafbfd;
}

th {
  padding: 12px 19px;
  border-bottom: 1px solid #edf0f4;
  color: #8d96a5;
  font-size: 10px;
  text-align: left;
}

td {
  padding: 15px 19px;
  border-bottom: 1px solid #f0f2f5;
  color: #5b6679;
  font-size: 11px;
}

tbody tr {
  cursor: pointer;
  transition: .15s;
}

tbody tr:hover {
  background: #f8faff;
}

.title-cell {
  color: #293548;
  font-weight: 700;
}

.lock {
  margin-right: 5px;
  font-size: 10px;
}

.status-badge,
.category-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 8px;
  border-radius: 6px;
  font-size: 9px;
  font-weight: 800;
  white-space: nowrap;
}

.status-badge.waiting {
  background: #fff7e8;
  color: #b87300;
}

.status-badge.answered {
  background: #edf9f3;
  color: #21865a;
}

.category-badge {
  background: #f1f4f8;
  color: #647085;
}

.dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
}

.state-box {
  display: flex;
  min-height: 260px;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 7px;
  color: #7e8898;
}

.state-box strong {
  color: #455165;
  font-size: 12px;
}

.state-box span {
  font-size: 10px;
}

.spinner {
  width: 27px;
  height: 27px;
  margin-bottom: 7px;
  border: 3px solid #e8edf4;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin .7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-icon {
  display: flex;
  width: 38px;
  height: 38px;
  align-items: center;
  justify-content: center;
  margin-bottom: 5px;
  border-radius: 10px;
  background: #f1f4f8;
  font-weight: 800;
}

.mobile-list {
  display: none;
}

@media (max-width: 1100px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .panel-header {
    align-items: stretch;
    flex-direction: column;
  }

  .filters {
    width: 100%;
  }

  .search-box {
    flex: 1;
  }

  .search-box input {
    width: 100%;
  }
}

@media (max-width: 700px) {
  .qna-page {
    padding: 22px 15px 40px;
  }

  .page-header {
    flex-direction: column;
  }

  .page-header h1 {
    font-size: 25px;
  }

  .write-button {
    justify-content: center;
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 9px;
  }

  .stat-card {
    padding: 15px;
  }

  .stat-card strong {
    font-size: 22px;
  }

  .stat-card small {
    display: none;
  }

  .filters {
    flex-direction: column;
  }

  .filters select {
    width: 100%;
  }

  .table-wrapper {
    display: none;
  }

  .mobile-list {
    display: flex;
    flex-direction: column;
    gap: 9px;
    padding: 11px;
    background: #f7f9fc;
  }

  .mobile-card {
    padding: 15px;
    border: 1px solid #e6eaf0;
    border-radius: 11px;
    background: white;
    cursor: pointer;
  }

  .mobile-top {
    display: flex;
    gap: 6px;
  }

  .mobile-card h3 {
    margin: 13px 0;
    color: #273347;
    font-size: 13px;
  }

  .mobile-meta {
    display: flex;
    justify-content: space-between;
    color: #9aa3b1;
    font-size: 10px;
  }
}
</style>
