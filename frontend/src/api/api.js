import axios from 'axios'

const api = axios.create({
  // Render 백엔드 주소로 강제 고정
  baseURL: 'https://ensonsoft-api.onrender.com/api',

  headers: {
    'Content-Type': 'application/json'
  },

  timeout: 30000
})

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')

    // 로그인 후 요청에만 JWT 자동 추가
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    console.log(
      '[API 요청]',
      config.method?.toUpperCase(),
      `${config.baseURL}${config.url}`
    )

    return config
  },
  (error) => {
    console.error('[API 요청 오류]', error)
    return Promise.reject(error)
  }
)

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    console.log(
      '[API 성공]',
      response.status,
      response.config.url,
      response.data
    )

    return response
  },

  (error) => {
    console.error('[API 실패]', {
      url: error.config?.url,
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    })

    return Promise.reject(error)
  }
)

export default api
