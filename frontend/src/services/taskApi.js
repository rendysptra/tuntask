import apiClient from '../api/axiosConfig'

export const taskApi = {
  getTasks() {
    return apiClient.get('/tasks')
  },

  createTask(data) {
    return apiClient.post('/tasks', data)
  },

  updateTask(id, data) {
    return apiClient.put(`/tasks/${id}`, data)
  },

  deleteTask(id) {
    return apiClient.delete(`/tasks/${id}`)
  }
}
