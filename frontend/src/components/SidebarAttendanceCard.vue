<script setup>
import { onMounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useAttendanceStore } from '../stores/attendance'
import { useAttendance, workTypes } from '../composables/useAttendance'

const store = useAttendanceStore()
const { today, record, loading, busy, error, canCheckIn, workType, overnight } = storeToRefs(store)
const { t, clockTime, duration, synchronize } = useAttendance()
const expanded = ref(false)
watch(() => today.value?.serverTime, value => { if (value) synchronize(value) }, { immediate: true })
onMounted(() => store.refresh())
</script>

<template>
  <div class="sidebar-attendance" @keydown.esc="expanded = false">
    <button class="attendance-mini" :aria-label="t('attendance.today')" :title="t('attendance.today')" :aria-expanded="expanded" aria-controls="sidebar-attendance-panel" @click="expanded = !expanded">
      <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 6v6l4 2"/></svg>
      <span class="mini-dot" :class="record?.status"></span>
    </button>
    <section id="sidebar-attendance-panel" class="shift-card" :class="{ expanded }" :aria-label="t('attendance.today')" :aria-busy="loading || busy">
      <header><span class="shift-kicker">{{ t('attendance.quickTitle') }}</span><button class="shift-close" :aria-label="t('attendance.close')" @click="expanded = false">×</button></header>
      <template v-if="today">
        <h2 class="shift-status" :class="record.status"><span></span>{{ t(`attendance.states.${record.status}`) }}</h2>
        <p class="shift-date">{{ today.date }}</p>
        <p v-if="overnight" class="shift-feedback">{{ t('attendance.overnight', { date: record.workDate }) }}</p>
        <dl class="shift-times"><div><dt>{{ t('attendance.checkInAt') }}</dt><dd>{{ clockTime(record.checkInAt, today.zone) }}</dd></div><div><dt>{{ t('attendance.checkOutAt') }}</dt><dd>{{ clockTime(record.checkOutAt, today.zone) }}</dd></div></dl>
        <div v-if="record.id" class="shift-duration"><span>{{ t('attendance.worked') }}</span><strong>{{ duration(record) }}</strong></div>
        <label v-if="canCheckIn" class="shift-type">{{ t('attendance.workType') }}<select v-model="workType" :disabled="busy || loading" :aria-label="t('attendance.workType')"><option v-for="type in workTypes" :key="type" :value="type">{{ t(`attendance.types.${type}`) }}</option></select></label>
        <p v-else class="shift-type-name">{{ t(`attendance.types.${record.workType}`) }}</p>
        <button v-if="canCheckIn" class="shift-action" :disabled="busy || loading" @click="store.act('in')">{{ t('attendance.checkIn') }} <span aria-hidden="true">→</span></button>
        <button v-else-if="today.activeAttendance" class="shift-action" :disabled="busy || loading" @click="store.act('out')">{{ t('attendance.checkOut') }} <span aria-hidden="true">→</span></button>
        <p v-else class="shift-complete">✓ {{ t('attendance.workComplete') }}</p>
      </template>
      <p v-else class="shift-feedback">{{ t(loading ? 'attendance.loading' : 'attendance.unavailable') }}</p>
      <p v-if="error" class="shift-feedback shift-error" role="alert">{{ t(error) }}</p>
      <button v-if="error" class="shift-retry" :disabled="loading || busy" @click="store.refresh()">{{ t('attendance.refresh') }}</button>
      <RouterLink class="shift-link" to="/attendance" @click="expanded = false">{{ t('attendance.myTitle') }} <span aria-hidden="true">↗</span></RouterLink>
    </section>
  </div>
</template>

<style scoped>
.sidebar-attendance { flex-shrink: 0; margin: 16px 14px 0; position: relative; }
.shift-card { padding: 18px 16px 12px; border: 1px solid #a5f3d8; border-radius: 15px; background: linear-gradient(140deg,#ccfbed,#ecfff9 70%,#c8f3ed); color: #134e4a; box-shadow: 0 8px 24px #0002; max-height: 48vh; overflow-y: auto; }
.shift-card header { display: flex; align-items: center; justify-content: space-between; }.shift-kicker { font-size: 11px; font-weight: 800; letter-spacing: .8px; color: #11796e; }
.shift-status { display: flex; align-items: center; gap: 7px; font-size: 19px; line-height: 1.3; margin: 9px 0 3px; }.shift-status>span { width: 7px; height: 7px; flex-shrink: 0; border-radius: 50%; background: #709b91; }.shift-status.WORKING>span { background: #059669; box-shadow: 0 0 0 4px #10b98120; }
.shift-date { font-size: 11px; color: #427c72; margin: 0 0 12px; }.shift-times { margin: 0; font-size: 11px; }.shift-times div { display: flex; justify-content: space-between; align-items: baseline; gap: 8px; padding: 5px 0; }.shift-times dt { flex-shrink: 0; color: #427c72; }.shift-times dd { margin: 0; text-align: right; font-weight: 700; font-variant-numeric: tabular-nums; overflow-wrap: anywhere; }
.shift-duration { display: flex; justify-content: space-between; gap: 8px; font-size: 11px; padding-top: 7px; }.shift-duration strong { font-size: 16px; }.shift-type { display: flex; flex-direction: column; gap: 5px; font-size: 11px; margin: 12px 0; }.shift-type select { width: 100%; min-height: 34px; border: 1px solid #94d9c7; border-radius: 7px; color: #134e4a; background: #ffffffb3; padding: 5px 8px; font: inherit; }.shift-type-name { font-size: 11px; margin: 9px 0; color: #427c72; }
.shift-action { width: 100%; display: flex; justify-content: space-between; align-items: center; margin-top: 12px; padding: 11px 13px; border: 0; border-radius: 9px; background: #087f70; color: #fff; font: inherit; font-size: 13px; font-weight: 750; cursor: pointer; }.shift-action:hover { background: #066858; }.shift-action:disabled,.shift-retry:disabled { opacity: .5; cursor: wait; }.shift-complete { text-align: center; background: #b9eeda; border-radius: 8px; padding: 9px; margin: 12px 0 0; font-size: 12px; font-weight: 700; }
.shift-link { display: flex; justify-content: space-between; padding-top: 12px; font-size: 11px; font-weight: 700; color: #216e62; }.shift-feedback { font-size: 12px; line-height: 1.6; margin: 10px 0; }.shift-error { color: #9f1239; }.shift-retry { color: #134e4a; border: 1px solid #72bfa9; border-radius: 6px; background: #fff; padding: 6px 10px; cursor: pointer; }.shift-close,.attendance-mini { display: none; }.shift-card :focus-visible,.attendance-mini:focus-visible { outline: 2px solid #087f70; outline-offset: 3px; }
@media(max-width:820px) {
  .sidebar-attendance { margin: 12px 10px 0; }.attendance-mini { position: relative; display: flex; align-items: center; justify-content: center; width: 100%; height: 44px; border: 1px solid #9ce7ce; border-radius: 11px; background: #c9f8e7; color: #087f70; cursor: pointer; }.attendance-mini svg { width: 23px; fill: none; stroke: currentColor; stroke-width: 1.8; }.mini-dot { position: absolute; right: 5px; bottom: 5px; width: 6px; height: 6px; border-radius: 50%; background: #76998e; }.mini-dot.WORKING { background: #059669; }
  .shift-card { display: none; }.shift-card.expanded { display: block; position: fixed; left: calc(var(--sidebar-width) + 8px); top: 88px; width: min(280px,calc(100vw - var(--sidebar-width) - 16px)); max-height: calc(100dvh - 106px); }.shift-close { display: block; border: 0; background: transparent; color: #134e4a; font-size: 22px; cursor: pointer; }
}
</style>
