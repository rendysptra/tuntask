import axios from 'axios'
import { useAuthStore } from '../stores/authStore'
import { getAuthCookie } from '../utils/cookieUtils'
import router from '../router'

const getApiUrl = () => {
  return import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1'
}

const apiClient = axios.create({
  baseURL: getApiUrl(),
  withCredentials: false
})

apiClient.interceptors.request.use(
  (config) => {
    const token = getAuthCookie()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.clearAuth()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export default apiClient
