import { defineStore } from 'pinia'
import api from '../api/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('accessToken') || null,
    username: localStorage.getItem('username') || null,
    role: localStorage.getItem('role') || null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'ADMIN'
  },

  actions: {
    async login(username, password) {
      const response = await api.post('/auth/login', {
        username,
        password
      })

      const data = response.data

      this.token = data.accessToken
      this.username = data.username
      this.role = data.role

      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('username', data.username)
      localStorage.setItem('role', data.role)

      return data
    },

    logout() {
      this.token = null
      this.username = null
      this.role = null

      localStorage.removeItem('accessToken')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
    }
  }
})