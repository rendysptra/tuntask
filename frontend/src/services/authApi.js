import axios from 'axios'

const getApiUrl = () => {
  const runtimeUrl = window.env?.VITE_API_URL
  const isPlaceholder = runtimeUrl && runtimeUrl.includes('__VITE_API_URL__')
  if (runtimeUrl && !isPlaceholder) {
    return runtimeUrl
  }
  return import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1'
}

const authApi = axios.create({
  baseURL: getApiUrl(),
  withCredentials: false
})

export const signup = (email, password) => {
  return authApi.post('/auth/signup', { email, password })
}

export const login = (email, password) => {
  return authApi.post('/auth/login', { email, password })
}

export default authApi
