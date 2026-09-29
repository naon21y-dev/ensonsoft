<script setup>
import { onMounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useAttendanceStore } from '../stores/attendance'
import { getMyAttendanceHistory } from '../api/Attendance'
import { useAttendance, workTypes, daysBefore } from '../composables/useAttendance'
import AttendanceTable from '../components/AttendanceTable.vue'
import '../assets/attendance.css'

const { t, time, duration, synchronize, errorText } = useAttendance()
const store = useAttendanceStore()
const { today, loading, busy, error, notice, workType, notes, record, canCheckIn, overnight, revision } = storeToRefs(store)
const history = ref(null)
const historyLoading = ref(false)
const historyError = ref('')
const from = ref('')
const to = ref('')
watch(() => today.value?.serverTime, value => { if (value) synchronize(value) }, { immediate: true })
watch(() => today.value?.date, value => {
  if (value && !to.value) { to.value = value; from.value = daysBefore(value) }
}, { immediate: true })
let historyRequest = 0
async function loadHistory() {
  const request = ++historyRequest
  historyLoading.value = true; historyError.value = ''
  try {
    const { data } = await getMyAttendanceHistory({ from: from.value || undefined, to: to.value || undefined })
    if (request === historyRequest) history.value = data
  } catch (e) { if (request === historyRequest) historyError.value = errorText(e) }
  finally { if (request === historyRequest) historyLoading.value = false }
}
const act = action => store.act(action)
watch(revision, () => loadHistory())
async function refresh() { notice.value = ''; await store.refresh(); await loadHistory() }
onMounted(refresh)
</script>

<template>
  <div class="attendance-page">
    <header class="attendance-header"><div><p class="attendance-eyebrow">{{ t('attendance.eyebrow') }}</p><h1>{{ t('attendance.myTitle') }}</h1><p>{{ t('attendance.myDescription') }}</p></div>
      <button class="attendance-button" :disabled="busy || loading" @click="refresh">{{ t('attendance.refresh') }}</button>
    </header>
    <p v-if="error" class="attendance-alert" role="alert">{{ t(error) }}</p>
    <p v-if="notice" class="attendance-notice" role="status">{{ t(notice) }}</p>
    <section class="attendance-panel attendance-today" :aria-busy="loading || busy">
      <div class="attendance-section-title"><div><p class="attendance-today-label">{{ t('attendance.quickTitle') }}</p><h2>{{ t('attendance.today') }}</h2><p v-if="today">{{ today.date }} · {{ today.zone }}</p></div>
        <span v-if="record" class="attendance-badge" :class="record.status">{{ t(`attendance.states.${record.status}`) }}</span></div>
      <div v-if="!today" class="attendance-empty"><p>{{ t(loading ? 'attendance.loading' : 'attendance.unavailable') }}</p><button v-if="!loading" class="attendance-button" @click="refresh">{{ t('attendance.refresh') }}</button></div>
      <template v-else>
      <p v-if="overnight" class="attendance-info">{{ t('attendance.overnight', { date: record.workDate }) }}</p>
      <div class="attendance-clock-grid">
        <div><span>{{ t('attendance.checkInAt') }}</span><strong>{{ time(record.checkInAt, today.zone) }}</strong></div>
        <div><span>{{ t('attendance.checkOutAt') }}</span><strong>{{ time(record.checkOutAt, today.zone) }}</strong></div>
        <div><span>{{ t('attendance.worked') }}</span><strong>{{ record.id ? duration(record) : '—' }}</strong></div>
      </div>
      <p v-if="record.status === 'CHECKED_OUT'" class="attendance-work-complete">✓ {{ t('attendance.workComplete') }}</p>
      <form @submit.prevent="act(canCheckIn ? 'in' : 'notes')">
        <div class="attendance-fields">
          <label>{{ t('attendance.workType') }}<select v-if="canCheckIn" v-model="workType" :disabled="busy || loading"><option v-for="type in workTypes" :key="type" :value="type">{{ t(`attendance.types.${type}`) }}</option></select>
            <span v-else class="attendance-readonly">{{ record.workType ? t(`attendance.types.${record.workType}`) : '—' }}</span></label>
          <label class="attendance-grow">{{ t('attendance.notes') }}<textarea v-model="notes" maxlength="2000" rows="3" :disabled="busy || loading" :placeholder="t('attendance.notesPlaceholder')"></textarea><small>{{ notes.length }} / 2000</small></label>
        </div>
        <p class="attendance-muted">{{ t('attendance.serverTimeHint') }}</p>
        <div class="attendance-actions">
          <button v-if="canCheckIn" class="attendance-button primary" :disabled="busy || loading" type="submit">{{ t('attendance.checkIn') }}</button>
          <template v-else><button class="attendance-button" :disabled="busy || loading || !record.id" type="submit">{{ t('attendance.saveNotes') }}</button>
            <button v-if="today.activeAttendance" class="attendance-button primary" :disabled="busy || loading" type="button" @click="act('out')">{{ t('attendance.checkOut') }}</button></template>
        </div>
      </form>
      </template>
    </section>
    <section class="attendance-panel">
      <div class="attendance-section-title"><h2>{{ t('attendance.myHistory') }}</h2><span class="attendance-muted">{{ t('attendance.rangeHint') }}</span></div>
      <form class="attendance-filters" @submit.prevent="loadHistory"><label>{{ t('attendance.from') }}<input v-model="from" type="date" required :max="to" /></label><label>{{ t('attendance.to') }}<input v-model="to" type="date" required :min="from" :max="today?.date" /></label><button class="attendance-button primary" :disabled="historyLoading">{{ t('attendance.search') }}</button></form>
      <p v-if="historyError" class="attendance-alert" role="alert">{{ t(historyError) }}</p>
      <p v-if="historyLoading" class="attendance-empty">{{ t('attendance.loading') }}</p>
      <AttendanceTable v-else-if="history" :rows="history.records" :zone="history.zone" :server-time="history.serverTime" />
    </section>
  </div>
</template>
