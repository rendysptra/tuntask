import { vi } from 'vitest'
import axios from 'axios'

vi.mock('axios')

axios.post = vi.fn()
axios.get = vi.fn()
axios.put = vi.fn()
axios.delete = vi.fn()

globalThis.process = { env: { VITE_API_URL: 'http://localhost:8080' } }
