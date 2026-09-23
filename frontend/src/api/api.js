import axios from 'axios'

const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL || 'http://localhost:8081'}/api`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// JWT가 있으면 모든 요청에 자동으로 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => Promise.reject(error)
)

export default api
