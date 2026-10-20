import { describe, it, expect, vi, beforeEach } from 'vitest'

describe('authApi', () => {
  let mockPost
  let authApi

  beforeEach(async () => {
    vi.resetModules()
    
    mockPost = vi.fn()
    
    vi.doMock('axios', () => ({
      default: {
        create: vi.fn(() => ({
          post: mockPost,
        })),
      },
    }))

    authApi = await import('@/services/authApi')
  })

  it('signup sends correct data', async () => {
    mockPost.mockResolvedValue({ data: { token: 'abc123' } })
    
    await authApi.signup('test@example.com', 'password123')
    
    expect(mockPost).toHaveBeenCalledWith('/auth/signup', {
      email: 'test@example.com',
      password: 'password123'
    })
  })

  it('login sends correct data', async () => {
    mockPost.mockResolvedValue({ data: { token: 'xyz789' } })
    
    await authApi.login('user@example.com', 'mypassword')
    
    expect(mockPost).toHaveBeenCalledWith('/auth/login', {
      email: 'user@example.com',
      password: 'mypassword'
    })
  })
})
