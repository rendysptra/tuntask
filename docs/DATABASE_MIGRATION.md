# Database Migration: H2 to PostgreSQL

## Overview
Migrasi database dari H2 ke PostgreSQL untuk production-ready environment.

## Changes Made

### 1. Dependencies (pom.xml)
- **Removed**: `spring-boot-h2console`, `h2` dependency
- **Kept**: PostgreSQL driver `org.postgresql:postgresql` (sudah ada)

### 2. Configuration (application.properties)
Tambah konfigurasi PostgreSQL:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
```

### 3. Environment Variables (.env)
```
JWT_SECRET=your_secret_key
DB_NAME=task_db
DB_USER=postgres
DB_PASSWORD=postgres
```

### 4. Security Configuration (SecurityConfig.java)
- Removed: `/h2-console/**` permitAll
- Removed: Frame options disable (tidak perlu untuk PostgreSQL)

### 5. Docker Setup (docker-compose.yml)
PostgreSQL container configuration dengan:
- Image: postgres:16-alpine
- Port: 5432
- Database: task_db
- Username: postgres
- Password: postgres
- Volume: postgres_data untuk persistence

## Setup Instructions

### Prerequisites
- Docker & Docker Compose installed
- Java 21
- Maven

### Steps

1. **Start PostgreSQL**
   ```bash
   docker-compose up -d
   ```

2. **Verify Database Connection**
   ```bash
   docker-compose ps
   ```
   Status harus "healthy"

3. **Build Backend**
   ```bash
   cd backend
   mvn clean install
   ```

4. **Run Application**
   ```bash
   mvn spring-boot:run
   ```
   
   Aplikasi otomatis create tables berdasarkan entities (ddl-auto=update)

5. **Verify Tables Created**
   ```bash
   docker exec -it task_app_postgres psql -U postgres -d task_db -c "\dt"
   ```

### Environment Variables
Customize di `.env`:
- `DB_NAME`: Nama database (default: task_db)
- `DB_USER`: PostgreSQL user (default: postgres)
- `DB_PASSWORD`: PostgreSQL password (ganti di production!)

### Stopping Services
```bash
docker-compose down
```

Untuk reset database:
```bash
docker-compose down -v
```
(Flag `-v` menghapus volumes)

## Verification

Tables created otomatis saat app start:
```
 Schema | Name  | Type  |  Owner   
--------+-------+-------+----------
 public | tasks | table | postgres
 public | users | table | postgres
```

Users table structure:
```
Column   |            Type             | Nullable | Default 
---------|-----------------------------+----------|--------
id       | uuid                        | not null | 
email    | character varying(255)      | not null | unique
password | character varying(255)      | not null | 
created_at | timestamp(6) with time zone | not null | 
```

Tasks table: similar UUID structure dengan enums untuk status & priority.

## Viewing Database

### Via Command Line (psql)
```bash
# List all tables
docker exec task_app_postgres psql -U postgres -d task_db -c "\dt"

# View users
docker exec task_app_postgres psql -U postgres -d task_db -c "SELECT id, email, created_at FROM users;"

# View tasks
docker exec task_app_postgres psql -U postgres -d task_db -c "SELECT id, title, status, priority, created FROM tasks;"

# Full user details (dengan password hash)
docker exec task_app_postgres psql -U postgres -d task_db -c "SELECT * FROM users;"
```

### Via pgAdmin (GUI)
```bash
# Start pgAdmin
docker compose up -d pgadmin

# Access: http://localhost:5050
# Login: admin@example.com / admin

# Add Server:
# - Hostname: postgres
# - Port: 5432
# - Username: postgres
# - Password: postgres
```

## Testing

### API Test
```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Get Tasks (dengan JWT token)
curl -X GET http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer <token>"
```

## Troubleshooting

### Connection Refused
```
Cek docker-compose status:
docker-compose ps

Start jika belum running:
docker-compose up -d
```

### Permission Denied (port 5432)
```
Port 5432 sudah terpakai:
docker ps
docker stop <container_id>

Atau ganti port di docker-compose.yml:
ports:
  - "5433:5432"
```

### Database Not Found
```
Verify database exists:
docker exec -it task_app_postgres psql -U postgres -l

Jika tidak ada, Spring Boot akan create otomatis saat start
```

## Migration from H2 Data

Jika ada data H2 yang perlu di-migrate:
1. Export data dari H2 (jika ada)
2. Transform ke PostgreSQL compatible format
3. Import ke PostgreSQL

Note: Struktur H2 dan PostgreSQL sudah compatible di app ini (JPA handles conversion).

## Production Deployment

Untuk production:
1. Ganti password di `.env.example` dan gunakan secrets manager
2. Set `spring.jpa.hibernate.ddl-auto=validate` (jangan update/create)
3. Setup database backups
4. Enable SSL untuk koneksi database
5. Gunakan managed database service (AWS RDS, GCP Cloud SQL, dll)

```properties
# Production config
spring.datasource.url=jdbc:postgresql://prod-db-host:5432/task_db?sslmode=require
spring.jpa.hibernate.ddl-auto=validate
```
