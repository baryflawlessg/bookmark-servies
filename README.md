# BookVerse - Backend API

A Spring Boot REST API for the BookVerse book review platform.

## Features

- User authentication with JWT
- Book management and search
- Review and rating system
- User profiles and favorites
- AI-powered book recommendations
- RESTful API with OpenAPI documentation

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT
- **Spring Data JPA**
- **PostgreSQL** (Supabase)
- **Gradle**
- **OpenAPI/Swagger**

## Prerequisites

- Java 17 or higher
- Gradle 8.5 or higher

## Getting Started

### 1. Clone the repository
```bash
git clone <repository-url>
cd bookmark-servies
```

### 2. Build the project
```bash
./gradlew build
```

### 3. Run the application
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 4. Access the API

- **API Base URL**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`

## API Documentation

Once the application is running, you can access the interactive API documentation at:
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/api-docs`

## Development

### Project Structure
```
src/main/java/com/bookverse/
├── controller/     # REST controllers
├── service/        # Business logic
├── repository/     # Data access layer
├── entity/         # JPA entities
├── dto/           # Data transfer objects
├── config/        # Configuration classes
├── security/      # Security configuration
└── exception/     # Custom exceptions
```

### Running Tests
```bash
./gradlew test
```

### Code Coverage
```bash
./gradlew test jacocoTestReport
```

## Environment Variables

- `OPENAI_API_KEY`: OpenAI API key for AI recommendations (optional)

## Docker

### Build Docker image
```bash
docker build -t bookverse-backend .
```

### Run with Docker
```bash
docker run -p 8080:8080 bookverse-backend
```

## Contributing

1. Follow the existing code style
2. Write tests for new features
3. Update documentation as needed
4. Ensure all tests pass before submitting

## License

This project is licensed under the MIT License.
