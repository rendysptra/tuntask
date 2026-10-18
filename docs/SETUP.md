# Environment Setup Guide

## Prerequisites

- Java 21 or higher
- Maven 3.8 or higher
- OpenSSL (for generating JWT_SECRET)

## Backend Setup

### 1. Generate Secure JWT_SECRET

Generate a secure base64-encoded secret key (minimum 64 characters):

```bash
openssl rand -base64 64 | tr -d '\n'
```

Example output:
```
AbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYz==
```

### 2. Create `.env` File

Navigate to the backend directory and create a `.env` file from the template:

```bash
cd backend
cp .env.example .env
```

### 3. Configure JWT_SECRET

Edit `backend/.env` and paste the generated secret:

```
JWT_SECRET=AbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYz==
```

### 4. Install Dependencies

```bash
cd backend
./mvnw clean install
```

### 5. Run Backend Server

```bash
./mvnw spring-boot:run
```

The application will automatically load the `.env` file during startup and set the `JWT_SECRET` environment variable.

Server runs on: `http://localhost:8080`

## Testing Authentication Endpoints

### Sign Up

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Response (201 Created):
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "user@example.com"
}
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Response (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "user@example.com"
}
```

## Troubleshooting

### Issue: `Could not resolve placeholder 'JWT_SECRET'`

**Solution:** Ensure `.env` file exists in the `backend/` directory and contains `JWT_SECRET` value.

```bash
# Verify .env exists
ls -la backend/.env

# Check JWT_SECRET is set
grep JWT_SECRET backend/.env
```

### Issue: `Illegal base64 character`

**Solution:** Ensure JWT_SECRET is valid base64 (no newlines). When generating with openssl, use:

```bash
openssl rand -base64 64 | tr -d '\n'
```

### Issue: 403 Forbidden on Postman

**Solution:** Ensure proper CORS headers. The backend supports all origins for development. In Postman:
- Set `Content-Type: application/json` header
- Use `http://localhost:8080/api/v1/auth/signup` (not `https`)

## Important Notes

- **Never commit `.env` file** - it contains sensitive data
- `.env` is git-ignored by default (see `.gitignore`)
- Each developer must generate and configure their own JWT_SECRET
- For production, use secure environment variable management (e.g., AWS Secrets Manager, HashiCorp Vault)

## Production Deployment

For production environments:

1. Remove `.env` file usage
2. Set `JWT_SECRET` via environment variables in your deployment platform
3. Use HTTPS instead of HTTP
4. Update CORS configuration to allow only trusted origins
5. Use stronger passwords and consider OAuth2/OpenID Connect

Example production setup with environment variables:

```bash
export JWT_SECRET="your_production_secret_key"
./mvnw spring-boot:run
```
