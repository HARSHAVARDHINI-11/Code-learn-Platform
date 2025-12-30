# CodeLearn Platform - Spring Boot Backend

A professional social learning platform for coding built with Spring Boot 3.2 and MongoDB.

## Prerequisites

- Java 17 or higher
- Maven 3.8+ (or use the included Maven wrapper)
- MongoDB running locally on port 27017

## Project Structure

```
backend-spring/
├── src/main/java/com/codelearn/
│   ├── CodeLearnApplication.java     # Main application entry point
│   ├── config/                       # Configuration classes
│   │   ├── SecurityConfig.java       # Spring Security configuration
│   │   ├── CorsConfig.java           # CORS configuration
│   │   └── OpenApiConfig.java        # Swagger/OpenAPI configuration
│   ├── controller/                   # REST API controllers
│   │   ├── AuthController.java       # Authentication endpoints
│   │   ├── PostController.java       # Posts CRUD operations
│   │   ├── GroupController.java      # Groups management
│   │   ├── ContestController.java    # Contests management
│   │   ├── DiscussionController.java # Comments and discussions
│   │   └── LeaderboardController.java# Rankings and leaderboards
│   ├── dto/                          # Data Transfer Objects
│   │   ├── request/                  # Request DTOs
│   │   └── response/                 # Response DTOs
│   ├── exception/                    # Custom exceptions and handlers
│   ├── model/                        # MongoDB document models
│   ├── repository/                   # MongoDB repositories
│   ├── security/                     # JWT security components
│   └── service/                      # Business logic services
└── src/main/resources/
    └── application.yml               # Application configuration
```

## Quick Start

### 1. Start MongoDB
Make sure MongoDB is running on `localhost:27017`

### 2. Run the Application

**Using Maven:**
```bash
cd backend-spring
mvn spring-boot:run
```

**Using the JAR:**
```bash
mvn clean package
java -jar target/codelearn-platform-1.0.0.jar
```

## API Documentation

Once the application is running, access the Swagger UI at:
- **Swagger UI**: http://localhost:5000/swagger-ui.html
- **OpenAPI JSON**: http://localhost:5000/api-docs

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| GET | `/api/auth/me` | Get current user |
| PUT | `/api/auth/profile` | Update profile |

### Posts
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/posts` | Get all posts |
| GET | `/api/posts/{id}` | Get post by ID |
| POST | `/api/posts` | Create post |
| PUT | `/api/posts/{id}` | Update post |
| DELETE | `/api/posts/{id}` | Delete post |
| PUT | `/api/posts/{id}/like` | Like/Unlike post |

### Groups
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/groups` | Get user's groups |
| GET | `/api/groups/all` | Get all public groups |
| GET | `/api/groups/{id}` | Get group by ID |
| POST | `/api/groups` | Create group |
| POST | `/api/groups/{id}/join` | Join group |
| DELETE | `/api/groups/{id}/leave` | Leave group |
| DELETE | `/api/groups/{id}` | Delete group |

### Contests
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/contests` | Get all contests |
| GET | `/api/contests/{id}` | Get contest by ID |
| POST | `/api/contests` | Create contest |
| POST | `/api/contests/{id}/submit` | Submit solution |
| PUT | `/api/contests/{id}/status` | Update status |
| DELETE | `/api/contests/{id}` | Delete contest |

### Discussions
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/discussions/{postId}` | Get discussion |
| POST | `/api/discussions/{postId}/comment` | Add comment |
| POST | `/api/discussions/{postId}/comment/{commentId}/reply` | Add reply |
| PUT | `/api/discussions/{postId}/comment/{commentId}/like` | Like comment |

### Leaderboard
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/leaderboard/college` | College ranking |
| GET | `/api/leaderboard/global` | Global ranking |
| GET | `/api/leaderboard/groups` | Group ranking |
| GET | `/api/leaderboard/department/{dept}` | Department ranking |

## Configuration

Environment variables (can be set in application.yml or as system properties):

| Variable | Default | Description |
|----------|---------|-------------|
| `MONGODB_URI` | `mongodb://localhost:27017/codelearn` | MongoDB connection string |
| `JWT_SECRET` | (generated) | JWT signing secret key |
| `SERVER_PORT` | `5000` | Server port |

## Security

- JWT-based authentication
- Password encryption using BCrypt
- CORS configured for frontend access
- Token sent via `x-auth-token` header

## Technologies

- **Framework**: Spring Boot 3.2.1
- **Security**: Spring Security with JWT
- **Database**: MongoDB with Spring Data
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build**: Maven
- **Java Version**: 17
