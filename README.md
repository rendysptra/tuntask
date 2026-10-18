# Rendy Saptra Task App

Task management application dengan Vue.js (frontend) dan Spring Boot (backend).

## Setup

### Backend Setup

#### 1. Configure Environment Variables

Copy `.env.example` ke `.env`:

```bash
cd backend
cp .env.example .env
```

#### 2. Generate JWT_SECRET

Generate base64 string valid untuk JWT signing:

```bash
openssl rand -base64 64
```

Output akan berupa base64 string ~88 characters. Contoh:
```
vtFA4TRHOF8koYYlHJUVaGXELz/Dmd/bSG3FAselwBbck2SMllncpyUXUrJVsByQiagGedavURYMAyhnJ0jCLA==
```

#### 3. Update .env File

Edit `backend/.env` dan set JWT_SECRET dengan generated value:

```
JWT_SECRET=vtFA4TRHOF8koYYlHJUVaGXELz/Dmd/bSG3FAselwBbck2SMllncpyUXUrJVsByQiagGedavURYMAyhnJ0jCLA==
```

**Important:** 
- String harus valid base64 (only A-Z, a-z, 0-9, +, /, =)
- Jangan gunakan placeholder dengan underscore (_)
- Minimal 64 characters

#### 4. Update TaskApplication.java

Ensure `TaskApplication.java` load .env variables ke System properties:

```java
package com.rendysaptra.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class TaskApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		
		dotenv.entries().forEach(entry -> 
			System.setProperty(entry.getKey(), entry.getValue())
		);
		
		SpringApplication.run(TaskApplication.class, args);
	}

}
```

Key point: `dotenv.entries().forEach()` loop set setiap env var dari .env ke System properties sehingga Spring Boot @Value resolver bisa akses JWT_SECRET.

#### 5. Run Backend

```bash
cd backend
./mvnw spring-boot:run
```

Server akan start di `http://localhost:8080`

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend akan start di `http://localhost:5173`

## Troubleshooting

### Backend Error: "Could not resolve placeholder 'JWT_SECRET'"

**Cause:** .env file tidak exist atau Dotenv tidak set System properties.

**Fix:**
1. Pastikan `.env` file exist di `backend/` directory
2. Pastikan `TaskApplication.java` punya `dotenv.entries().forEach(entry -> System.setProperty(...))` code
3. Restart backend

### Backend Error: "Illegal base64 character: '_'"

**Cause:** JWT_SECRET value bukan valid base64 (punya underscore atau character tidak valid).

**Fix:**
1. Generate valid base64: `openssl rand -base64 64`
2. Update `.env` dengan generated value
3. Ensure string hanya punya: A-Z, a-z, 0-9, +, /, =

### Port 8080 Already in Use

**Fix:**
```bash
lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

## Tech Stack

- **Frontend:** Vue.js 3, Vite, Axios
- **Backend:** Spring Boot 4.0.8, Spring Security, JWT, JPA
- **Database:** H2 (dev), PostgreSQL (prod)
- **Java Version:** 21

## API Endpoints

- `POST /api/v1/auth/register` - Register user
- `POST /api/v1/auth/login` - Login user
- `GET /api/v1/tasks` - Get all tasks
- `POST /api/v1/tasks` - Create task
- `PUT /api/v1/tasks/{id}` - Update task
- `DELETE /api/v1/tasks/{id}` - Delete task
