import api from './api'

export const getEquipments = (params = {}) => api.get('/equipments', { params })
export const getEquipment = (id) => api.get(`/equipments/${id}`)
export const getEquipmentByCode = (code) => api.get(`/equipments/code/${encodeURIComponent(code)}`)
export const getEquipmentsBySite = (siteId) => api.get(`/equipments/site/${siteId}`)
export const searchEquipmentsByName = (name) => api.get('/equipments/search/name', { params: { name } })
export const getEquipmentsByStatus = (status) => api.get(`/equipments/status/${status}`)
export const getEquipmentsByType = (type) => api.get(`/equipments/type/${type}`)
export const createEquipment = (data) => api.post('/equipments', data)
export const updateEquipment = (id, data) => api.put(`/equipments/${id}`, data)
export const updateEquipmentStatus = (id, status, reason) => api.patch(`/equipments/${id}/status`, null, { params: { status, reason } })
export const getEquipmentHistories = (id) => api.get(`/equipments/${id}/histories`)
export const deleteEquipment = (id) => api.delete(`/equipments/${id}`)
