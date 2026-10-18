import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    globals: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      exclude: [
        'node_modules/',
        'tests/',
        'dist/',
        '**/components/HelloWorld.vue',
        '**/components/TheWelcome.vue',
        '**/components/WelcomeItem.vue',
        '**/components/icons/**',
        '**/components/mascots/**',
        '**/*.config.js',
        '**/main.js',
        'src/App.vue',
        '**/pages/**',
        'src/utils/colors.js',
      ],
      lines: 80,
      functions: 80,
      branches: 80,
      statements: 80,
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
})
