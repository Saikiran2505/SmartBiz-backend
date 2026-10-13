# ==============================================================================
# Multi-stage Dockerfile for SmartBiz Spring Boot Backend on Render / Cloud
# ==============================================================================

# Stage 1: Build the Application with Maven 3.9 & Java 21
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy POM configuration first for layer caching
COPY pom.xml ./
RUN mvn dependency:go-offline -B || true

# Copy source files and package executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Production Runtime Image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as non-root user for security
RUN addgroup -S smartbiz && adduser -S smartbiz -G smartbiz
USER smartbiz

# Copy built JAR from builder stage
COPY --from=builder --chown=smartbiz:smartbiz /app/target/smartbiz-backend-1.0.0-SNAPSHOT.jar app.jar

# Render assigns port dynamically via the PORT environment variable (defaults to 8080)
ENV PORT=8080
EXPOSE 8080

# Run with container-aware memory limits suitable for cloud tiers (e.g., Render 512MB RAM)
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT:-8080} -jar app.jar"]
