<script setup>
import { computed, onMounted, ref } from 'vue'

import {
  getVehicles,
  getEvents,
  getEvent,
  getEventHistories,
  startEventProcessing,
  completeEventProcessing,
  searchVehicles,
  searchVehiclesByLocation,
  getVehiclesByStatus,
  getEventsByStatus,
  getEventsByType,
  getEventsBySeverity,
  getEventsByLocation
} from '../api/Monitoring'


// ========================================
// 기본 상태
// ========================================

const vehicles = ref([])
const events = ref([])

const loading = ref(true)
const errorMessage = ref('')


// ========================================
// 이벤트 탭
// ========================================

const activeTab = ref('events')


// ========================================
// 이벤트 검색 / 필터
// ========================================

const eventLocationSearch = ref('')
const eventTypeFilter = ref('ALL')
const eventSeverityFilter = ref('ALL')
const eventSearchLoading = ref(false)


// ========================================
// 차량 검색
// ========================================

const plateSearch = ref('')
const locationSearch = ref('')
const vehicleStatusFilter = ref('ALL')
const vehicleSearchLoading = ref(false)


// ========================================
// 이벤트 상세 모달
// ========================================

const showEventModal = ref(false)
const selectedEvent = ref(null)
const eventHistories = ref([])

const detailLoading = ref(false)
const historyLoading = ref(false)
const eventActionLoading = ref(false)


// ========================================
// 안전한 배열
// ========================================

const safeVehicles = computed(() => {
  return Array.isArray(vehicles.value)
    ? vehicles.value
    : []
})

const safeEvents = computed(() => {
  return Array.isArray(events.value)
    ? events.value
    : []
})

const safeHistories = computed(() => {
  return Array.isArray(eventHistories.value)
    ? eventHistories.value
    : []
})


// ========================================
// 통합관제 데이터 조회
// ========================================

const loadMonitoringData = async () => {

  loading.value = true
  errorMessage.value = ''

  try {

    const [
      vehicleResponse,
      eventResponse
    ] = await Promise.all([
      getVehicles(),
      getEvents()
    ])

    vehicles.value =
      Array.isArray(vehicleResponse.data)
        ? vehicleResponse.data
        : []

    events.value =
      Array.isArray(eventResponse.data)
        ? eventResponse.data
        : []

  } catch (error) {

    console.error(
      '통합관제 데이터 조회 실패:',
      error
    )

    vehicles.value = []
    events.value = []

    errorMessage.value =
      error.response?.data?.message ||
      '통합관제 데이터를 불러오지 못했습니다.'

  } finally {

    loading.value = false

  }
}


// ========================================
// KPI
// ========================================

const totalVehicles = computed(() =>
  safeVehicles.value.length
)

const totalEvents = computed(() =>
  safeEvents.value.length
)

const unprocessedEvents = computed(() =>
  safeEvents.value.filter(
    event =>
      event.status === 'UNPROCESSED'
  ).length
)

const processingEvents = computed(() =>
  safeEvents.value.filter(
    event =>
      event.status === 'PROCESSING'
  ).length
)

const criticalEvents = computed(() =>
  safeEvents.value.filter(
    event =>
      event.severity === 'CRITICAL'
  ).length
)


// ========================================
// 화면에 표시할 이벤트
// ========================================

const displayedEvents = computed(() => {

  return safeEvents.value.filter(event => {

    if (
      activeTab.value === 'unprocessed' &&
      event.status !== 'UNPROCESSED'
    ) {
      return false
    }

    if (
      activeTab.value === 'processing' &&
      event.status !== 'PROCESSING'
    ) {
      return false
    }

    if (
      activeTab.value === 'completed' &&
      event.status !== 'COMPLETED'
    ) {
      return false
    }

    return true
  })
})


// ========================================
// 이벤트 검색
// ========================================

const searchEventData = async () => {

  eventSearchLoading.value = true

  try {

    let response

    if (eventLocationSearch.value.trim()) {

      response = await getEventsByLocation(
        eventLocationSearch.value.trim()
      )

    } else if (
      eventTypeFilter.value !== 'ALL'
    ) {

      response = await getEventsByType(
        eventTypeFilter.value
      )

    } else if (
      eventSeverityFilter.value !== 'ALL'
    ) {

      response = await getEventsBySeverity(
        eventSeverityFilter.value
      )

    } else {

      response = await getEvents()
    }

    events.value =
      Array.isArray(response.data)
        ? response.data
        : []

    activeTab.value = 'events'

  } catch (error) {

    console.error(
      '이벤트 검색 실패:',
      error
    )

    alert(
      error.response?.data?.message ||
      '이벤트 데이터를 조회하지 못했습니다.'
    )

  } finally {

    eventSearchLoading.value = false

  }
}


// ========================================
// 이벤트 검색 초기화
// ========================================

const resetEventSearch = async () => {

  eventLocationSearch.value = ''
  eventTypeFilter.value = 'ALL'
  eventSeverityFilter.value = 'ALL'
  activeTab.value = 'events'

  try {

    const response = await getEvents()

    events.value =
      Array.isArray(response.data)
        ? response.data
        : []

  } catch (error) {

    console.error(
      '이벤트 초기화 실패:',
      error
    )
  }
}


// ========================================
// 차량 검색
// ========================================

const searchVehicleData = async () => {

  vehicleSearchLoading.value = true

  try {

    let response

    if (plateSearch.value.trim()) {

      response = await searchVehicles(
        plateSearch.value.trim()
      )

    } else if (
      locationSearch.value.trim()
    ) {

      response =
        await searchVehiclesByLocation(
          locationSearch.value.trim()
        )

    } else if (
      vehicleStatusFilter.value !== 'ALL'
    ) {

      response =
        await getVehiclesByStatus(
          vehicleStatusFilter.value
        )

    } else {

      response = await getVehicles()
    }

    vehicles.value =
      Array.isArray(response.data)
        ? response.data
        : []

  } catch (error) {

    console.error(
      '차량 검색 실패:',
      error
    )

    alert(
      error.response?.data?.message ||
      '차량 데이터를 조회하지 못했습니다.'
    )

  } finally {

    vehicleSearchLoading.value = false

  }
}


// ========================================
// 차량 검색 초기화
// ========================================

const resetVehicleSearch = async () => {

  plateSearch.value = ''
  locationSearch.value = ''
  vehicleStatusFilter.value = 'ALL'

  try {

    const response = await getVehicles()

    vehicles.value =
      Array.isArray(response.data)
        ? response.data
        : []

  } catch (error) {

    console.error(
      '차량 데이터 조회 실패:',
      error
    )
  }
}


// ========================================
// 이벤트 상세 모달 열기
// ========================================

const openEventDetail = async (event) => {

  showEventModal.value = true

  selectedEvent.value = event

  eventHistories.value = []

  detailLoading.value = true
  historyLoading.value = true

  try {

    const [
      detailResponse,
      historyResponse
    ] = await Promise.all([
      getEvent(event.id),
      getEventHistories(event.id)
    ])

    selectedEvent.value =
      detailResponse.data || event

    eventHistories.value =
      Array.isArray(historyResponse.data)
        ? historyResponse.data
        : []

  } catch (error) {

    console.error(
      '이벤트 상세 조회 실패:',
      error
    )

    alert(
      error.response?.data?.message ||
      '이벤트 상세 정보를 불러오지 못했습니다.'
    )

  } finally {

    detailLoading.value = false
    historyLoading.value = false

  }
}


// ========================================
// 상세 모달 닫기
// ========================================

const closeEventDetail = () => {

  showEventModal.value = false

  selectedEvent.value = null

  eventHistories.value = []
}


// ========================================
// 처리 이력 다시 조회
// ========================================

const reloadEventHistories = async (id) => {

  historyLoading.value = true

  try {

    const response =
      await getEventHistories(id)

    eventHistories.value =
      Array.isArray(response.data)
        ? response.data
        : []

  } catch (error) {

    console.error(
      '처리 이력 조회 실패:',
      error
    )

  } finally {

    historyLoading.value = false

  }
}


// ========================================
// 목록 이벤트 상태 업데이트
// ========================================

const updateEventInList = (updatedEvent) => {

  if (!updatedEvent) {
    return
  }

  const index =
    events.value.findIndex(
      item =>
        item.id === updatedEvent.id
    )

  if (index !== -1) {

    events.value[index] =
      updatedEvent
  }
}


// ========================================
// 이벤트 처리 시작
// ========================================

const handleStartProcessing = async (event) => {

  if (!event) {
    return
  }

  eventActionLoading.value = true

  try {

    const response =
      await startEventProcessing(
        event.id
      )

    updateEventInList(
      response.data
    )

    if (
      selectedEvent.value?.id ===
      event.id
    ) {

      selectedEvent.value =
        response.data

      await reloadEventHistories(
        event.id
      )
    }

  } catch (error) {

    console.error(error)

    alert(
      error.response?.data?.message ||
      '이벤트 처리 시작에 실패했습니다.'
    )

  } finally {

    eventActionLoading.value = false

  }
}


// ========================================
// 이벤트 처리 완료
// ========================================

const handleCompleteProcessing = async (
  event
) => {

  if (!event) {
    return
  }

  eventActionLoading.value = true

  try {

    const response =
      await completeEventProcessing(
        event.id
      )

    updateEventInList(
      response.data
    )

    if (
      selectedEvent.value?.id ===
      event.id
    ) {

      selectedEvent.value =
        response.data

      await reloadEventHistories(
        event.id
      )
    }

  } catch (error) {

    console.error(error)

    alert(
      error.response?.data?.message ||
      '이벤트 처리 완료에 실패했습니다.'
    )

  } finally {

    eventActionLoading.value = false

  }
}


// ========================================
// 이벤트 종류 한글
// ========================================

const eventTypeText = (type) => {

  const map = {

    ACCIDENT: '사고 감지',

    STOPPED_VEHICLE:
      '정지 차량',

    WRONG_WAY:
      '역주행',

    PEDESTRIAN:
      '보행자 감지',

    CONGESTION:
      '교통 혼잡',

    ETC:
      '기타'
  }

  return map[type] || type
}


// ========================================
// 상태 한글
// ========================================

const statusText = (status) => {

  const map = {

    UNPROCESSED:
      '미처리',

    PROCESSING:
      '처리중',

    COMPLETED:
      '처리완료'
  }

  return map[status] || status
}


// ========================================
// 위험도 한글
// ========================================

const severityText = (severity) => {

  const map = {

    NORMAL:
      '일반',

    WARNING:
      '주의',

    CRITICAL:
      '긴급'
  }

  return map[severity] || severity
}


// ========================================
// 차량 상태 한글
// ========================================

const vehicleStatusText = (status) => {

  const map = {

    NORMAL:
      '정상',

    WARNING:
      '주의',

    ERROR:
      '오류'
  }

  return map[status] || status
}


// ========================================
// 이벤트 아이콘
// ========================================

const eventIcon = (type) => {

  const map = {

    ACCIDENT:
      '🚨',

    STOPPED_VEHICLE:
      '🚗',

    WRONG_WAY:
      '↔',

    PEDESTRIAN:
      '🚶',

    CONGESTION:
      '〽',

    ETC:
      '●'
  }

  return map[type] || '●'
}


// ========================================
// 날짜 포맷
// ========================================

const formatDate = (date) => {

  if (!date) {
    return '-'
  }

  return date
    .replace('T', ' ')
    .substring(0, 19)
}


// ========================================
// 새로고침
// ========================================

const refreshData = async () => {

  await loadMonitoringData()
}


// ========================================
// 최초 실행
// ========================================

onMounted(() => {

  loadMonitoringData()
})

</script>


<template>

  <div class="monitoring-page">

    <!-- HEADER -->

    <header class="top-header">

      <div class="title-area">

        <div class="live-dot"></div>

        <div>

          <p class="system-name">
            ENSONSOFT SMART MOBILITY
          </p>

          <h1>
            통합관제센터
          </h1>

          <p class="subtitle">
            차량 및 영상분석 기반 스마트 모빌리티 통합 관제 시스템
          </p>

        </div>

      </div>


      <button
        class="refresh-button"
        @click="refreshData"
      >
        <span>↻</span>
        데이터 새로고침
      </button>

    </header>


    <!-- LOADING -->

    <div
      v-if="loading"
      class="loading-box"
    >

      <div class="loading-circle"></div>

      <strong>
        관제 시스템 연결 중
      </strong>

      <span>
        실시간 데이터를 불러오고 있습니다.
      </span>

    </div>


    <!-- ERROR -->

    <div
      v-else-if="errorMessage"
      class="error-box"
    >

      <strong>
        관제 데이터 연결 오류
      </strong>

      <p>
        {{ errorMessage }}
      </p>

      <button @click="refreshData">
        다시 시도
      </button>

    </div>


    <template v-else>

      <!-- KPI -->

      <section class="kpi-grid">

        <article class="kpi-card">

          <div class="kpi-symbol">
            🚗
          </div>

          <div class="kpi-content">

            <span>
              차량번호 인식
            </span>

            <strong>
              {{ totalVehicles }}
            </strong>

            <small>
              최근 인식 데이터
            </small>

          </div>

        </article>


        <article class="kpi-card">

          <div class="kpi-symbol event-symbol">
            ◈
          </div>

          <div class="kpi-content">

            <span>
              전체 이벤트
            </span>

            <strong>
              {{ totalEvents }}
            </strong>

            <small>
              영상분석 감지
            </small>

          </div>

        </article>


        <article class="kpi-card">

          <div class="kpi-symbol warning-symbol">
            !
          </div>

          <div class="kpi-content">

            <span>
              미처리 이벤트
            </span>

            <strong>
              {{ unprocessedEvents }}
            </strong>

            <small>
              확인이 필요한 이벤트
            </small>

          </div>

        </article>


        <article class="kpi-card">

          <div class="kpi-symbol critical-symbol">
            ⚠
          </div>

          <div class="kpi-content">

            <span>
              긴급 이벤트
            </span>

            <strong>
              {{ criticalEvents }}
            </strong>

            <small>
              긴급 대응 필요
            </small>

          </div>

        </article>

      </section>


      <!-- SYSTEM STATUS -->

      <section class="status-bar">

        <div class="system-status">

          <span class="status-light"></span>

          <div>

            <strong>
              SYSTEM ONLINE
            </strong>

            <small>
              관제 시스템 정상 운영 중
            </small>

          </div>

        </div>


        <div class="status-items">

          <div>
            <span>미처리</span>
            <strong>
              {{ unprocessedEvents }}
            </strong>
          </div>

          <div>
            <span>처리중</span>
            <strong>
              {{ processingEvents }}
            </strong>
          </div>

          <div>
            <span>긴급</span>
            <strong>
              {{ criticalEvents }}
            </strong>
          </div>

        </div>

      </section>


      <!-- VIDEO EVENT -->

      <section class="panel event-panel">

        <div class="panel-header">

          <div>

            <span class="panel-label">
              REAL-TIME MONITORING
            </span>

            <h2>
              영상분석 이벤트
            </h2>

          </div>

          <span class="data-count">
            {{ safeEvents.length }} EVENTS
          </span>

        </div>


        <!-- 이벤트 검색 -->

        <div class="event-search">

          <div class="search-field">

            <label>
              위치
            </label>

            <input
              v-model="eventLocationSearch"
              type="text"
              placeholder="예: 서울TG"
              @keyup.enter="searchEventData"
            />

          </div>


          <div class="search-field">

            <label>
              이벤트 종류
            </label>

            <select
              v-model="eventTypeFilter"
            >
              <option value="ALL">
                전체
              </option>

              <option value="ACCIDENT">
                사고 감지
              </option>

              <option value="STOPPED_VEHICLE">
                정지 차량
              </option>

              <option value="WRONG_WAY">
                역주행
              </option>

              <option value="PEDESTRIAN">
                보행자 감지
              </option>

              <option value="CONGESTION">
                교통 혼잡
              </option>

              <option value="ETC">
                기타
              </option>
            </select>

          </div>


          <div class="search-field">

            <label>
              위험도
            </label>

            <select
              v-model="eventSeverityFilter"
            >

              <option value="ALL">
                전체
              </option>

              <option value="NORMAL">
                일반
              </option>

              <option value="WARNING">
                주의
              </option>

              <option value="CRITICAL">
                긴급
              </option>

            </select>

          </div>


          <button
            class="search-button"
            @click="searchEventData"
          >
            {{
              eventSearchLoading
                ? '조회중...'
                : '검색'
            }}
          </button>


          <button
            class="reset-button"
            @click="resetEventSearch"
          >
            초기화
          </button>

        </div>


        <!-- TABS -->

        <div class="tabs">

          <button
            :class="{
              active:
                activeTab === 'events'
            }"
            @click="activeTab = 'events'"
          >
            전체 이벤트
          </button>


          <button
            :class="{
              active:
                activeTab === 'unprocessed'
            }"
            @click="
              activeTab = 'unprocessed'
            "
          >
            미처리

            <b>
              {{ unprocessedEvents }}
            </b>
          </button>


          <button
            :class="{
              active:
                activeTab === 'processing'
            }"
            @click="
              activeTab = 'processing'
            "
          >
            처리중

            <b>
              {{ processingEvents }}
            </b>
          </button>


          <button
            :class="{
              active:
                activeTab === 'completed'
            }"
            @click="
              activeTab = 'completed'
            "
          >
            처리완료
          </button>

        </div>


        <!-- EVENT LIST -->

        <div class="event-list">

          <article
            v-for="event in displayedEvents"
            :key="event.id"
            class="event-row"
            @click="openEventDetail(event)"
          >

            <div
              class="event-icon"
              :class="
                event.severity
                  ? event.severity.toLowerCase()
                  : ''
              "
            >
              {{
                eventIcon(
                  event.eventType
                )
              }}
            </div>


            <div class="event-info">

              <div class="event-name">

                <strong>
                  {{
                    eventTypeText(
                      event.eventType
                    )
                  }}
                </strong>


                <span
                  class="severity"
                  :class="
                    event.severity
                      ? event.severity.toLowerCase()
                      : ''
                  "
                >
                  {{
                    severityText(
                      event.severity
                    )
                  }}
                </span>

              </div>


              <div class="event-meta">

                <span>
                  📍 {{ event.location }}
                </span>

                <span>
                  📹 {{ event.cameraId }}
                </span>

                <span>
                  🕐
                  {{
                    formatDate(
                      event.occurredAt
                    )
                  }}
                </span>

              </div>


              <p>
                {{ event.description }}
              </p>

            </div>


            <div
              class="event-status"
              @click.stop
            >

              <span
                class="status-badge"
                :class="
                  event.status
                    ? event.status.toLowerCase()
                    : ''
                "
              >
                {{
                  statusText(
                    event.status
                  )
                }}
              </span>


              <button
                class="detail-button"
                @click="
                  openEventDetail(event)
                "
              >
                상세
              </button>


              <button
                v-if="
                  event.status ===
                  'UNPROCESSED'
                "
                class="event-button"
                @click="
                  handleStartProcessing(
                    event
                  )
                "
              >
                처리 시작
              </button>


              <button
                v-if="
                  event.status ===
                  'PROCESSING'
                "
                class="event-button complete"
                @click="
                  handleCompleteProcessing(
                    event
                  )
                "
              >
                처리 완료
              </button>

            </div>

          </article>


          <div
            v-if="
              displayedEvents.length === 0
            "
            class="empty-state"
          >
            현재 표시할 이벤트가 없습니다.
          </div>

        </div>

      </section>


      <!-- VEHICLE -->

      <section class="panel vehicle-panel">

        <div class="panel-header">

          <div>

            <span class="panel-label">
              LICENSE PLATE RECOGNITION
            </span>

            <h2>
              차량번호 인식 현황
            </h2>

          </div>


          <span class="data-count">
            {{ safeVehicles.length }} RECORDS
          </span>

        </div>


        <!-- 차량 검색 -->

        <div class="vehicle-search">

          <div class="search-field">

            <label>
              차량번호
            </label>

            <input
              v-model="plateSearch"
              type="text"
              placeholder="예: 12가3456"
              @keyup.enter="
                searchVehicleData
              "
            />

          </div>


          <div class="search-field">

            <label>
              위치
            </label>

            <input
              v-model="locationSearch"
              type="text"
              placeholder="예: 서울TG"
              @keyup.enter="
                searchVehicleData
              "
            />

          </div>


          <div class="search-field">

            <label>
              상태
            </label>

            <select
              v-model="
                vehicleStatusFilter
              "
            >

              <option value="ALL">
                전체
              </option>

              <option value="NORMAL">
                정상
              </option>

              <option value="WARNING">
                주의
              </option>

              <option value="ERROR">
                오류
              </option>

            </select>

          </div>


          <button
            class="search-button"
            @click="
              searchVehicleData
            "
          >
            {{
              vehicleSearchLoading
                ? '조회중...'
                : '검색'
            }}
          </button>


          <button
            class="reset-button"
            @click="
              resetVehicleSearch
            "
          >
            초기화
          </button>

        </div>


        <!-- TABLE -->

        <div class="table-wrapper">

          <table>

            <thead>

              <tr>

                <th>
                  차량번호
                </th>

                <th>
                  인식 위치
                </th>

                <th>
                  카메라
                </th>

                <th>
                  인식 시간
                </th>

                <th>
                  정확도
                </th>

                <th>
                  상태
                </th>

              </tr>

            </thead>


            <tbody>

              <tr
                v-for="
                  vehicle in safeVehicles
                "
                :key="vehicle.id"
              >

                <td>

                  <span
                    class="plate-number"
                  >
                    {{
                      vehicle.plateNumber
                    }}
                  </span>

                </td>


                <td>
                  {{ vehicle.location }}
                </td>


                <td>

                  <span
                    class="camera-id"
                  >
                    {{
                      vehicle.cameraId
                    }}
                  </span>

                </td>


                <td class="time">

                  {{
                    formatDate(
                      vehicle.recognizedAt
                    )
                  }}

                </td>


                <td>

                  <div class="accuracy">

                    <div
                      class="accuracy-track"
                    >

                      <div
                        class="accuracy-value"
                        :style="{
                          width:
                            `${vehicle.confidence}%`
                        }"
                      ></div>

                    </div>

                    <strong>
                      {{
                        vehicle.confidence
                      }}%
                    </strong>

                  </div>

                </td>


                <td>

                  <span
                    class="vehicle-status"
                    :class="
                      vehicle.status
                        ? vehicle.status.toLowerCase()
                        : ''
                    "
                  >

                    <i></i>

                    {{
                      vehicleStatusText(
                        vehicle.status
                      )
                    }}

                  </span>

                </td>

              </tr>

            </tbody>

          </table>


          <div
            v-if="
              safeVehicles.length === 0
            "
            class="empty-state"
          >
            검색 결과가 없습니다.
          </div>

        </div>

      </section>

    </template>


    <!-- ===================================== -->
    <!-- 이벤트 상세 모달 -->
    <!-- ===================================== -->

    <div
      v-if="showEventModal"
      class="modal-overlay"
      @click.self="closeEventDetail"
    >

      <div class="event-modal">

        <!-- 모달 헤더 -->

        <div class="modal-header">

          <div>

            <span class="modal-label">
              EVENT DETAIL
            </span>

            <h2>
              이벤트 상세 정보
            </h2>

          </div>


          <button
            class="modal-close"
            @click="closeEventDetail"
          >
            ×
          </button>

        </div>


        <!-- 상세 로딩 -->

        <div
          v-if="detailLoading"
          class="modal-loading"
        >

          <div class="loading-circle"></div>

          <strong>
            이벤트 정보를 불러오는 중...
          </strong>

        </div>


        <!-- 상세 내용 -->

        <template
          v-else-if="selectedEvent"
        >

          <div class="modal-event-head">

            <div
              class="modal-event-icon"
              :class="
                selectedEvent.severity
                  ? selectedEvent.severity.toLowerCase()
                  : ''
              "
            >
              {{
                eventIcon(
                  selectedEvent.eventType
                )
              }}
            </div>


            <div class="modal-event-title">

              <div>

                <h3>
                  {{
                    eventTypeText(
                      selectedEvent.eventType
                    )
                  }}
                </h3>

                <span
                  class="severity"
                  :class="
                    selectedEvent.severity
                      ? selectedEvent.severity.toLowerCase()
                      : ''
                  "
                >
                  {{
                    severityText(
                      selectedEvent.severity
                    )
                  }}
                </span>

              </div>

              <p>
                {{ selectedEvent.description }}
              </p>

            </div>


            <span
              class="status-badge modal-status"
              :class="
                selectedEvent.status
                  ? selectedEvent.status.toLowerCase()
                  : ''
              "
            >
              {{
                statusText(
                  selectedEvent.status
                )
              }}
            </span>

          </div>


          <!-- 기본 정보 -->

          <div class="detail-grid">

            <div class="detail-card">

              <span>
                이벤트 번호
              </span>

              <strong>
                #{{ selectedEvent.id }}
              </strong>

            </div>


            <div class="detail-card">

              <span>
                발생 위치
              </span>

              <strong>
                {{ selectedEvent.location }}
              </strong>

            </div>


            <div class="detail-card">

              <span>
                카메라
              </span>

              <strong>
                {{ selectedEvent.cameraId }}
              </strong>

            </div>


            <div class="detail-card">

              <span>
                발생 시간
              </span>

              <strong>
                {{
                  formatDate(
                    selectedEvent.occurredAt
                  )
                }}
              </strong>

            </div>


            <div class="detail-card">

              <span>
                처리 담당자
              </span>

              <strong>
                {{
                  selectedEvent.processor ||
                  '미배정'
                }}
              </strong>

            </div>


            <div class="detail-card">

              <span>
                처리 완료 시간
              </span>

              <strong>
                {{
                  formatDate(
                    selectedEvent.processedAt
                  )
                }}
              </strong>

            </div>

          </div>


          <!-- 처리 버튼 -->

          <div
            class="modal-action-area"
          >

            <div>

              <span class="action-label">
                EVENT CONTROL
              </span>

              <strong>
                이벤트 처리
              </strong>

            </div>


            <button
              v-if="
                selectedEvent.status ===
                'UNPROCESSED'
              "
              class="modal-action-button"
              :disabled="
                eventActionLoading
              "
              @click="
                handleStartProcessing(
                  selectedEvent
                )
              "
            >
              {{
                eventActionLoading
                  ? '처리중...'
                  : '처리 시작'
              }}
            </button>


            <button
              v-else-if="
                selectedEvent.status ===
                'PROCESSING'
              "
              class="
                modal-action-button
                complete
              "
              :disabled="
                eventActionLoading
              "
              @click="
                handleCompleteProcessing(
                  selectedEvent
                )
              "
            >
              {{
                eventActionLoading
                  ? '처리중...'
                  : '처리 완료'
              }}
            </button>


            <span
              v-else
              class="complete-message"
            >
              ✓ 처리 완료된 이벤트입니다.
            </span>

          </div>


          <!-- 처리 이력 -->

          <section
            class="history-section"
          >

            <div class="history-header">

              <div>

                <span class="modal-label">
                  PROCESS HISTORY
                </span>

                <h3>
                  처리 이력
                </h3>

              </div>


              <span
                class="history-count"
              >
                {{
                  safeHistories.length
                }} RECORDS
              </span>

            </div>


            <div
              v-if="historyLoading"
              class="history-loading"
            >
              처리 이력을 불러오는 중...
            </div>


            <div
              v-else-if="
                safeHistories.length === 0
              "
              class="history-empty"
            >
              아직 등록된 처리 이력이 없습니다.
            </div>


            <div
              v-else
              class="timeline"
            >

              <div
                v-for="
                  history in safeHistories
                "
                :key="history.id"
                class="timeline-item"
              >

                <div
                  class="timeline-marker"
                  :class="
                    history.status
                      ? history.status.toLowerCase()
                      : ''
                  "
                >
                  <span></span>
                </div>


                <div
                  class="timeline-content"
                >

                  <div
                    class="timeline-top"
                  >

                    <div>

                      <strong>
                        {{
                          history.description
                        }}
                      </strong>

                      <span
                        class="status-badge"
                        :class="
                          history.status
                            ? history.status.toLowerCase()
                            : ''
                        "
                      >
                        {{
                          statusText(
                            history.status
                          )
                        }}
                      </span>

                    </div>


                    <time>
                      {{
                        formatDate(
                          history.processedAt
                        )
                      }}
                    </time>

                  </div>


                  <div
                    class="processor-info"
                  >
                    <span>
                      담당자
                    </span>

                    <strong>
                      {{
                        history.processor ||
                        '-'
                      }}
                    </strong>
                  </div>

                </div>

              </div>

            </div>

          </section>

        </template>

      </div>

    </div>

  </div>

</template>


<style scoped>

/* ========================================
   BASE
======================================== */

* {
  box-sizing: border-box;
}

.monitoring-page {
  min-height: 100vh;
  padding: 42px 48px 70px;

  background:
    radial-gradient(
      circle at 85% 5%,
      rgba(53, 120, 255, 0.07),
      transparent 25%
    ),
    #f4f7fb;

  color: #182235;
}


/* ========================================
   HEADER
======================================== */

.top-header {
  max-width: none;
  margin: 0 auto 30px;

  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.live-dot {
  width: 11px;
  height: 11px;

  border-radius: 50%;

  background: #18b66b;

  box-shadow:
    0 0 0 6px rgba(24, 182, 107, 0.1),
    0 0 18px rgba(24, 182, 107, 0.5);
}

.system-name {
  margin: 0 0 4px;

  font-size: 10px;
  font-weight: 900;
  letter-spacing: 0.18em;

  color: #718096;
}

.top-header h1 {
  margin: 0;

  font-size: 36px;
  font-weight: 900;

  letter-spacing: -0.05em;
}

.subtitle {
  margin: 7px 0 0;

  color: #8793a5;

  font-size: 13px;
}

.refresh-button {
  display: flex;
  align-items: center;

  gap: 8px;

  padding: 12px 17px;

  border: 1px solid #e0e6ef;
  border-radius: 10px;

  background: white;

  color: #354154;

  font-size: 12px;
  font-weight: 800;

  cursor: pointer;

  box-shadow:
    0 5px 20px
    rgba(30, 45, 70, 0.04);

  transition: 0.2s;
}

.refresh-button:hover {
  transform: translateY(-2px);
}

.refresh-button span {
  font-size: 19px;
}


/* ========================================
   KPI
======================================== */

.kpi-grid {
  max-width: none;

  margin: 0 auto 18px;

  display: grid;

  grid-template-columns:
    repeat(4, 1fr);

  gap: 15px;
}

.kpi-card {
  min-height: 145px;

  padding: 24px;

  display: flex;
  align-items: center;

  gap: 17px;

  background: white;

  border:
    1px solid #e7ebf1;

  border-radius: 16px;

  box-shadow:
    0 8px 28px
    rgba(26, 43, 70, 0.045);
}

.kpi-symbol {
  width: 54px;
  height: 54px;

  flex-shrink: 0;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 15px;

  background: #edf3ff;

  font-size: 23px;
}

.event-symbol {
  background: #eef8f4;
}

.warning-symbol {
  background: #fff6df;

  color: #b7791f;
}

.critical-symbol {
  background: #ffebeb;

  color: #d13b3b;
}

.kpi-content span {
  display: block;

  color: #778398;

  font-size: 12px;
  font-weight: 800;
}

.kpi-content strong {
  display: block;

  margin: 6px 0;

  font-size: 32px;
  line-height: 1;

  letter-spacing: -0.05em;
}

.kpi-content small {
  color: #a2acba;

  font-size: 10px;
}


/* ========================================
   STATUS
======================================== */

.status-bar {
  max-width: none;

  margin: 0 auto 22px;

  padding: 15px 20px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  background: #172238;

  border-radius: 13px;

  color: white;

  box-shadow:
    0 10px 28px
    rgba(23, 34, 56, 0.13);
}

.system-status {
  display: flex;
  align-items: center;

  gap: 11px;
}

.status-light {
  width: 8px;
  height: 8px;

  border-radius: 50%;

  background: #37db91;

  box-shadow:
    0 0 12px #37db91;
}

.system-status strong {
  display: block;

  font-size: 11px;

  letter-spacing: 0.08em;
}

.system-status small {
  display: block;

  margin-top: 2px;

  color: #aab5c7;

  font-size: 10px;
}

.status-items {
  display: flex;
  align-items: center;

  gap: 30px;
}

.status-items div {
  display: flex;
  align-items: center;

  gap: 8px;
}

.status-items span {
  color: #9ba8bd;

  font-size: 10px;
}

.status-items strong {
  font-size: 14px;
}


/* ========================================
   PANEL
======================================== */

.panel {
  background: white;

  border:
    1px solid #e6ebf2;

  border-radius: 17px;

  overflow: hidden;

  box-shadow:
    0 8px 30px
    rgba(26, 43, 70, 0.045);
}

.event-panel {
  max-width: none;

  margin: 0 auto 20px;
}

.vehicle-panel {
  max-width: none;

  margin: 0 auto;
}

.panel-header {
  padding: 23px 25px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  border-bottom:
    1px solid #edf0f5;
}

.panel-label,
.modal-label {
  display: block;

  margin-bottom: 5px;

  color: #8490a3;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 0.16em;
}

.panel-header h2 {
  margin: 0;

  font-size: 19px;
  font-weight: 850;

  letter-spacing: -0.035em;
}

.data-count {
  padding: 7px 10px;

  border-radius: 7px;

  background: #f3f6fa;

  color: #7a8799;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 0.08em;
}


/* ========================================
   SEARCH
======================================== */

.event-search,
.vehicle-search {
  padding: 18px 25px;

  display: flex;
  align-items: flex-end;

  gap: 10px;

  background: #fafbfd;

  border-bottom:
    1px solid #edf0f5;
}

.search-field {
  display: flex;

  flex-direction: column;

  gap: 6px;
}

.search-field label {
  color: #7c8798;

  font-size: 9px;
  font-weight: 900;

  letter-spacing: 0.05em;
}

.search-field input,
.search-field select {
  width: 170px;
  height: 38px;

  padding: 0 11px;

  border:
    1px solid #dfe5ed;

  border-radius: 8px;

  outline: none;

  background: white;

  color: #354154;

  font-size: 11px;
}

.search-field input:focus,
.search-field select:focus {
  border-color: #347ce3;

  box-shadow:
    0 0 0 3px
    rgba(52, 124, 227, 0.08);
}

.search-button,
.reset-button {
  height: 38px;

  padding: 0 16px;

  border-radius: 8px;

  font-size: 10px;
  font-weight: 800;

  cursor: pointer;
}

.search-button {
  border: 0;

  background: #1d2a40;

  color: white;
}

.reset-button {
  border:
    1px solid #dfe5ed;

  background: white;

  color: #687589;
}


/* ========================================
   TABS
======================================== */

.tabs {
  display: flex;

  padding: 0 25px;

  border-bottom:
    1px solid #edf0f5;
}

.tabs button {
  position: relative;

  padding: 14px 16px;

  border: 0;

  background: transparent;

  color: #909bad;

  font-size: 11px;
  font-weight: 800;

  cursor: pointer;
}

.tabs button.active {
  color: #1e6fe8;
}

.tabs button.active::after {
  content: '';

  position: absolute;

  left: 10px;
  right: 10px;
  bottom: -1px;

  height: 2px;

  background: #1e6fe8;
}

.tabs b {
  margin-left: 5px;

  padding: 2px 5px;

  border-radius: 5px;

  background: #edf3ff;

  color: #337be7;

  font-size: 9px;
}


/* ========================================
   EVENT
======================================== */

.event-list {
  padding: 0 25px;
}

.event-row {
  min-height: 115px;

  padding: 18px 0;

  display: flex;
  align-items: center;

  gap: 16px;

  border-bottom:
    1px solid #edf0f5;

  cursor: pointer;

  transition: 0.18s;
}

.event-row:hover {
  padding-left: 8px;

  background: #fbfcff;
}

.event-icon {
  width: 44px;
  height: 44px;

  flex-shrink: 0;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 12px;

  background: #eef8f4;

  font-size: 19px;
}

.event-icon.warning {
  background: #fff7e3;
}

.event-icon.critical {
  background: #ffeded;
}

.event-info {
  flex: 1;

  min-width: 0;
}

.event-name {
  display: flex;
  align-items: center;

  gap: 8px;
}

.event-name strong {
  font-size: 14px;
}

.severity {
  padding: 4px 7px;

  border-radius: 5px;

  font-size: 8px;
  font-weight: 900;
}

.severity.normal {
  background: #eaf8f1;

  color: #26905e;
}

.severity.warning {
  background: #fff4d9;

  color: #b47716;
}

.severity.critical {
  background: #ffe7e7;

  color: #ca3c3c;
}

.event-meta {
  margin-top: 7px;

  display: flex;
  flex-wrap: wrap;

  gap: 13px;

  color: #8793a5;

  font-size: 10px;
}

.event-info p {
  margin: 7px 0 0;

  color: #a0a9b7;

  font-size: 10px;
}

.event-status {
  min-width: 220px;

  display: flex;
  align-items: center;
  justify-content: flex-end;

  gap: 7px;
}

.status-badge {
  display: inline-block;

  padding: 6px 8px;

  border-radius: 6px;

  font-size: 9px;
  font-weight: 900;
}

.status-badge.unprocessed {
  background: #ffe9e9;

  color: #c53c3c;
}

.status-badge.processing {
  background: #e9f2ff;

  color: #3479d5;
}

.status-badge.completed {
  background: #eaf8f1;

  color: #2b9561;
}

.event-button,
.detail-button {
  padding: 7px 9px;

  border: 0;

  border-radius: 6px;

  font-size: 9px;
  font-weight: 800;

  cursor: pointer;
}

.event-button {
  background: #1d2a40;

  color: white;
}

.event-button.complete {
  background: #23895a;
}

.detail-button {
  border:
    1px solid #dfe5ed;

  background: white;

  color: #657185;
}


/* ========================================
   TABLE
======================================== */

.table-wrapper {
  width: 100%;

  overflow-x: auto;
}

table {
  width: 100%;

  border-collapse: collapse;

  white-space: nowrap;
}

thead {
  background: #f8fafc;
}

th {
  padding: 12px 20px;

  color: #8994a6;

  font-size: 9px;
  font-weight: 900;

  text-align: left;

  letter-spacing: 0.04em;
}

td {
  padding: 16px 20px;

  border-top:
    1px solid #edf0f5;

  color: #596579;

  font-size: 11px;
}

.plate-number {
  color: #1b273b;

  font-size: 13px;
  font-weight: 900;
}

.camera-id {
  padding: 5px 7px;

  border-radius: 5px;

  background: #f0f3f7;

  color: #738095;

  font-size: 9px;
  font-weight: 800;
}

.time {
  color: #8793a5;
}

.accuracy {
  display: flex;
  align-items: center;

  gap: 8px;
}

.accuracy-track {
  width: 55px;
  height: 5px;

  overflow: hidden;

  border-radius: 10px;

  background: #e8edf3;
}

.accuracy-value {
  height: 100%;

  border-radius: inherit;

  background: #347ce3;
}

.accuracy strong {
  font-size: 10px;
}

.vehicle-status {
  display: inline-flex;
  align-items: center;

  gap: 5px;

  padding: 5px 8px;

  border-radius: 6px;

  font-size: 9px;
  font-weight: 900;
}

.vehicle-status i {
  width: 5px;
  height: 5px;

  border-radius: 50%;

  background: currentColor;
}

.vehicle-status.normal {
  background: #eaf8f1;

  color: #2b9561;
}

.vehicle-status.warning {
  background: #fff4d9;

  color: #b47716;
}

.vehicle-status.error {
  background: #ffe7e7;

  color: #ca3c3c;
}


/* ========================================
   MODAL
======================================== */

.modal-overlay {
  position: fixed;

  inset: 0;

  z-index: 9999;

  padding: 35px 20px;

  display: flex;
  align-items: center;
  justify-content: center;

  background:
    rgba(12, 21, 37, 0.62);

  backdrop-filter: blur(6px);
}

.event-modal {
  width: min(850px, 100%);

  max-height: 90vh;

  overflow-y: auto;

  background: white;

  border-radius: 20px;

  box-shadow:
    0 30px 90px
    rgba(10, 20, 40, 0.25);
}

.modal-header {
  position: sticky;

  top: 0;

  z-index: 3;

  padding: 22px 25px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  background:
    rgba(255, 255, 255, 0.97);

  border-bottom:
    1px solid #e9edf3;
}

.modal-header h2 {
  margin: 0;

  font-size: 21px;
}

.modal-close {
  width: 36px;
  height: 36px;

  border: 0;

  border-radius: 9px;

  background: #f1f4f8;

  color: #586477;

  font-size: 24px;

  cursor: pointer;
}

.modal-event-head {
  padding: 25px;

  display: flex;
  align-items: center;

  gap: 15px;

  border-bottom:
    1px solid #edf0f5;
}

.modal-event-icon {
  width: 58px;
  height: 58px;

  flex-shrink: 0;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 16px;

  background: #eef8f4;

  font-size: 25px;
}

.modal-event-icon.warning {
  background: #fff7e3;
}

.modal-event-icon.critical {
  background: #ffeded;
}

.modal-event-title {
  flex: 1;
}

.modal-event-title > div {
  display: flex;
  align-items: center;

  gap: 8px;
}

.modal-event-title h3 {
  margin: 0;

  font-size: 19px;
}

.modal-event-title p {
  margin: 7px 0 0;

  color: #7f8b9e;

  font-size: 11px;
}

.modal-status {
  flex-shrink: 0;
}

.detail-grid {
  padding: 20px 25px;

  display: grid;

  grid-template-columns:
    repeat(3, 1fr);

  gap: 10px;

  background: #f8fafc;
}

.detail-card {
  min-height: 78px;

  padding: 14px;

  display: flex;
  flex-direction: column;
  justify-content: center;

  gap: 6px;

  border:
    1px solid #e8edf3;

  border-radius: 10px;

  background: white;
}

.detail-card span {
  color: #929dad;

  font-size: 9px;
  font-weight: 800;
}

.detail-card strong {
  color: #253248;

  font-size: 11px;
}

.modal-action-area {
  margin: 20px 25px;

  padding: 16px 18px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 20px;

  border-radius: 12px;

  background: #172238;

  color: white;
}

.modal-action-area > div {
  display: flex;
  flex-direction: column;

  gap: 3px;
}

.action-label {
  color: #8e9bb0;

  font-size: 8px;
  font-weight: 900;

  letter-spacing: 0.13em;
}

.modal-action-area strong {
  font-size: 12px;
}

.modal-action-button {
  padding: 10px 15px;

  border: 0;

  border-radius: 8px;

  background: #347ce3;

  color: white;

  font-size: 10px;
  font-weight: 900;

  cursor: pointer;
}

.modal-action-button.complete {
  background: #27a36b;
}

.modal-action-button:disabled {
  opacity: 0.6;

  cursor: wait;
}

.complete-message {
  color: #55e5a3;

  font-size: 10px;
  font-weight: 800;
}


/* ========================================
   HISTORY
======================================== */

.history-section {
  padding: 5px 25px 30px;
}

.history-header {
  padding: 15px 0;

  display: flex;
  align-items: center;
  justify-content: space-between;

  border-bottom:
    1px solid #edf0f5;
}

.history-header h3 {
  margin: 0;

  font-size: 16px;
}

.history-count {
  padding: 6px 9px;

  border-radius: 6px;

  background: #f1f4f8;

  color: #8490a3;

  font-size: 8px;
  font-weight: 900;
}

.history-loading,
.history-empty {
  padding: 45px 10px;

  color: #98a3b3;

  text-align: center;

  font-size: 11px;
}

.timeline {
  padding-top: 18px;
}

.timeline-item {
  display: flex;

  gap: 15px;
}

.timeline-marker {
  position: relative;

  width: 20px;

  flex-shrink: 0;

  display: flex;
  justify-content: center;
}

.timeline-marker::after {
  content: '';

  position: absolute;

  top: 16px;
  bottom: -12px;

  width: 1px;

  background: #dfe5ed;
}

.timeline-item:last-child
.timeline-marker::after {
  display: none;
}

.timeline-marker span {
  position: relative;

  z-index: 1;

  width: 10px;
  height: 10px;

  margin-top: 5px;

  border: 3px solid white;

  border-radius: 50%;

  background: #8c98aa;

  box-shadow:
    0 0 0 2px #dfe5ed;
}

.timeline-marker.processing span {
  background: #347ce3;

  box-shadow:
    0 0 0 2px #bcd5f7;
}

.timeline-marker.completed span {
  background: #29a36c;

  box-shadow:
    0 0 0 2px #bfe8d5;
}

.timeline-content {
  flex: 1;

  margin-bottom: 20px;

  padding: 14px 16px;

  border:
    1px solid #e7ebf1;

  border-radius: 11px;

  background: #fbfcfe;
}

.timeline-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;

  gap: 15px;
}

.timeline-top > div {
  display: flex;
  align-items: center;

  gap: 7px;
}

.timeline-top strong {
  font-size: 11px;
}

.timeline-top time {
  color: #929dad;

  font-size: 9px;
}

.processor-info {
  margin-top: 10px;

  display: flex;
  align-items: center;

  gap: 7px;
}

.processor-info span {
  color: #98a3b3;

  font-size: 9px;
}

.processor-info strong {
  color: #526075;

  font-size: 10px;
}


/* ========================================
   LOADING / ERROR
======================================== */

.loading-box,
.error-box {
  max-width: 500px;

  margin: 120px auto;

  padding: 45px;

  display: flex;
  flex-direction: column;
  align-items: center;

  text-align: center;

  background: white;

  border:
    1px solid #e5eaf1;

  border-radius: 18px;

  box-shadow:
    0 10px 35px
    rgba(20, 35, 60, 0.06);
}

.loading-circle {
  width: 35px;
  height: 35px;

  margin-bottom: 18px;

  border:
    3px solid #e5eaf1;

  border-top-color: #347ce3;

  border-radius: 50%;

  animation:
    spin 0.8s linear infinite;
}

@keyframes spin {

  to {
    transform: rotate(360deg);
  }
}

.modal-loading {
  min-height: 300px;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  color: #7f8b9e;

  font-size: 11px;
}

.empty-state {
  padding: 50px;

  color: #a0a9b7;

  text-align: center;

  font-size: 11px;
}


/* ========================================
   RESPONSIVE
======================================== */

@media (max-width: 1000px) {

  .monitoring-page {
    padding:
      30px 25px 50px;
  }

  .kpi-grid {
    grid-template-columns:
      repeat(2, 1fr);
  }

  .event-search,
  .vehicle-search {
    flex-wrap: wrap;
  }

  .detail-grid {
    grid-template-columns:
      repeat(2, 1fr);
  }
}


@media (max-width: 700px) {

  .monitoring-page {
    padding:
      20px 15px 40px;
  }

  .top-header {
    align-items: flex-start;

    flex-direction: column;

    gap: 18px;
  }

  .top-header h1 {
    font-size: 29px;
  }

  .refresh-button {
    width: 100%;

    justify-content: center;
  }

  .kpi-grid {
    grid-template-columns: 1fr;
  }

  .status-bar {
    align-items: flex-start;

    flex-direction: column;

    gap: 15px;
  }

  .status-items {
    width: 100%;

    justify-content: space-between;
  }

  .event-search,
  .vehicle-search {
    align-items: stretch;

    flex-direction: column;
  }

  .search-field input,
  .search-field select,
  .search-button,
  .reset-button {
    width: 100%;
  }

  .event-row {
    align-items: flex-start;

    flex-wrap: wrap;
  }

  .event-status {
    width: 100%;

    justify-content: flex-start;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .modal-event-head {
    align-items: flex-start;

    flex-wrap: wrap;
  }

  .modal-action-area {
    align-items: flex-start;

    flex-direction: column;
  }

  .timeline-top {
    flex-direction: column;

    gap: 8px;
  }
}

</style>