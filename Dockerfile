# ==============================================================================
# Multi-stage Dockerfile for SmartBiz Spring Boot Backend
# ==============================================================================

# Stage 1: Build the Application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy backend pom and sources
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B || true

COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Production Runtime Image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S smartbiz && adduser -S smartbiz -G smartbiz
USER smartbiz

COPY --from=builder --chown=smartbiz:smartbiz /app/target/smartbiz-backend-1.0.0-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT:-8080} -jar app.jar"]
