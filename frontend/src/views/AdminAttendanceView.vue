<script setup>
import { onMounted, ref } from 'vue'
import { getAttendanceDay, getEmployeeAttendance } from '../api/Attendance'
import { useAttendance, workTypes, attendanceStatuses, daysBefore } from '../composables/useAttendance'
import AttendanceTable from '../components/AttendanceTable.vue'
import LanguageSwitcher from '../components/LanguageSwitcher.vue'
import '../assets/attendance.css'

const { t, errorText } = useAttendance()
const day = ref(null)
const date = ref('')
const search = ref('')
const status = ref('')
const workType = ref('')
const loading = ref(false)
const error = ref('')
const detail = ref(null)
const detailLoading = ref(false)
const detailError = ref('')
const selected = ref(null)
const modal = ref(null)
const from = ref('')
const to = ref('')
const summaryKeys = ['total', 'working', 'notCheckedIn', 'checkedOut', 'duty', 'emergency', 'substitute']
let requestId = 0
async function load() {
  const request = ++requestId; loading.value = true; error.value = ''
  try {
    const { data } = await getAttendanceDay({ date: date.value || undefined, search: search.value || undefined, status: status.value || undefined, workType: workType.value || undefined })
    if (request !== requestId) return
    day.value = data; date.value = data.date
  } catch (e) { if (request === requestId) error.value = errorText(e) }
  finally { if (request === requestId) loading.value = false }
}
function reset() { search.value = ''; status.value = ''; workType.value = ''; load() }
let detailId = 0
async function loadDetail() {
  const request = ++detailId; detailLoading.value = true; detailError.value = ''
  try {
    const { data } = await getEmployeeAttendance(selected.value.memberId, { from: from.value, to: to.value })
    if (request === detailId) detail.value = data
  } catch (e) { if (request === detailId) detailError.value = errorText(e) }
  finally { if (request === detailId) detailLoading.value = false }
}
function openDetail(row) {
  selected.value = row; detail.value = null; to.value = day.value.date; from.value = daysBefore(to.value)
  modal.value.showModal(); loadDetail()
}
function closeDetail() { ++detailId; detailLoading.value = false; modal.value.close() }
onMounted(load)
</script>

<template>
  <div class="attendance-page">
    <header class="attendance-header"><div><p class="attendance-eyebrow">{{ t('attendance.adminEyebrow') }}</p><h1>{{ t('attendance.adminTitle') }}</h1><p>{{ t('attendance.adminDescription') }}</p></div><button class="attendance-button" :disabled="loading" @click="load">{{ t('attendance.refresh') }}</button></header>
    <p v-if="error" class="attendance-alert" role="alert">{{ t(error) }}</p>
    <template v-if="day"><p class="attendance-muted">{{ day.date }} · {{ day.zone }} · {{ t('attendance.summaryHint') }}</p>
      <div class="attendance-summary"><article v-for="key in summaryKeys" :key="key" class="attendance-summary-card" :class="key"><span>{{ t(`attendance.summary.${key}`) }}</span><strong>{{ day.summary[key] }}</strong></article></div></template>
    <section class="attendance-panel">
      <form class="attendance-filters" @submit.prevent="load">
        <label>{{ t('attendance.date') }}<input v-model="date" type="date" required /></label>
        <label class="attendance-grow">{{ t('attendance.employee') }}<input v-model="search" :placeholder="t('attendance.searchPlaceholder')" maxlength="100" /></label>
        <label>{{ t('attendance.status') }}<select v-model="status"><option value="">{{ t('attendance.all') }}</option><option v-for="value in attendanceStatuses" :key="value" :value="value">{{ t(`attendance.states.${value}`) }}</option></select></label>
        <label>{{ t('attendance.workType') }}<select v-model="workType"><option value="">{{ t('attendance.all') }}</option><option v-for="type in workTypes" :key="type" :value="type">{{ t(`attendance.types.${type}`) }}</option></select></label>
        <button class="attendance-button primary" :disabled="loading">{{ t('attendance.search') }}</button><button type="button" class="attendance-button" :disabled="loading" @click="reset">{{ t('attendance.reset') }}</button>
      </form>
      <div class="attendance-section-title"><h2>{{ t('attendance.employeeList') }}</h2><span>{{ t('attendance.resultCount', { count: day?.employees.length || 0 }) }}</span></div>
      <p v-if="loading" class="attendance-empty">{{ t('attendance.loading') }}</p>
      <AttendanceTable v-else-if="day" employees :rows="day.employees" :zone="day.zone" :server-time="day.serverTime" @detail="openDetail" />
      <p class="attendance-muted">{{ t('attendance.absenceHint') }}</p>
    </section>
    <dialog ref="modal" class="attendance-modal" aria-labelledby="attendance-detail-title" @cancel.prevent="closeDetail">
      <header class="attendance-section-title"><div><h2 id="attendance-detail-title">{{ t('attendance.employeeDetail') }}</h2><p>{{ selected?.name }} · {{ selected?.username }}</p></div><LanguageSwitcher /><button class="attendance-button" @click="closeDetail">{{ t('attendance.close') }}</button></header>
      <form class="attendance-filters" @submit.prevent="loadDetail"><label>{{ t('attendance.from') }}<input v-model="from" required type="date" :max="to" /></label><label>{{ t('attendance.to') }}<input v-model="to" required type="date" :min="from" /></label><button class="attendance-button primary" :disabled="detailLoading">{{ t('attendance.search') }}</button></form>
      <p class="attendance-muted">{{ t('attendance.rangeHint') }}</p><p v-if="detailError" class="attendance-alert" role="alert">{{ t(detailError) }}</p>
      <p v-if="detailLoading" class="attendance-empty">{{ t('attendance.loading') }}</p>
      <AttendanceTable v-else-if="detail" :rows="detail.records" :zone="detail.zone" :server-time="detail.serverTime" />
    </dialog>
  </div>
</template>
