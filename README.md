# Fakebook Backend

Fakebook is a Spring Boot 3 application that provides Facebook-like social media features including posts, comments, likes, follows, and a personalized newsfeed.

## Features

- JWT-based authentication with secure password hashing (BCrypt)
- User profiles with bios and profile images
- Create, paginate, and delete posts (with optional images)
- Like and unlike posts with aggregated like counts
- Comment on posts with full CRUD support
- Follow/unfollow system plus follower/following listings
- Newsfeed showing recent posts from followed users (and yourself)
- Centralized error handling returning structured JSON
- MapStruct DTO mapping to keep controllers slim

## Tech Stack

- Java 17
- Spring Boot 3.x (Web, Data JPA, Security, Validation)
- PostgreSQL
- JWT (jjwt)
- Lombok & MapStruct
- Maven
- Docker & Docker Compose
- JUnit 5 + Mockito unit tests

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker (optional, for containerized setup)

### Running Locally

1. Start PostgreSQL (or use Docker Compose below).
2. Update `src/main/resources/application.yml` with your database credentials and ensure `jwt.secret` is a Base64-encoded string.
3. Build and run:

```bash
mvn spring-boot:run
```

### Using Docker Compose

Docker Compose starts both PostgreSQL and the application. The default JWT secret is Base64-encoded (`c2VjdXJlLXNlY3JldC1jaGFuZ2UtbWU=`). Override `JWT_SECRET` for production.

```bash
docker-compose up --build
```

The API will be available at `http://localhost:8080`.

### Running Tests

```bash
mvn test
```

## Example API Usage

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"password123"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"alice","password":"password123"}'
```

Copy the returned JWT token for the `Authorization: Bearer <token>` header in subsequent requests.

### Create a Post

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello Fakebook!"}'
```

### Follow a User

```bash
curl -X POST http://localhost:8080/api/follows/2 \
  -H "Authorization: Bearer $TOKEN"
```

### View Newsfeed

```bash
curl -X GET "http://localhost:8080/api/posts/feed?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

Additional endpoints are available for updating profiles, listing followers/following, managing comments, and liking/unliking posts.

## Configuration

Key properties (overridable via environment variables):

- `spring.datasource.url` – JDBC connection string
- `spring.datasource.username` / `spring.datasource.password`
- `jwt.secret` – **Base64-encoded** signing key
- `jwt.expiration` – Token lifetime in milliseconds

## Architecture

The backend follows a layered Spring Boot design with controllers delegating to services, repositories, and MapStruct mappers while JWT security guards every request. Detailed descriptions of the layers, domain entities, request flow, and Mermaid diagram sources are available in [`docs/architecture.md`](docs/architecture.md).

## License

MIT
