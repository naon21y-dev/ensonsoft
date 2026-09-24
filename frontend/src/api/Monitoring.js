import api from './api'

// ========================================
// 차량번호 인식 API
// ========================================

export const getVehicles = () => {
  return api.get('/monitoring/vehicles')
}

export const searchVehicles = (plateNumber) => {
  return api.get('/monitoring/vehicles/search', {
    params: { plateNumber }
  })
}

export const searchVehiclesByLocation = (location) => {
  return api.get('/monitoring/vehicles/location', {
    params: { location }
  })
}

export const getVehiclesByStatus = (status) => {
  return api.get(`/monitoring/vehicles/status/${status}`)
}

// ========================================
// 영상분석 이벤트 API
// ========================================

export const getEvents = () => {
  return api.get('/monitoring/events')
}

export const getEvent = (id) => {
  return api.get(`/monitoring/events/${id}`)
}

export const getEventsByStatus = (status) => {
  return api.get(`/monitoring/events/status/${status}`)
}

export const getEventsByType = (eventType) => {
  return api.get(`/monitoring/events/type/${eventType}`)
}

export const getEventsBySeverity = (severity) => {
  return api.get(`/monitoring/events/severity/${severity}`)
}

export const getEventsByLocation = (location) => {
  return api.get('/monitoring/events/location', {
    params: { location }
  })
}

// ========================================
// 이벤트 처리 이력
// ========================================

export const getEventHistories = (id) => {
  return api.get(`/monitoring/events/${id}/histories`)
}

// ========================================
// 이벤트 처리
// ========================================

export const startEventProcessing = (id) => {
  return api.put(`/monitoring/events/${id}/processing`)
}

export const completeEventProcessing = (id) => {
  return api.put(`/monitoring/events/${id}/complete`)
}
