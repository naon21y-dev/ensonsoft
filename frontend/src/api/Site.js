import api from './api'

// ========================================
// 현장 기본 조회
// ========================================

// 전체 현장 조회
export const getSites = () => {
  return api.get('/sites')
}

// 현장 상세 조회
export const getSite = (id) => {
  return api.get(`/sites/${id}`)
}

// 현장 코드 조회
export const getSiteByCode = (siteCode) => {
  return api.get(`/sites/code/${siteCode}`)
}


// ========================================
// 현장 검색
// ========================================

// 현장명 검색
export const searchSitesByName = (name) => {
  return api.get('/sites/search/name', {
    params: { name }
  })
}

// 주소 검색
export const searchSitesByAddress = (address) => {
  return api.get('/sites/search/address', {
    params: { address }
  })
}


// ========================================
// 현장 필터
// ========================================

// 상태별 조회
export const getSitesByStatus = (status) => {
  return api.get(`/sites/status/${status}`)
}

// 유형별 조회
export const getSitesByType = (siteType) => {
  return api.get(`/sites/type/${siteType}`)
}


// ========================================
// 현장 등록
// ========================================

export const createSite = (data) => {
  return api.post('/sites', data)
}


// ========================================
// 현장 수정
// ========================================

export const updateSite = (id, data) => {
  return api.put(`/sites/${id}`, data)
}


// ========================================
// 현장 상태 변경
// ========================================

export const updateSiteStatus = (
  id,
  status,
  reason
) => {
  return api.patch(
    `/sites/${id}/status`,
    null,
    {
      params: {
        status,
        reason
      }
    }
  )
}


// ========================================
// 현장 상태 변경 이력
// ========================================

export const getSiteHistories = (id) => {
  return api.get(`/sites/${id}/histories`)
}


// ========================================
// 현장 삭제
// ========================================

export const deleteSite = (id) => {
  return api.delete(`/sites/${id}`)
}