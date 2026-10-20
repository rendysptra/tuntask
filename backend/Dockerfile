# Multi-stage build untuk Spring Boot backend
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper dan pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build aplikasi
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy jar dari build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run aplikasi
ENTRYPOINT ["java", "-jar", "app.jar"]
