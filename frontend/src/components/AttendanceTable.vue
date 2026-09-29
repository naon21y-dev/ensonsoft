<script setup>
import { watch } from 'vue'
import { useAttendance } from '../composables/useAttendance'
const props = defineProps({ rows: { type: Array, default: () => [] }, zone: String, serverTime: String, employees: Boolean })
defineEmits(['detail'])
const { t, time, duration, synchronize } = useAttendance()
watch(() => props.serverTime, value => { if (value) synchronize(value) }, { immediate: true })
</script>

<template>
  <div class="attendance-table-scroll">
    <table class="attendance-table">
      <thead><tr>
        <th v-if="employees">{{ t('attendance.employee') }}</th><th>{{ t('attendance.date') }}</th>
        <th>{{ t('attendance.checkInAt') }}</th><th>{{ t('attendance.checkOutAt') }}</th><th>{{ t('attendance.worked') }}</th>
        <th>{{ t('attendance.workType') }}</th><th>{{ t('attendance.status') }}</th><th>{{ t('attendance.notes') }}</th>
        <th v-if="employees">{{ t('attendance.detail') }}</th>
      </tr></thead>
      <tbody>
        <tr v-for="row in rows" :key="`${row.memberId}-${row.workDate}`">
          <td v-if="employees"><strong>{{ row.name }}</strong><small class="attendance-muted">{{ row.username }}</small></td>
          <td>{{ row.workDate }}</td><td>{{ time(row.checkInAt, zone) }}</td><td>{{ time(row.checkOutAt, zone) }}</td>
          <td>{{ row.status === 'NOT_CHECKED_IN' ? '—' : duration(row) }}</td>
          <td>{{ row.workType ? t(`attendance.types.${row.workType}`) : '—' }}</td>
          <td><span class="attendance-badge" :class="row.status">{{ t(`attendance.states.${row.status}`) }}</span></td>
          <td class="attendance-notes-cell">{{ row.notes || '—' }}</td>
          <td v-if="employees"><button type="button" class="attendance-button" @click="$emit('detail', row)">{{ t('attendance.detail') }}</button></td>
        </tr>
        <tr v-if="!rows.length"><td :colspan="employees ? 9 : 7" class="attendance-empty">{{ t('attendance.empty') }}</td></tr>
      </tbody>
    </table>
  </div>
</template>
