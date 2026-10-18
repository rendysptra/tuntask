import axios from 'axios'

const API_URL = 'http://localhost:8080/api/v1'

const authApi = axios.create({
  baseURL: API_URL,
  withCredentials: false
})

export const signup = (email, password) => {
  return authApi.post('/auth/signup', { email, password })
}

export const login = (email, password) => {
  return authApi.post('/auth/login', { email, password })
}

export default authApi
