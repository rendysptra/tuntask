# Docker Setup Guide

## Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Frontend   │────▶│   Backend   │────▶│  PostgreSQL │     │  pgAdmin    │
│  (Nginx)    │     │  (Spring)   │     │             │     │             │
│  :3000      │     │  :8080      │     │  :5432      │     │  :5050      │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
       │                    │                    │                    │
       └────────────────────┴────────────────────┴────────────────────┘
                              task_network
```

## Services

### Frontend (Port 3000)
- **Image**: Node 22 Alpine (build) → Nginx Alpine (runtime)
- **Tech**: Vue 3 + Vite
- **Features**:
  - Multi-stage build (optimized image ~30MB)
  - Runtime environment variable injection
  - SPA routing support
  - Gzip compression
  - Health check endpoint

### Backend (Port 8080)
- **Image**: Eclipse Temurin 21 JDK (build) → JRE (runtime)
- **Tech**: Spring Boot 4.0.8
- **Features**:
  - Multi-stage build
  - Maven dependency caching
  - Connects to PostgreSQL via Docker network

### PostgreSQL (Port 5432)
- **Image**: PostgreSQL 16 Alpine
- **Data**: Persistent volume `postgres_data`
- **Health check**: pg_isready

### pgAdmin (Port 5050)
- **Image**: pgAdmin4 latest
- **Access**: Web UI for database management

## Environment Variables

Root `.env` file:

```env
# JWT
JWT_SECRET=your_jwt_secret_here

# Database
DB_NAME=task_db
DB_USER=postgres
DB_PASSWORD=your_password

# pgAdmin
PGADMIN_DEFAULT_EMAIL=admin@example.com
PGADMIN_DEFAULT_PASSWORD=admin_password
```

## Commands

### Build & Start All Services

```bash
docker-compose up --build
```

### Start (without rebuild)

```bash
docker-compose up
```

### Stop All Services

```bash
docker-compose down
```

### Stop & Remove Volumes (clean slate)

```bash
docker-compose down -v
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f frontend
docker-compose logs -f backend
```

### Rebuild Single Service

```bash
# Frontend only
docker-compose build frontend
docker-compose up -d frontend

# Backend only
docker-compose build backend
docker-compose up -d backend
```

## Development Workflow

### 1. Initial Setup

```bash
# Copy .env.example to .env and update values
cp .env.example .env

# Start all services
docker-compose up --build
```

### 2. Access Applications

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1
- **pgAdmin**: http://localhost:5050
- **PostgreSQL**: localhost:5432

### 3. Make Changes

**Frontend changes:**
```bash
# Rebuild frontend
docker-compose build frontend
docker-compose up -d frontend
```

**Backend changes:**
```bash
# Rebuild backend
docker-compose build backend
docker-compose up -d backend
```

### 4. Database Access

**Via pgAdmin (Web UI):**
1. Go to http://localhost:5050
2. Login with credentials from `.env`
3. Add server:
   - Host: `postgres` (Docker network)
   - Port: `5432`
   - Database: `task_db`
   - Username/Password: from `.env`

**Via psql (CLI):**
```bash
docker exec -it task_app_postgres psql -U postgres -d task_db
```

## Runtime Environment Configuration

Frontend API URL can be changed without rebuild:

```bash
# In docker-compose.yml, update:
services:
  frontend:
    environment:
      - VITE_API_URL=http://different-backend:8080/api/v1

# Restart
docker-compose restart frontend
```

## Troubleshooting

### Frontend can't connect to backend

Check network:
```bash
docker exec task_app_frontend ping backend
```

### Backend can't connect to database

Check PostgreSQL health:
```bash
docker-compose ps
docker logs task_app_postgres
```

Wait for PostgreSQL to be healthy before backend starts.

### Port already in use

```bash
# Find process using port 3000
lsof -i :3000
# Kill it
kill -9 <PID>
```

Or change port in `docker-compose.yml`:
```yaml
ports:
  - "3001:80"  # Use 3001 instead
```

### View container resource usage

```bash
docker stats
```

### Clean up everything

```bash
# Stop all containers
docker-compose down -v

# Remove all images
docker rmi task-app-frontend:test
docker rmi $(docker images -q task-app*)

# Remove unused images/volumes
docker system prune -a --volumes
```

## Production Considerations

1. **Use secrets management** for sensitive env vars
2. **Add reverse proxy** (Traefik, nginx) for SSL/TLS
3. **Resource limits** in docker-compose:
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1.0'
         memory: 512M
   ```
4. **Logging driver** for centralized logs
5. **Backup strategy** for postgres_data volume
6. **Health checks** for all services
7. **Update base images** regularly for security patches

## Image Sizes

- Frontend: ~30MB (nginx alpine + static files)
- Backend: ~200MB (JRE + JAR)
- PostgreSQL: ~240MB
- pgAdmin: ~420MB

Total: ~900MB
