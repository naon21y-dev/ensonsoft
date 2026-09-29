import api from './api'

export const getMyAttendanceToday = () => api.get('/attendance/me/today')
export const getMyAttendanceHistory = params => api.get('/attendance/me/history', { params })
export const checkIn = data => api.post('/attendance/me/check-in', data)
export const checkOut = () => api.post('/attendance/me/check-out')
export const saveAttendanceNotes = notes => api.patch('/attendance/me/notes', { notes })
export const getAttendanceDay = params => api.get('/admin/attendance', { params })
export const getEmployeeAttendance = (id, params) => api.get(`/admin/attendance/members/${id}`, { params })
