import { describe, it, expect, vi, beforeEach } from 'vitest'

describe('taskApi', () => {
  let mockGet, mockPost, mockPut, mockDelete
  let taskApi

  beforeEach(async () => {
    vi.resetModules()
    
    mockGet = vi.fn()
    mockPost = vi.fn()
    mockPut = vi.fn()
    mockDelete = vi.fn()
    
    vi.doMock('@/api/axiosConfig', () => ({
      default: {
        get: mockGet,
        post: mockPost,
        put: mockPut,
        delete: mockDelete,
      },
    }))

    const module = await import('@/services/taskApi')
    taskApi = module.taskApi
  })

  it('getTasks calls correct endpoint', async () => {
    mockGet.mockResolvedValue({ data: [] })
    
    await taskApi.getTasks()
    
    expect(mockGet).toHaveBeenCalledWith('/tasks')
  })

  it('createTask sends correct data', async () => {
    const taskData = { title: 'New Task', description: 'Test' }
    mockPost.mockResolvedValue({ data: { id: 1, ...taskData } })
    
    await taskApi.createTask(taskData)
    
    expect(mockPost).toHaveBeenCalledWith('/tasks', taskData)
  })

  it('updateTask sends correct data', async () => {
    const taskData = { title: 'Updated', description: 'Changed' }
    mockPut.mockResolvedValue({ data: { id: 5, ...taskData } })
    
    await taskApi.updateTask(5, taskData)
    
    expect(mockPut).toHaveBeenCalledWith('/tasks/5', taskData)
  })

  it('deleteTask calls correct endpoint', async () => {
    mockDelete.mockResolvedValue({ data: { success: true } })
    
    await taskApi.deleteTask(10)
    
    expect(mockDelete).toHaveBeenCalledWith('/tasks/10')
  })
})
