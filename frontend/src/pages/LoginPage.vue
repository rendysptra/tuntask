<template>
  <div class="login-container">
    <div class="login-card">
      <h1>Login</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">Email</label>
          <input
            id="email"
            v-model="email"
            type="email"
            required
            placeholder="Enter your email"
            class="form-input"
          />
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="password"
            type="password"
            required
            placeholder="Enter your password"
            class="form-input"
          />
        </div>
        <button type="submit" :disabled="loading" class="submit-btn">
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>
      </form>
      <p v-if="error" class="error-message">{{ error }}</p>
      <p class="signup-link">
        Don't have an account?
        <router-link to="/signup">Sign up here</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    await authStore.handleLogin(email.value, password.value)
    router.push('/tasks')
  } catch (err) {
    error.value = err?.message || 'Login failed'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100dvh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: clamp(1rem, 5vw, 2rem);
  overflow-x: hidden;
}

.login-card {
  background: white;
  padding: clamp(1.5rem, 6vw, 3rem);
  border-radius: clamp(8px, 2vw, 12px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: min(90vw, 450px);
}

h1 {
  text-align: center;
  color: #1a202c;
  margin-bottom: clamp(1.5rem, 4vw, 2.5rem);
  font-size: clamp(1.5rem, 6vw, 2rem);
  font-weight: 700;
  letter-spacing: -0.5px;
}

form {
  display: flex;
  flex-direction: column;
  gap: clamp(1rem, 3vw, 1.5rem);
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  display: block;
  margin-bottom: clamp(0.5rem, 2vw, 0.75rem);
  color: #2d3748;
  font-weight: 600;
  font-size: clamp(0.85rem, 2vw, 0.95rem);
}

.form-input {
  width: 100%;
  padding: clamp(0.625rem, 2vw, 0.875rem) clamp(0.75rem, 2vw, 1rem);
  border: 2px solid #e2e8f0;
  border-radius: clamp(6px, 1vw, 8px);
  font-size: clamp(0.9rem, 2vw, 1rem);
  font-family: inherit;
  transition: all 0.2s ease;
  background: #f7fafc;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-input::placeholder {
  color: #a0aec0;
}

.submit-btn {
  width: 100%;
  padding: clamp(0.75rem, 2vw, 1rem);
  background: linear-gradient(135deg, #667eea 0%, #5568d3 100%);
  color: white;
  border: none;
  border-radius: clamp(6px, 1vw, 8px);
  font-size: clamp(0.9rem, 2vw, 1rem);
  font-weight: 600;
  cursor: pointer;
  margin-top: clamp(0.5rem, 1vw, 0.75rem);
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
  min-height: 44px;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.4);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #e53e3e;
  text-align: center;
  margin-top: clamp(0.75rem, 2vw, 1rem);
  padding: clamp(0.5rem, 2vw, 0.75rem);
  background: #fff5f5;
  border-radius: clamp(4px, 1vw, 6px);
  border-left: clamp(2px, 1vw, 4px) solid #e53e3e;
  font-size: clamp(0.85rem, 2vw, 0.95rem);
}

.signup-link {
  text-align: center;
  margin-top: clamp(1.5rem, 4vw, 2rem);
  color: #4a5568;
  font-size: clamp(0.85rem, 2vw, 0.95rem);
}

.signup-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s ease;
}

.signup-link a:hover {
  color: #5568d3;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-card {
    padding: clamp(1.25rem, 5vw, 1.75rem);
  }

  h1 {
    font-size: clamp(1.25rem, 5vw, 1.5rem);
    margin-bottom: clamp(1.25rem, 3vw, 1.75rem);
  }

  .form-group {
    gap: clamp(0.25rem, 1vw, 0.5rem);
  }

  .submit-btn {
    min-height: 48px;
  }
}
</style>
