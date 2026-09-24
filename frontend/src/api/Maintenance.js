import api from './api'

export const getMaintenances = () => api.get('/maintenance')
export const getMaintenance = (id) => api.get(`/maintenance/${id}`)
export const getMaintenancesByEquipment = (equipmentId) => api.get(`/maintenance/equipment/${equipmentId}`)
export const createMaintenance = (data) => api.post('/maintenance', data)
export const startMaintenanceProcessing = (id) => api.patch(`/maintenance/${id}/processing`)
export const completeMaintenance = (id, result) => api.patch(`/maintenance/${id}/complete`, { result })
export const getMaintenanceHistories = (id) => api.get(`/maintenance/${id}/histories`)
