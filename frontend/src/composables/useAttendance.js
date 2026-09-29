import { onBeforeUnmount, ref } from 'vue'
import { useI18n } from 'vue-i18n'

export const workTypes = ['NORMAL', 'DUTY', 'EMERGENCY', 'SUBSTITUTE']
export const attendanceStatuses = ['NOT_CHECKED_IN', 'WORKING', 'CHECKED_OUT']

export function useAttendance() {
  const { t, te, locale } = useI18n()
  const tick = ref(0)
  const timer = setInterval(() => tick.value++, 1000)
  onBeforeUnmount(() => clearInterval(timer))
  let serverMillis = 0
  let receivedAt = 0
  const synchronize = time => { serverMillis = new Date(time).getTime(); receivedAt = performance.now() }
  const time = (value, zone = 'Asia/Seoul') => value ? new Intl.DateTimeFormat(locale.value, {
    timeZone: zone, year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit', hourCycle: 'h23',
  }).format(new Date(value)) : '—'
  const duration = row => {
    tick.value // Refresh the displayed elapsed time without writing client time to the API.
    let seconds = row.workedSeconds || 0
    if (row.status === 'WORKING' && row.checkInAt && serverMillis)
      seconds = Math.max(0, Math.floor((serverMillis + performance.now() - receivedAt - new Date(row.checkInAt).getTime()) / 1000))
    return t('attendance.duration', { hours: Math.floor(seconds / 3600), minutes: Math.floor(seconds % 3600 / 60) })
  }
  const errorText = error => {
    const code = error.response?.data?.code
    if (typeof code === 'string' && code.startsWith('attendance.errors.') && te(code)) return code
    return error.response?.status === 401 ? 'attendance.errors.unauthorized'
      : error.response?.status === 403 ? 'attendance.errors.forbidden' : 'attendance.errors.failed'
  }
  return { t, time, duration, synchronize, errorText }
}

export function daysBefore(date, days = 30) {
  const value = new Date(`${date}T12:00:00Z`)
  value.setUTCDate(value.getUTCDate() - days)
  return value.toISOString().slice(0, 10)
}
