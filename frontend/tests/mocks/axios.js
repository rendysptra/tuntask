import { vi } from 'vitest'

export const mockAxios = {
  post: vi.fn(),
  get: vi.fn(),
  put: vi.fn(),
  delete: vi.fn(),
}

export const resetMocks = () => {
  Object.values(mockAxios).forEach(mock => mock.mockReset())
}
