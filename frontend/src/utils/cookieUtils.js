const AUTH_COOKIE_NAME = 'auth_token'
const COOKIE_MAX_AGE = 86400

export const setAuthCookie = (token) => {
  document.cookie = `${AUTH_COOKIE_NAME}=${token}; max-age=${COOKIE_MAX_AGE}; path=/; SameSite=Strict`
}

export const getAuthCookie = () => {
  const cookies = document.cookie.split('; ')
  for (const cookie of cookies) {
    const [name, value] = cookie.split('=')
    if (name === AUTH_COOKIE_NAME) {
      return value
    }
  }
  return null
}

export const removeAuthCookie = () => {
  document.cookie = `${AUTH_COOKIE_NAME}=; max-age=0; path=/`
}
