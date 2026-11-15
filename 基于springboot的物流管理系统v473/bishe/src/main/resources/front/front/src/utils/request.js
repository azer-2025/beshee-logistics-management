import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/wuliuguanlixitong',
  timeout: 10000
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('Token')
    if (token) {
      config.headers.token = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    if (response.data.code === 401) {
      window.dispatchEvent(new CustomEvent('showLoginModal', {
        detail: { message: '请先登录' }
      }))
      return Promise.reject(new Error('未登录'))
    }
    return response.data
  },
  (error) => {
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export default api