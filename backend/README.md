# Task Board — Backend

REST API for the Task Board application, built with Spring Boot 3.5.16 and Java 21.

## Requirements

- Java 21
- Maven (or use your IDE's built-in Maven support)
- Docker (to run MySQL) — see the root [docker-compose.yml](../docker-compose.yml)

## Running the application

1. **Start the database** from the repository root:
   ```bash
   docker-compose up -d
   ```
   This starts a MySQL 8.0 container named `taskboard-mysql`, exposing port `3307` (host) → `3306` (container), with database `taskboard`.

2. **Run the API**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   Or run `TaskBoardApiApplication.java` directly from your IDE.

   The API will be available at `http://localhost:8080`.

`spring.jpa.hibernate.ddl-auto=update` is set, so tables are created/updated automatically on startup — no manual migration step is needed.

## Configuration

Current configuration (`src/main/resources/application.properties`):

| Property | Value |
|---|---|
| Server port | `8080` |
| Database URL | `jdbc:mysql://localhost:3307/taskboard` |
| Database username | `root` |
| Database password | `root` |
| DDL strategy | `update` |
| Log level (app package) | `DEBUG` |

> **Note:** credentials are currently hardcoded in `application.properties` for simplicity, since this is a local assessment project. In a real production setup, these would be externalized via environment variables or a secrets manager. See [technical-decisions.md](../docs/technical-decisions.md) for details.

## API Documentation

Interactive API docs (Swagger UI):

```
http://localhost:8080/swagger-ui/index.html
```

### Main endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/tasks` | Create a new task |
| `GET` | `/tasks` | List all tasks (optionally filter by `?title=`) |
| `GET` | `/tasks/{id}` | Get a task by id |
| `PUT` | `/tasks/{id}` | Update a task |
| `DELETE` | `/tasks/{id}` | Delete a task |
| `PATCH` | `/tasks/{id}/status?status=` | Update only the task's status |
| `GET` | `/tasks/status/{status}` | List tasks by status |
| `GET` | `/tasks/search?title=` | Search tasks by title (starts with) |

## Logging

The application logs key business operations (create, update, delete, status change) at `INFO`/`DEBUG` level, and errors (validation failures, not-found resources, unexpected exceptions) at `WARN`/`ERROR` level, via SLF4J. Log level is configured in `application.properties`:

```properties
logging.level.com.saramageste.taskboard=DEBUG
```

## Running Tests

```bash
mvn test
```

This runs:
- `TaskServiceTest` — unit tests for business logic (create, find, update, delete, status change), using Mockito.
- `TaskControllerTest` — integration tests for the HTTP layer (status codes, validation, error handling), using MockMvc.