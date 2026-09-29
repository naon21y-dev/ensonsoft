import { computed, ref, watch } from 'vue'
import { defineStore } from 'pinia'
import { useAuthStore } from './auth'
import { i18n } from '../i18n'
import { getMyAttendanceToday, checkIn, checkOut, saveAttendanceNotes } from '../api/Attendance'

// One state and mutation lock for the sidebar and employee page.
export const useAttendanceStore = defineStore('attendance', () => {
  const auth = useAuthStore()
  const today = ref(null), loading = ref(false), busy = ref(false)
  const error = ref(''), notice = ref(''), notes = ref(''), workType = ref('NORMAL'), revision = ref(0)
  const record = computed(() => today.value?.activeAttendance || today.value?.attendance)
  const canCheckIn = computed(() => !!today.value && !today.value.activeAttendance && !today.value.attendance.id)
  const overnight = computed(() => !!today.value?.activeAttendance && record.value.workDate !== today.value.date)
  let generation = 0, pending = null
  watch(() => [auth.token, auth.username, auth.role], () => {
    generation++; pending = null
    today.value = null; loading.value = false; busy.value = false
    error.value = ''; notice.value = ''; notes.value = ''; workType.value = 'NORMAL'; revision.value = 0
  }, { flush: 'sync' })
  function errorKey(e) {
    const code = e.response?.data?.code
    return typeof code === 'string' && code.startsWith('attendance.errors.') && i18n.global.te(code) ? code
      : e.response?.status === 401 ? 'attendance.errors.unauthorized'
        : e.response?.status === 403 ? 'attendance.errors.forbidden' : 'attendance.errors.failed'
  }
  function refresh(replaceDraft = false) {
    if (!auth.isLoggedIn || auth.role !== 'USER') return Promise.resolve()
    if (pending) return pending
    const current = generation
    loading.value = true; error.value = ''
    pending = (async () => {
      try {
        const { data } = await getMyAttendanceToday()
        if (current !== generation) return
        const pristine = notes.value === (record.value?.notes || '')
        today.value = data
        if (replaceDraft || pristine) notes.value = record.value.notes
      } catch (e) { if (current === generation) error.value = errorKey(e) }
      finally { if (current === generation) { loading.value = false; pending = null } }
    })()
    return pending
  }
  async function act(action) {
    if (busy.value || loading.value || !today.value || auth.role !== 'USER') return
    const current = generation
    busy.value = true; error.value = ''; notice.value = ''
    try {
      let response
      if (action === 'in') response = await checkIn({ workType: workType.value, notes: notes.value })
      else if (action === 'out') {
        if (notes.value !== record.value.notes) await saveAttendanceNotes(notes.value)
        if (current !== generation) return
        response = await checkOut()
      } else response = await saveAttendanceNotes(notes.value)
      if (current !== generation) return
      // Preserve the successful server result even if the following GET fails.
      const row = response.data
      if (row.workDate === today.value.date) today.value.attendance = row
      today.value.activeAttendance = row.status === 'WORKING' ? row : null
      notice.value = action === 'in' ? 'attendance.checkedIn' : action === 'out' ? 'attendance.checkedOut' : 'attendance.notesSaved'
      revision.value++
      await refresh(true)
    } catch (e) {
      if (current !== generation) return
      const message = errorKey(e)
      if (e.response?.status === 400) await refresh()
      if (current === generation) error.value = message
    } finally { if (current === generation) busy.value = false }
  }
  return { today, loading, busy, error, notice, notes, workType, revision, record, canCheckIn, overnight, refresh, act }
})
