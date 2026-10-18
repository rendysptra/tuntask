import { describe, it, expect, vi, beforeEach } from 'vitest'

describe('router', () => {
  let router
  let mockAuthStore

  beforeEach(async () => {
    vi.resetModules()
    
    mockAuthStore = {
      isAuthenticated: false
    }
    
    vi.doMock('@/stores/authStore', () => ({
      useAuthStore: vi.fn(() => mockAuthStore)
    }))

    vi.doMock('@/pages/LoginPage.vue', () => ({
      default: { template: '<div>Login</div>' }
    }))

    vi.doMock('@/pages/SignupPage.vue', () => ({
      default: { template: '<div>Signup</div>' }
    }))

    vi.doMock('@/pages/TasksPage.vue', () => ({
      default: { template: '<div>Tasks</div>' }
    }))

    const routerModule = await import('@/router/index.js')
    router = routerModule.default
  })

  it('has login route', () => {
    const loginRoute = router.getRoutes().find(r => r.name === 'Login')
    expect(loginRoute).toBeDefined()
    expect(loginRoute.path).toBe('/login')
  })

  it('has signup route', () => {
    const signupRoute = router.getRoutes().find(r => r.name === 'Signup')
    expect(signupRoute).toBeDefined()
    expect(signupRoute.path).toBe('/signup')
  })

  it('has tasks route with requiresAuth meta', () => {
    const tasksRoute = router.getRoutes().find(r => r.name === 'Tasks')
    expect(tasksRoute).toBeDefined()
    expect(tasksRoute.path).toBe('/tasks')
    expect(tasksRoute.meta.requiresAuth).toBe(true)
  })

  it('redirects root to tasks', () => {
    const rootRoute = router.getRoutes().find(r => r.path === '/')
    expect(rootRoute).toBeDefined()
    expect(rootRoute.redirect).toBe('/tasks')
  })

  it('beforeEach guard is defined', () => {
    expect(router.beforeEach).toBeDefined()
  })

  it('router has correct number of routes', () => {
    const routes = router.getRoutes()
    expect(routes.length).toBeGreaterThanOrEqual(4)
  })
})
