import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

vi.mock('@/services/authApi', () => ({
  signup: vi.fn(),
  login: vi.fn(),
}))

vi.mock('@/utils/cookieUtils', () => ({
  setAuthCookie: vi.fn(),
  getAuthCookie: vi.fn(() => null),
  removeAuthCookie: vi.fn(),
}))

describe('authStore', () => {
  let store

  beforeEach(async () => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    const { useAuthStore } = await import('@/stores/authStore')
    store = useAuthStore()
  })

  it('initializes with null token', () => {
    expect(store.token).toBe(null)
    expect(store.isAuthenticated).toBe(false)
  })

  it('logout clears auth state', async () => {
    const { removeAuthCookie } = await import('@/utils/cookieUtils')
    
    store.token = 'test-token'
    store.user = { email: 'test@example.com' }
    
    store.logout()
    
    expect(store.token).toBe(null)
    expect(store.user).toBe(null)
    expect(removeAuthCookie).toHaveBeenCalled()
  })

  it('isAuthenticated computed works', () => {
    expect(store.isAuthenticated).toBe(false)
    
    store.token = 'some-token'
    expect(store.isAuthenticated).toBe(true)
    
    store.token = null
    expect(store.isAuthenticated).toBe(false)
  })

  it('clearAuth calls logout', async () => {
    const { removeAuthCookie } = await import('@/utils/cookieUtils')
    store.token = 'token123'
    
    store.clearAuth()
    
    expect(store.token).toBe(null)
    expect(removeAuthCookie).toHaveBeenCalled()
  })

  it('handleSignup success sets token and user', async () => {
    const { signup } = await import('@/services/authApi')
    const { setAuthCookie } = await import('@/utils/cookieUtils')
    
    signup.mockResolvedValue({
      data: { token: 'signup-token', email: 'newuser@example.com' }
    })
    
    const result = await store.handleSignup('newuser@example.com', 'password')
    
    expect(store.token).toBe('signup-token')
    expect(store.user).toEqual({ email: 'newuser@example.com' })
    expect(setAuthCookie).toHaveBeenCalledWith('signup-token')
    expect(result).toEqual({ token: 'signup-token', email: 'newuser@example.com' })
  })

  it('handleSignup failure throws error', async () => {
    const { signup } = await import('@/services/authApi')
    
    const error = new Error('Network error')
    error.response = { data: { message: 'Email already exists' } }
    signup.mockRejectedValue(error)
    
    await expect(store.handleSignup('existing@example.com', 'pass'))
      .rejects.toThrow('Email already exists')
    
    expect(store.token).toBe(null)
    expect(store.user).toBe(null)
  })

  it('handleLogin success sets token and user', async () => {
    const { login } = await import('@/services/authApi')
    const { setAuthCookie } = await import('@/utils/cookieUtils')
    
    login.mockResolvedValue({
      data: { token: 'login-token', email: 'user@example.com' }
    })
    
    const result = await store.handleLogin('user@example.com', 'password')
    
    expect(store.token).toBe('login-token')
    expect(store.user).toEqual({ email: 'user@example.com' })
    expect(setAuthCookie).toHaveBeenCalledWith('login-token')
    expect(result).toEqual({ token: 'login-token', email: 'user@example.com' })
  })

  it('handleLogin failure throws error', async () => {
    const { login } = await import('@/services/authApi')
    
    const error = new Error('Unauthorized')
    error.response = { data: { error: 'Invalid credentials' } }
    login.mockRejectedValue(error)
    
    await expect(store.handleLogin('wrong@example.com', 'wrongpass'))
      .rejects.toThrow('Invalid credentials')
    
    expect(store.token).toBe(null)
    expect(store.user).toBe(null)
  })

  it('handleLogin handles network error', async () => {
    const { login } = await import('@/services/authApi')
    
    const error = new Error('Network Error')
    error.response = undefined
    login.mockRejectedValue(error)
    
    await expect(store.handleLogin('user@example.com', 'pass'))
      .rejects.toThrow('Network Error')
  })
})
