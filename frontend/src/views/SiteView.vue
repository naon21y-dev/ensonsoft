<script setup>
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
const errorMessage = ref('')


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
      '현장 정보를 불러오지 못했습니다.'

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
      '현장 검색에 실패했습니다.'
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
      '현장 상세 정보를 불러오지 못했습니다.'
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
      '변경할 상태를 선택해주세요.'
    )

    return
  }

  if (
    newStatus.value ===
    selectedSite.value.status
  ) {

    alert(
      '현재 상태와 동일한 상태입니다.'
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
      '현장 상태가 변경되었습니다.'
    )

  } catch (error) {

    console.error(
      '상태 변경 실패:',
      error
    )

    alert(
      error.response?.data?.message ||
      '상태 변경에 실패했습니다.'
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
      '현장 코드, 현장명, 주소는 필수입니다.'
    )

    return
  }

  createLoading.value = true

  try {

    await createSite(
      createForm.value
    )

    alert(
      '현장이 등록되었습니다.'
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
      '현장 등록에 실패했습니다.'
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
    NORMAL: '정상',
    WARNING: '주의',
    ERROR: '장애',
    MAINTENANCE: '점검중',
    INACTIVE: '운영중지'
  }

  return map[status] || status
}


const typeText = (type) => {

  const map = {
    TOLL_GATE: '요금소',
    TUNNEL: '터널',
    BRIDGE: '교량',
    ROAD: '도로',
    PARKING: '주차장',
    ETC: '기타'
  }

  return map[type] || type
}


const formatDate = (date) => {

  if (!date) {
    return '-'
  }

  return new Date(date)
    .toLocaleString('ko-KR')
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
          SITE MANAGEMENT
        </p>

        <h1>
          현장 관리
        </h1>

        <p class="header-description">
          스마트 모빌리티 현장의
          운영 상태와 변경 이력을 관리합니다.
        </p>
      </div>

      <button
        class="create-button"
        @click="showCreateModal = true"
      >
        + 현장 등록
      </button>

    </div>


    <!-- ======================================
         로딩
    ======================================= -->

    <div
      v-if="loading"
      class="state-box"
    >
      현장 정보를 불러오는 중입니다...
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
          <span>전체 현장</span>
          <strong>{{ totalCount }}</strong>
        </div>

        <div class="kpi-card">
          <span>정상 운영</span>
          <strong>{{ normalCount }}</strong>
        </div>

        <div class="kpi-card">
          <span>주의</span>
          <strong>{{ warningCount }}</strong>
        </div>

        <div class="kpi-card">
          <span>장애</span>
          <strong>{{ errorCount }}</strong>
        </div>

        <div class="kpi-card">
          <span>점검중</span>
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
            현장명
          </option>

          <option value="address">
            주소
          </option>
        </select>


        <input
          v-model="searchKeyword"
          class="control search-input"
          placeholder="검색어 입력"
          @keyup.enter="searchSiteData"
        />


        <select
          v-model="statusFilter"
          class="control"
          @change="typeFilter = 'ALL'"
        >
          <option value="ALL">
            전체 상태
          </option>

          <option value="NORMAL">
            정상
          </option>

          <option value="WARNING">
            주의
          </option>

          <option value="ERROR">
            장애
          </option>

          <option value="MAINTENANCE">
            점검중
          </option>

          <option value="INACTIVE">
            운영중지
          </option>
        </select>


        <select
          v-model="typeFilter"
          class="control"
          @change="statusFilter = 'ALL'"
        >
          <option value="ALL">
            전체 유형
          </option>

          <option value="TOLL_GATE">
            요금소
          </option>

          <option value="TUNNEL">
            터널
          </option>

          <option value="BRIDGE">
            교량
          </option>

          <option value="ROAD">
            도로
          </option>

          <option value="PARKING">
            주차장
          </option>

          <option value="ETC">
            기타
          </option>
        </select>


        <button
          class="search-button"
          :disabled="searchLoading"
          @click="searchSiteData"
        >
          {{ searchLoading ? '조회중...' : '조회' }}
        </button>


        <button
          class="reset-button"
          @click="resetSearch"
        >
          초기화
        </button>

      </div>


      <!-- ======================================
           현장 목록
      ======================================= -->

      <div class="table-card">

        <div class="table-header">

          <div>
            <h2>현장 목록</h2>

            <p>
              총 {{ safeSites.length }}개의 현장
            </p>
          </div>

        </div>


        <div class="table-wrapper">

          <table>

            <thead>
              <tr>
                <th>현장 코드</th>
                <th>현장명</th>
                <th>유형</th>
                <th>주소</th>
                <th>담당자</th>
                <th>상태</th>
                <th>관리</th>
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
                    상세
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
                  조회된 현장이 없습니다.
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
              SITE DETAIL
            </p>

            <h2>
              현장 상세
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
          상세 정보를 불러오는 중입니다...
        </div>


        <template
          v-else-if="selectedSite"
        >

          <!-- 현장 정보 -->

          <div class="detail-grid">

            <div class="detail-item">
              <span>현장 코드</span>
              <strong>
                {{ selectedSite.siteCode }}
              </strong>
            </div>

            <div class="detail-item">
              <span>현장명</span>
              <strong>
                {{ selectedSite.name }}
              </strong>
            </div>

            <div class="detail-item">
              <span>현장 유형</span>
              <strong>
                {{ typeText(selectedSite.siteType) }}
              </strong>
            </div>

            <div class="detail-item">
              <span>현재 상태</span>

              <strong>
                {{ statusText(selectedSite.status) }}
              </strong>
            </div>

            <div class="detail-item wide">
              <span>주소</span>
              <strong>
                {{ selectedSite.address }}
              </strong>
            </div>

            <div class="detail-item">
              <span>담당자</span>
              <strong>
                {{ selectedSite.managerName || '-' }}
              </strong>
            </div>

            <div class="detail-item">
              <span>연락처</span>
              <strong>
                {{ selectedSite.managerTel || '-' }}
              </strong>
            </div>

            <div class="detail-item wide">
              <span>현장 설명</span>
              <strong>
                {{ selectedSite.description || '-' }}
              </strong>
            </div>

            <div class="detail-item">
              <span>등록일</span>
              <strong>
                {{ formatDate(selectedSite.createdAt) }}
              </strong>
            </div>

            <div class="detail-item">
              <span>수정일</span>
              <strong>
                {{ formatDate(selectedSite.updatedAt) }}
              </strong>
            </div>

          </div>


          <!-- 상태 변경 -->

          <div class="status-section">

            <div class="section-title">
              <h3>현장 상태 변경</h3>
              <p>
                상태 변경 시 담당자와 변경 이력이 기록됩니다.
              </p>
            </div>


            <div class="status-form">

              <select
                v-model="newStatus"
                class="control"
              >
                <option value="">
                  변경 상태 선택
                </option>

                <option value="NORMAL">
                  정상
                </option>

                <option value="WARNING">
                  주의
                </option>

                <option value="ERROR">
                  장애
                </option>

                <option value="MAINTENANCE">
                  점검중
                </option>

                <option value="INACTIVE">
                  운영중지
                </option>
              </select>


              <input
                v-model="statusReason"
                class="control reason-input"
                placeholder="변경 사유 입력"
              />


              <button
                class="change-button"
                :disabled="statusLoading"
                @click="changeStatus"
              >
                {{
                  statusLoading
                    ? '변경중...'
                    : '상태 변경'
                }}
              </button>

            </div>

          </div>


          <!-- 이력 -->

          <div class="history-section">

            <div class="section-title">

              <h3>
                상태 변경 이력
              </h3>

              <p>
                최근 변경 내역부터 표시됩니다.
              </p>

            </div>


            <div
              v-if="safeHistories.length === 0"
              class="history-empty"
            >
              상태 변경 이력이 없습니다.
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
                    변경자:
                    <strong>
                      {{ history.changedBy }}
                    </strong>
                  </p>


                  <p>
                    변경 사유:
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
              CREATE SITE
            </p>

            <h2>
              현장 등록
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
            <span>현장 코드 *</span>

            <input
              v-model="createForm.siteCode"
              class="control"
              placeholder="SITE-002"
            />
          </label>


          <label>
            <span>현장명 *</span>

            <input
              v-model="createForm.name"
              class="control"
              placeholder="현장명"
            />
          </label>


          <label class="full">
            <span>주소 *</span>

            <input
              v-model="createForm.address"
              class="control"
              placeholder="현장 주소"
            />
          </label>


          <label>
            <span>현장 유형 *</span>

            <select
              v-model="createForm.siteType"
              class="control"
            >
              <option value="TOLL_GATE">
                요금소
              </option>

              <option value="TUNNEL">
                터널
              </option>

              <option value="BRIDGE">
                교량
              </option>

              <option value="ROAD">
                도로
              </option>

              <option value="PARKING">
                주차장
              </option>

              <option value="ETC">
                기타
              </option>
            </select>
          </label>


          <label>
            <span>담당자</span>

            <input
              v-model="createForm.managerName"
              class="control"
              placeholder="담당자명"
            />
          </label>


          <label>
            <span>담당자 연락처</span>

            <input
              v-model="createForm.managerTel"
              class="control"
              placeholder="010-0000-0000"
            />
          </label>


          <label class="full">
            <span>현장 설명</span>

            <textarea
              v-model="createForm.description"
              class="control textarea"
              placeholder="현장 설명"
            ></textarea>
          </label>

        </div>


        <div class="modal-actions">

          <button
            class="cancel-button"
            @click="showCreateModal = false"
          >
            취소
          </button>

          <button
            class="create-button"
            :disabled="createLoading"
            @click="submitCreate"
          >
            {{
              createLoading
                ? '등록중...'
                : '현장 등록'
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