# Multi-stage build
FROM gradle:7.6.1-jdk17 AS builder

# Set working directory
WORKDIR /app

# Copy Gradle files
COPY gradle gradle
COPY build.gradle .

# Copy source code
COPY src src

# Build the application
RUN gradle build --no-daemon

# Runtime stage
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/bookmark-servies-*.jar app.jar

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Expose port 8080
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
