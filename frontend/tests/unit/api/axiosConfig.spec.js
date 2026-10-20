import { describe, it, expect, vi, beforeEach } from 'vitest'

describe('axiosConfig', () => {
  let apiClient
  let mockAuthStore
  let mockRouter

  beforeEach(async () => {
    vi.resetModules()
    
    mockAuthStore = {
      clearAuth: vi.fn()
    }
    
    mockRouter = {
      push: vi.fn()
    }
    
    vi.doMock('@/stores/authStore', () => ({
      useAuthStore: vi.fn(() => mockAuthStore)
    }))

    vi.doMock('@/utils/cookieUtils', () => ({
      getAuthCookie: vi.fn()
    }))

    vi.doMock('@/router', () => ({
      default: mockRouter
    }))

    const axiosModule = await import('@/api/axiosConfig')
    apiClient = axiosModule.default
  })

  it('creates axios instance with correct baseURL', () => {
    expect(apiClient.defaults.baseURL).toBe('http://localhost:8080/api/v1')
  })

  it('sets withCredentials to false', () => {
    expect(apiClient.defaults.withCredentials).toBe(false)
  })

  it('request interceptor adds Authorization header when token exists', async () => {
    const { getAuthCookie } = await import('@/utils/cookieUtils')
    getAuthCookie.mockReturnValue('test-token')

    const config = { headers: {} }
    const interceptor = apiClient.interceptors.request.handlers[0]
    
    const result = await interceptor.fulfilled(config)
    
    expect(result.headers.Authorization).toBe('Bearer test-token')
  })

  it('request interceptor does not add header when no token', async () => {
    const { getAuthCookie } = await import('@/utils/cookieUtils')
    getAuthCookie.mockReturnValue(null)

    const config = { headers: {} }
    const interceptor = apiClient.interceptors.request.handlers[0]
    
    const result = await interceptor.fulfilled(config)
    
    expect(result.headers.Authorization).toBeUndefined()
  })

  it('response interceptor returns response on success', async () => {
    const response = { data: { success: true }, status: 200 }
    const interceptor = apiClient.interceptors.response.handlers[0]
    
    const result = await interceptor.fulfilled(response)
    
    expect(result).toBe(response)
  })

  it('response interceptor handles 401 error', async () => {
    const error = {
      response: { status: 401 }
    }
    const interceptor = apiClient.interceptors.response.handlers[0]
    
    try {
      await interceptor.rejected(error)
    } catch (e) {
      expect(mockAuthStore.clearAuth).toHaveBeenCalled()
      expect(mockRouter.push).toHaveBeenCalledWith('/login')
      expect(e).toBe(error)
    }
  })

  it('response interceptor passes through non-401 errors', async () => {
    const error = {
      response: { status: 500 }
    }
    const interceptor = apiClient.interceptors.response.handlers[0]
    
    try {
      await interceptor.rejected(error)
    } catch (e) {
      expect(mockAuthStore.clearAuth).not.toHaveBeenCalled()
      expect(mockRouter.push).not.toHaveBeenCalled()
      expect(e).toBe(error)
    }
  })

  it('response interceptor handles errors without response', async () => {
    const error = new Error('Network Error')
    const interceptor = apiClient.interceptors.response.handlers[0]
    
    try {
      await interceptor.rejected(error)
    } catch (e) {
      expect(mockAuthStore.clearAuth).not.toHaveBeenCalled()
      expect(e).toBe(error)
    }
  })
})
