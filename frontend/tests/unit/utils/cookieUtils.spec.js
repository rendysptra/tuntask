import { describe, it, expect, beforeEach } from 'vitest'
import { setAuthCookie, getAuthCookie } from '@/utils/cookieUtils'

describe('cookieUtils', () => {
  beforeEach(() => {
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: '',
    })
  })

  it('sets and gets auth cookie', () => {
    setAuthCookie('test-token')
    document.cookie = 'auth_token=test-token'
    expect(getAuthCookie()).toBe('test-token')
  })

  it('returns null when cookie not found', () => {
    document.cookie = ''
    expect(getAuthCookie()).toBe(null)
  })

  it('handles multiple cookies', () => {
    document.cookie = 'other=value; auth_token=my-token; another=test'
    expect(getAuthCookie()).toBe('my-token')
  })

  it('setAuthCookie creates cookie string', () => {
    setAuthCookie('token123')
    expect(document.cookie).toContain('auth_token=token123')
  })
})
