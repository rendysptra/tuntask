import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { signup, login } from '../services/authApi'
import { setAuthCookie, getAuthCookie, removeAuthCookie } from '../utils/cookieUtils'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(getAuthCookie() || null)

  const isAuthenticated = computed(() => !!token.value)

  const handleSignup = async (email, password) => {
    try {
      const response = await signup(email, password)
      const { token: newToken, email: userEmail } = response.data
      
      token.value = newToken
      user.value = { email: userEmail }
      setAuthCookie(newToken)
      
      return response.data
    } catch (error) {
      const errorMsg = error.response?.data?.message || error.response?.data?.error || error.message || 'Signup failed'
      throw new Error(errorMsg)
    }
  }

  const handleLogin = async (email, password) => {
    try {
      const response = await login(email, password)
      const { token: newToken, email: userEmail } = response.data
      
      token.value = newToken
      user.value = { email: userEmail }
      setAuthCookie(newToken)
      
      return response.data
    } catch (error) {
      const errorMsg = error.response?.data?.message || error.response?.data?.error || error.message || 'Login failed'
      throw new Error(errorMsg)
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    removeAuthCookie()
  }

  const clearAuth = () => {
    logout()
  }

  return {
    user,
    token,
    isAuthenticated,
    handleSignup,
    handleLogin,
    logout,
    clearAuth
  }
})
