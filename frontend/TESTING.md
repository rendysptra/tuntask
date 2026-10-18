# Frontend Testing Guide

## Setup

### Prerequisites
- Node.js 22+
- npm 10+

### Installation
```bash
cd frontend
npm install
```

## Test Commands

```bash
# Run tests in watch mode
npm run test

# Run tests once
npm run test:coverage

# Run linting
npm run lint

# Build for production
npm run build
```

## Coverage

**Target: 80% for all metrics**

Current coverage: **91.75%**

### Coverage Report
- **Services**: 100% (authApi, taskApi)
- **Stores**: 100% statements (authStore)
- **API Config**: 94.44% (axiosConfig)
- **Utils**: 88.88% (cookieUtils)
- **Router**: 76.59%

### Coverage Exclusions
The following are excluded from coverage:
- Demo components (HelloWorld, TheWelcome, mascots, icons)
- Page components (LoginPage, SignupPage, TasksPage)
- Config files (vite.config.js, vitest.config.js, eslint.config.js)
- Build artifacts (dist/)
- Entry point (main.js, App.vue)

## Test Structure

```
tests/
├── setup.js              # Global test setup
├── mocks/
│   └── axios.js          # Axios mock helpers
└── unit/
    ├── api/
    │   └── axiosConfig.spec.js
    ├── router/
    │   └── index.spec.js
    ├── services/
    │   ├── authApi.spec.js
    │   └── taskApi.spec.js
    ├── stores/
    │   └── authStore.spec.js
    └── utils/
        └── cookieUtils.spec.js
```

## CI/CD

Tests run automatically on:
- Push to `main` or `develop` branches
- Pull requests targeting `main` or `develop`

### CI Pipeline
1. Install dependencies
2. Lint check
3. Run tests with coverage
4. Verify 80% threshold
5. Build verification
6. Upload coverage to Codecov

## Adding New Tests

### For Services
```javascript
import { describe, it, expect, vi, beforeEach } from 'vitest'

vi.mock('@/api/axiosConfig', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

describe('myService', () => {
  it('does something', async () => {
    // test code
  })
})
```

### For Stores
```javascript
import { setActivePinia, createPinia } from 'pinia'

beforeEach(() => {
  setActivePinia(createPinia())
})
```

### For Components
```javascript
import { mount } from '@vue/test-utils'
import MyComponent from '@/components/MyComponent.vue'

it('renders correctly', () => {
  const wrapper = mount(MyComponent)
  expect(wrapper.html()).toContain('expected text')
})
```

## Environment Variables

Test environment uses `.env.test`:
```
VITE_API_URL=http://localhost:8080
```
