import api from './api'

// ========================================
// Q&A 기본 조회
// ========================================

export const getQnaList = () => {
  return api.get('/qna')
}

export const getQna = (id) => {
  return api.get(`/qna/${id}`)
}

export const getMyQna = () => {
  return api.get('/qna/my')
}

// ========================================
// 검색 / 필터
// ========================================

export const searchQna = (keyword) => {
  return api.get('/qna/search', {
    params: { keyword }
  })
}

export const getQnaByStatus = (status) => {
  return api.get(`/qna/status/${status}`)
}

export const getQnaByCategory = (category) => {
  return api.get(`/qna/category/${category}`)
}

// ========================================
// 문의 등록 / 수정 / 삭제
// ========================================

export const createQna = (data) => {
  return api.post('/qna', data)
}

export const updateQna = (id, data) => {
  return api.put(`/qna/${id}`, data)
}

export const deleteQna = (id) => {
  return api.delete(`/qna/${id}`)
}

// ========================================
// 관리자 답변
// ========================================

export const createQnaAnswer = (id, answer) => {
  return api.post(`/qna/${id}/answer`, {
    answer
  })
}

export const deleteQnaAnswer = (id) => {
  return api.delete(`/qna/${id}/answer`)
}
