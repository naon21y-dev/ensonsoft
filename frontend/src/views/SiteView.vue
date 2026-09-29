<script setup>
import { useI18n } from 'vue-i18n'
import { useUiMessage, useFormatLocale } from '../i18n'
const { t } = useI18n()
const formatLocale = useFormatLocale()

import { computed, onMounted, ref } from 'vue'

import {
  getSites,
  getSite,
  getSiteHistories,
  getSitesByStatus,
  getSitesByType,
  searchSitesByName,
  searchSitesByAddress,
  createSite,
  updateSiteStatus
} from '../api/Site'


// ========================================
// 기본 상태
// ========================================

const sites = ref([])
const loading = ref(true)
const errorMessage = useUiMessage()


// ========================================
// 검색 / 필터
// ========================================

const searchType = ref('name')
const searchKeyword = ref('')
const statusFilter = ref('ALL')
const typeFilter = ref('ALL')
const searchLoading = ref(false)


// ========================================
// 상세 모달
// ========================================

const showDetailModal = ref(false)
const selectedSite = ref(null)
const histories = ref([])
const detailLoading = ref(false)


// ========================================
// 상태 변경
// ========================================

const newStatus = ref('')
const statusReason = ref('')
const statusLoading = ref(false)


// ========================================
// 등록 모달
// ========================================

const showCreateModal = ref(false)
const createLoading = ref(false)

const createForm = ref({
  siteCode: '',
  name: '',
  address: '',
  description: '',
  siteType: 'TOLL_GATE',
  managerName: '',
  managerTel: ''
})


// ========================================
// 안전한 배열
// ========================================

const safeSites = computed(() =>
  Array.isArray(sites.value)
    ? sites.value
    : []
)

const safeHistories = computed(() =>
  Array.isArray(histories.value)
    ? histories.value
    : []
)


// ========================================
// KPI
// ========================================

const totalCount = computed(() =>
  safeSites.value.length
)

const normalCount = computed(() =>
  safeSites.value.filter(
    site => site.status === 'NORMAL'
  ).length
)

const warningCount = computed(() =>
  safeSites.value.filter(
    site => site.status === 'WARNING'
  ).length
)

const errorCount = computed(() =>
  safeSites.value.filter(
    site => site.status === 'ERROR'
  ).length
)

const maintenanceCount = computed(() =>
  safeSites.value.filter(
    site => site.status === 'MAINTENANCE'
  ).length
)


// ========================================
// 전체 현장 조회
// ========================================

const loadSites = async () => {

  loading.value = true
  errorMessage.value = ''

  try {

    const response = await getSites()

    sites.value =
      Array.isArray(response.data)
        ? response.data
        : []

  } catch (error) {

    console.error(
      '현장 조회 실패:',
      error
    )

    errorMessage.value =
      error.response?.data?.message ||
      t('m403')

  } finally {

    loading.value = false
  }
}


// ========================================
// 검색
// ========================================

const searchSiteData = async () => {

  searchLoading.value = true

  try {

    let response

    // 상태 필터
    if (statusFilter.value !== 'ALL') {

      response =
        await getSitesByStatus(
          statusFilter.value
        )

    }

    // 유형 필터
    else if (typeFilter.value !== 'ALL') {

      response =
        await getSitesByType(
          typeFilter.value
        )

    }

    // 검색어
    else if (searchKeyword.value.trim()) {

      if (searchType.value === 'address') {

        response =
          await searchSitesByAddress(
            searchKeyword.value.trim()
          )

      } else {

        response =
          await searchSitesByName(
            searchKeyword.value.trim()
          )
      }

    }

    // 조건 없음
    else {

      response =
        await getSites()
    }

    sites.value =
      Array.isArray(response.data)
        ? response.data
        : []

  } catch (error) {

    console.error(
      '현장 검색 실패:',
      error
    )

    alert(
      error.response?.data?.message ||
      t('m404')
    )

  } finally {

    searchLoading.value = false
  }
}


// ========================================
// 검색 초기화
// ========================================

const resetSearch = async () => {

  searchType.value = 'name'
  searchKeyword.value = ''
  statusFilter.value = 'ALL'
  typeFilter.value = 'ALL'

  await loadSites()
}


// ========================================
// 상세 모달 열기
// ========================================

const openDetail = async (site) => {

  showDetailModal.value = true
  selectedSite.value = site
  histories.value = []

  newStatus.value = ''
  statusReason.value = ''

  detailLoading.value = true

  try {

    const [
      detailResponse,
      historyResponse
    ] = await Promise.all([
      getSite(site.id),
      getSiteHistories(site.id)
    ])

    selectedSite.value =
      detailResponse.data

    histories.value =
      Array.isArray(historyResponse.data)
        ? historyResponse.data
        : []

  } catch (error) {

    console.error(
      '현장 상세 조회 실패:',
      error
    )

    alert(
      error.response?.data?.message ||
      t('m405')
    )

  } finally {

    detailLoading.value = false
  }
}


// ========================================
// 상세 모달 닫기
// ========================================

const closeDetail = () => {

  showDetailModal.value = false
  selectedSite.value = null
  histories.value = []

  newStatus.value = ''
  statusReason.value = ''
}


// ========================================
// 이력 다시 조회
// ========================================

const reloadHistories = async () => {

  if (!selectedSite.value) {
    return
  }

  try {

    const response =
      await getSiteHistories(
        selectedSite.value.id
      )

    histories.value =
      Array.isArray(response.data)
        ? response.data
        : []

  } catch (error) {

    console.error(
      '이력 조회 실패:',
      error
    )
  }
}


// ========================================
// 목록 데이터 수정
// ========================================

const updateSiteInList = (
  updatedSite
) => {

  const index =
    sites.value.findIndex(
      site =>
        site.id === updatedSite.id
    )

  if (index !== -1) {

    sites.value[index] =
      updatedSite
  }
}


// ========================================
// 상태 변경
// ========================================

const changeStatus = async () => {

  if (!selectedSite.value) {
    return
  }

  if (!newStatus.value) {

    alert(
      t('m406')
    )

    return
  }

  if (
    newStatus.value ===
    selectedSite.value.status
  ) {

    alert(
      t('m407')
    )

    return
  }

  statusLoading.value = true

  try {

    const response =
      await updateSiteStatus(
        selectedSite.value.id,
        newStatus.value,
        statusReason.value
      )

    selectedSite.value =
      response.data

    updateSiteInList(
      response.data
    )

    await reloadHistories()

    newStatus.value = ''
    statusReason.value = ''

    alert(
      t('m408')
    )

  } catch (error) {

    console.error(
      '상태 변경 실패:',
      error
    )

    alert(
      error.response?.data?.message ||
      t('m171')
    )

  } finally {

    statusLoading.value = false
  }
}


// ========================================
// 현장 등록
// ========================================

const submitCreate = async () => {

  if (
    !createForm.value.siteCode ||
    !createForm.value.name ||
    !createForm.value.address
  ) {

    alert(
      t('m409')
    )

    return
  }

  createLoading.value = true

  try {

    await createSite(
      createForm.value
    )

    alert(
      t('m410')
    )

    showCreateModal.value = false

    createForm.value = {
      siteCode: '',
      name: '',
      address: '',
      description: '',
      siteType: 'TOLL_GATE',
      managerName: '',
      managerTel: ''
    }

    await loadSites()

  } catch (error) {

    console.error(
      '현장 등록 실패:',
      error
    )

    alert(
      error.response?.data?.message ||
      t('m411')
    )

  } finally {

    createLoading.value = false
  }
}


// ========================================
// 표시용 함수
// ========================================

const statusText = (status) => {

  const map = {
    NORMAL: t('m081'),
    WARNING: t('m082'),
    ERROR: t('m083'),
    MAINTENANCE: t('m084'),
    INACTIVE: t('m412')
  }

  return map[status] || status
}


const typeText = (type) => {

  const map = {
    TOLL_GATE: t('m413'),
    TUNNEL: t('m414'),
    BRIDGE: t('m415'),
    ROAD: t('m416'),
    PARKING: t('m417'),
    ETC: t('m096')
  }

  return map[type] || type
}


const formatDate = (date) => {

  if (!date) {
    return '-'
  }

  return new Date(date)
    .toLocaleString(formatLocale.value)
}


// ========================================
// 시작
// ========================================

onMounted(() => {
  loadSites()
})
</script>


<template>

  <div class="site-page">

    <!-- ======================================
         상단
    ======================================= -->

    <div class="page-header">

      <div>
        <p class="eyebrow">
          {{ t('m418') }}
        </p>

        <h1>
          {{ t('m215') }}
        </h1>

        <p class="header-description">
          {{ t('m419') }}
        </p>
      </div>

      <button
        class="create-button"
        @click="showCreateModal = true"
      >
        {{ t('m420') }}
      </button>

    </div>


    <!-- ======================================
         로딩
    ======================================= -->

    <div
      v-if="loading"
      class="state-box"
    >
      {{ t('m421') }}
    </div>


    <!-- ======================================
         에러
    ======================================= -->

    <div
      v-else-if="errorMessage"
      class="state-box error"
    >
      {{ errorMessage }}
    </div>


    <template v-else>

      <!-- ======================================
           KPI
      ======================================= -->

      <div class="kpi-grid">

        <div class="kpi-card">
          <span>{{ t('m099') }}</span>
          <strong>{{ totalCount }}</strong>
        </div>

        <div class="kpi-card">
          <span>{{ t('m422') }}</span>
          <strong>{{ normalCount }}</strong>
        </div>

        <div class="kpi-card">
          <span>{{ t('m082') }}</span>
          <strong>{{ warningCount }}</strong>
        </div>

        <div class="kpi-card">
          <span>{{ t('m083') }}</span>
          <strong>{{ errorCount }}</strong>
        </div>

        <div class="kpi-card">
          <span>{{ t('m084') }}</span>
          <strong>{{ maintenanceCount }}</strong>
        </div>

      </div>


      <!-- ======================================
           검색
      ======================================= -->

      <div class="search-panel">

        <select
          v-model="searchType"
          class="control"
        >
          <option value="name">
            {{ t('m423') }}
          </option>

          <option value="address">
            {{ t('m424') }}
          </option>
        </select>


        <input
          v-model="searchKeyword"
          class="control search-input"
          :placeholder="t('m425')"
          @keyup.enter="searchSiteData"
        />


        <select
          v-model="statusFilter"
          class="control"
          @change="typeFilter = 'ALL'"
        >
          <option value="ALL">
            {{ t('m184') }}
          </option>

          <option value="NORMAL">
            {{ t('m081') }}
          </option>

          <option value="WARNING">
            {{ t('m082') }}
          </option>

          <option value="ERROR">
            {{ t('m083') }}
          </option>

          <option value="MAINTENANCE">
            {{ t('m084') }}
          </option>

          <option value="INACTIVE">
            {{ t('m412') }}
          </option>
        </select>


        <select
          v-model="typeFilter"
          class="control"
          @change="statusFilter = 'ALL'"
        >
          <option value="ALL">
            {{ t('m186') }}
          </option>

          <option value="TOLL_GATE">
            {{ t('m413') }}
          </option>

          <option value="TUNNEL">
            {{ t('m414') }}
          </option>

          <option value="BRIDGE">
            {{ t('m415') }}
          </option>

          <option value="ROAD">
            {{ t('m416') }}
          </option>

          <option value="PARKING">
            {{ t('m417') }}
          </option>

          <option value="ETC">
            {{ t('m096') }}
          </option>
        </select>


        <button
          class="search-button"
          :disabled="searchLoading"
          @click="searchSiteData"
        >
          {{ searchLoading ? t('m359') : t('m426') }}
        </button>


        <button
          class="reset-button"
          @click="resetSearch"
        >
          {{ t('m061') }}
        </button>

      </div>


      <!-- ======================================
           현장 목록
      ======================================= -->

      <div class="table-card">

        <div class="table-header">

          <div>
            <h2>{{ t('m427') }}</h2>

            <p>
              {{ t('counts.sites', safeSites.length) }}
            </p>
          </div>

        </div>


        <div class="table-wrapper">

          <table>

            <thead>
              <tr>
                <th>{{ t('m430') }}</th>
                <th>{{ t('m423') }}</th>
                <th>{{ t('m185') }}</th>
                <th>{{ t('m424') }}</th>
                <th>{{ t('m385') }}</th>
                <th>{{ t('m183') }}</th>
                <th>{{ t('m431') }}</th>
              </tr>
            </thead>

            <tbody>

              <tr
                v-for="site in safeSites"
                :key="site.id"
              >

                <td class="site-code">
                  {{ site.siteCode }}
                </td>

                <td>
                  <strong>
                    {{ site.name }}
                  </strong>
                </td>

                <td>
                  {{ typeText(site.siteType) }}
                </td>

                <td>
                  {{ site.address }}
                </td>

                <td>
                  {{ site.managerName || '-' }}
                </td>

                <td>

                  <span
                    class="status-badge"
                    :class="
                      'status-' +
                      site.status.toLowerCase()
                    "
                  >
                    {{ statusText(site.status) }}
                  </span>

                </td>

                <td>

                  <button
                    class="detail-button"
                    @click="openDetail(site)"
                  >
                    {{ t('m192') }}
                  </button>

                </td>

              </tr>


              <tr
                v-if="safeSites.length === 0"
              >

                <td
                  colspan="7"
                  class="empty"
                >
                  {{ t('m432') }}
                </td>

              </tr>

            </tbody>

          </table>

        </div>

      </div>

    </template>


    <!-- ======================================
         상세 모달
    ======================================= -->

    <div
      v-if="showDetailModal"
      class="modal-overlay"
      @click.self="closeDetail"
    >

      <div class="modal">

        <div class="modal-header">

          <div>
            <p class="modal-eyebrow">
              {{ t('m433') }}
            </p>

            <h2>
              {{ t('m434') }}
            </h2>
          </div>

          <button
            class="close-button"
            @click="closeDetail"
          >
            ×
          </button>

        </div>


        <div
          v-if="detailLoading"
          class="modal-loading"
        >
          {{ t('m197') }}
        </div>


        <template
          v-else-if="selectedSite"
        >

          <!-- 현장 정보 -->

          <div class="detail-grid">

            <div class="detail-item">
              <span>{{ t('m430') }}</span>
              <strong>
                {{ selectedSite.siteCode }}
              </strong>
            </div>

            <div class="detail-item">
              <span>{{ t('m423') }}</span>
              <strong>
                {{ selectedSite.name }}
              </strong>
            </div>

            <div class="detail-item">
              <span>{{ t('m435') }}</span>
              <strong>
                {{ typeText(selectedSite.siteType) }}
              </strong>
            </div>

            <div class="detail-item">
              <span>{{ t('m199') }}</span>

              <strong>
                {{ statusText(selectedSite.status) }}
              </strong>
            </div>

            <div class="detail-item wide">
              <span>{{ t('m424') }}</span>
              <strong>
                {{ selectedSite.address }}
              </strong>
            </div>

            <div class="detail-item">
              <span>{{ t('m385') }}</span>
              <strong>
                {{ selectedSite.managerName || '-' }}
              </strong>
            </div>

            <div class="detail-item">
              <span>{{ t('m436') }}</span>
              <strong>
                {{ selectedSite.managerTel || '-' }}
              </strong>
            </div>

            <div class="detail-item wide">
              <span>{{ t('m437') }}</span>
              <strong>
                {{ selectedSite.description || '-' }}
              </strong>
            </div>

            <div class="detail-item">
              <span>{{ t('m438') }}</span>
              <strong>
                {{ formatDate(selectedSite.createdAt) }}
              </strong>
            </div>

            <div class="detail-item">
              <span>{{ t('m027') }}</span>
              <strong>
                {{ formatDate(selectedSite.updatedAt) }}
              </strong>
            </div>

          </div>


          <!-- 상태 변경 -->

          <div class="status-section">

            <div class="section-title">
              <h3>{{ t('m439') }}</h3>
              <p>
                {{ t('m440') }}
              </p>
            </div>


            <div class="status-form">

              <select
                v-model="newStatus"
                class="control"
              >
                <option value="">
                  {{ t('m441') }}
                </option>

                <option value="NORMAL">
                  {{ t('m081') }}
                </option>

                <option value="WARNING">
                  {{ t('m082') }}
                </option>

                <option value="ERROR">
                  {{ t('m083') }}
                </option>

                <option value="MAINTENANCE">
                  {{ t('m084') }}
                </option>

                <option value="INACTIVE">
                  {{ t('m412') }}
                </option>
              </select>


              <input
                v-model="statusReason"
                class="control reason-input"
                :placeholder="t('m442')"
              />


              <button
                class="change-button"
                :disabled="statusLoading"
                @click="changeStatus"
              >
                {{
                  statusLoading
                    ? t('m443')
                    : t('m204')
                }}
              </button>

            </div>

          </div>


          <!-- 이력 -->

          <div class="history-section">

            <div class="section-title">

              <h3>
                {{ t('m209') }}
              </h3>

              <p>
                {{ t('m444') }}
              </p>

            </div>


            <div
              v-if="safeHistories.length === 0"
              class="history-empty"
            >
              {{ t('m210') }}
            </div>


            <div
              v-else
              class="timeline"
            >

              <div
                v-for="history in safeHistories"
                :key="history.id"
                class="timeline-item"
              >

                <div class="timeline-dot"></div>


                <div class="timeline-content">

                  <div class="timeline-top">

                    <strong>
                      {{ statusText(history.previousStatus) }}
                      →
                      {{ statusText(history.newStatus) }}
                    </strong>

                    <span>
                      {{ formatDate(history.changedAt) }}
                    </span>

                  </div>


                  <p>
                    {{ t('m211') }}
                    <strong>
                      {{ history.changedBy }}
                    </strong>
                  </p>


                  <p>
                    {{ t('m445') }}
                    {{ history.reason || '-' }}
                  </p>

                </div>

              </div>

            </div>

          </div>

        </template>

      </div>

    </div>


    <!-- ======================================
         현장 등록 모달
    ======================================= -->

    <div
      v-if="showCreateModal"
      class="modal-overlay"
      @click.self="showCreateModal = false"
    >

      <div class="modal create-modal">

        <div class="modal-header">

          <div>
            <p class="modal-eyebrow">
              {{ t('m446') }}
            </p>

            <h2>
              {{ t('m447') }}
            </h2>
          </div>

          <button
            class="close-button"
            @click="showCreateModal = false"
          >
            ×
          </button>

        </div>


        <div class="form-grid">

          <label>
            <span>{{ t('m448') }}</span>

            <input
              v-model="createForm.siteCode"
              class="control"
              :placeholder="t('m449')"
            />
          </label>


          <label>
            <span>{{ t('m450') }}</span>

            <input
              v-model="createForm.name"
              class="control"
              :placeholder="t('m423')"
            />
          </label>


          <label class="full">
            <span>{{ t('m451') }}</span>

            <input
              v-model="createForm.address"
              class="control"
              :placeholder="t('m452')"
            />
          </label>


          <label>
            <span>{{ t('m453') }}</span>

            <select
              v-model="createForm.siteType"
              class="control"
            >
              <option value="TOLL_GATE">
                {{ t('m413') }}
              </option>

              <option value="TUNNEL">
                {{ t('m414') }}
              </option>

              <option value="BRIDGE">
                {{ t('m415') }}
              </option>

              <option value="ROAD">
                {{ t('m416') }}
              </option>

              <option value="PARKING">
                {{ t('m417') }}
              </option>

              <option value="ETC">
                {{ t('m096') }}
              </option>
            </select>
          </label>


          <label>
            <span>{{ t('m385') }}</span>

            <input
              v-model="createForm.managerName"
              class="control"
              :placeholder="t('m454')"
            />
          </label>


          <label>
            <span>{{ t('m455') }}</span>

            <input
              v-model="createForm.managerTel"
              class="control"
              placeholder="010-0000-0000"
            />
          </label>


          <label class="full">
            <span>{{ t('m437') }}</span>

            <textarea
              v-model="createForm.description"
              class="control textarea"
              :placeholder="t('m437')"
            ></textarea>
          </label>

        </div>


        <div class="modal-actions">

          <button
            class="cancel-button"
            @click="showCreateModal = false"
          >
            {{ t('m050') }}
          </button>

          <button
            class="create-button"
            :disabled="createLoading"
            @click="submitCreate"
          >
            {{
              createLoading
                ? t('m456')
                : t('m447')
            }}
          </button>

        </div>

      </div>

    </div>

  </div>

</template>


<style scoped>

* {
  box-sizing: border-box;
}

.site-page {
  min-height: 100vh;
  padding: 36px;
  background: #f5f7fb;
  color: #172033;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 28px;
}

.eyebrow,
.modal-eyebrow {
  margin: 0 0 6px;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 1.8px;
  color: #64748b;
}

.page-header h1 {
  margin: 0;
  font-size: 32px;
}

.header-description {
  margin: 8px 0 0;
  color: #64748b;
}

.create-button,
.search-button,
.change-button {
  border: 0;
  border-radius: 10px;
  padding: 12px 18px;
  background: #172033;
  color: white;
  font-weight: 700;
  cursor: pointer;
}

.create-button:disabled,
.search-button:disabled,
.change-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.state-box {
  padding: 40px;
  border-radius: 14px;
  background: white;
  text-align: center;
}

.state-box.error {
  color: #b91c1c;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}

.kpi-card {
  padding: 20px;
  border: 1px solid #e6eaf0;
  border-radius: 14px;
  background: white;
}

.kpi-card span {
  display: block;
  margin-bottom: 8px;
  color: #64748b;
  font-size: 13px;
}

.kpi-card strong {
  font-size: 28px;
}

.search-panel {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  padding: 16px;
  border: 1px solid #e6eaf0;
  border-radius: 14px;
  background: white;
}

.control {
  width: 100%;
  min-height: 42px;
  padding: 10px 12px;
  border: 1px solid #dce2ea;
  border-radius: 9px;
  background: white;
  outline: none;
}

.control:focus {
  border-color: #64748b;
}

.search-input {
  flex: 1;
}

.reset-button,
.cancel-button {
  border: 1px solid #dce2ea;
  border-radius: 10px;
  padding: 10px 16px;
  background: white;
  cursor: pointer;
}

.table-card {
  overflow: hidden;
  border: 1px solid #e6eaf0;
  border-radius: 14px;
  background: white;
}

.table-header {
  padding: 20px;
  border-bottom: 1px solid #edf0f4;
}

.table-header h2 {
  margin: 0;
  font-size: 18px;
}

.table-header p {
  margin: 5px 0 0;
  color: #64748b;
  font-size: 13px;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  padding: 13px 16px;
  background: #f8fafc;
  color: #64748b;
  text-align: left;
  font-size: 12px;
}

td {
  padding: 15px 16px;
  border-top: 1px solid #edf0f4;
  font-size: 14px;
}

tbody tr:hover {
  background: #fafbfd;
}

.site-code {
  font-weight: 800;
}

.status-badge {
  display: inline-flex;
  padding: 6px 9px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
}

.status-normal {
  background: #dcfce7;
  color: #166534;
}

.status-warning {
  background: #fef3c7;
  color: #92400e;
}

.status-error {
  background: #fee2e2;
  color: #991b1b;
}

.status-maintenance {
  background: #dbeafe;
  color: #1e40af;
}

.status-inactive {
  background: #e5e7eb;
  color: #374151;
}

.detail-button {
  border: 1px solid #dce2ea;
  border-radius: 8px;
  padding: 7px 12px;
  background: white;
  cursor: pointer;
}

.empty {
  padding: 50px;
  color: #94a3b8;
  text-align: center;
}

.modal-overlay {
  position: fixed;
  z-index: 1000;
  inset: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30px;
  background: rgba(15, 23, 42, 0.55);
}

.modal {
  width: min(900px, 100%);
  max-height: 90vh;
  overflow-y: auto;
  padding: 26px;
  border-radius: 18px;
  background: white;
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.22);
}

.create-modal {
  width: min(720px, 100%);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.modal-header h2 {
  margin: 0;
}

.close-button {
  border: 0;
  background: transparent;
  font-size: 30px;
  cursor: pointer;
}

.modal-loading {
  padding: 60px;
  text-align: center;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.detail-item {
  padding: 14px;
  border: 1px solid #e7ebf0;
  border-radius: 10px;
  background: #fafbfd;
}

.detail-item.wide {
  grid-column: 1 / -1;
}

.detail-item span {
  display: block;
  margin-bottom: 6px;
  color: #64748b;
  font-size: 12px;
}

.status-section,
.history-section {
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #e7ebf0;
}

.section-title h3 {
  margin: 0;
  font-size: 17px;
}

.section-title p {
  margin: 5px 0 15px;
  color: #64748b;
  font-size: 13px;
}

.status-form {
  display: grid;
  grid-template-columns: 180px 1fr auto;
  gap: 10px;
}

.timeline {
  margin-top: 18px;
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 15px;
  padding: 0 0 22px 5px;
}

.timeline-item:not(:last-child)::before {
  content: '';
  position: absolute;
  top: 13px;
  left: 10px;
  width: 2px;
  height: calc(100% - 2px);
  background: #e2e8f0;
}

.timeline-dot {
  z-index: 1;
  flex: 0 0 12px;
  width: 12px;
  height: 12px;
  margin-top: 5px;
  border-radius: 50%;
  background: #172033;
}

.timeline-content {
  flex: 1;
  padding: 14px;
  border: 1px solid #e7ebf0;
  border-radius: 10px;
}

.timeline-top {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.timeline-top span {
  color: #64748b;
  font-size: 12px;
}

.timeline-content p {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 13px;
}

.history-empty {
  padding: 30px;
  border-radius: 10px;
  background: #f8fafc;
  color: #94a3b8;
  text-align: center;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.form-grid label {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.form-grid label span {
  font-size: 13px;
  font-weight: 700;
}

.form-grid .full {
  grid-column: 1 / -1;
}

.textarea {
  min-height: 100px;
  resize: vertical;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 24px;
}

@media (max-width: 900px) {

  .site-page {
    padding: 20px;
  }

  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .search-panel {
    flex-direction: column;
  }

  .status-form {
    grid-template-columns: 1fr;
  }

}

@media (max-width: 600px) {

  .page-header {
    flex-direction: column;
  }

  .kpi-grid {
    grid-template-columns: 1fr;
  }

  .detail-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }

  .detail-item.wide,
  .form-grid .full {
    grid-column: auto;
  }

}

</style>