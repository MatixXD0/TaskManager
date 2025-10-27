# TaskManager Server

## Overview
The **TaskManager Server** is a RESTful backend application built with **Spring Boot**.  
It provides endpoints for managing projects and tasks, including CRUD operations, search, filtering, and task assignment.  
The server communicates with the **TaskManager Client** application through HTTP requests and responses.

---

## Technologies

| Category | Technology |
|-----------|-------------|
| Language | Java 17+ |
| Framework | Spring Boot |
| Persistence | Spring Data JPA |
| Validation | Jakarta Validation |
| Database | PostgreSQL / H2 (configurable) |
| Build Tool | Maven |
| Architecture | REST API (Controller → Service → Repository) |

---

## Project Structure

```
pl.kul.taskmanager
│
├── controller         # REST controllers handling HTTP requests
├── dto                # Data Transfer Objects (input/output models)
├── exception          # Global exception handling and custom exceptions
├── mapper             # Manual entity–DTO mappers
├── model              # Entities and enums
├── repository         # Spring Data JPA repositories
├── service            # Business logic
├── specification      # Dynamic query specifications for filtering/search
└── TaskManagerServerApplication.java
```

---

## Architecture Overview

The server follows a **layered architecture**:

1. **Controller Layer** – receives HTTP requests, delegates processing to services.  
2. **Service Layer** – contains business logic and validation.  
3. **Repository Layer** – manages data persistence via JPA.  
4. **Model Layer** – defines database entities and enumerations.  
5. **DTO & Mapper Layers** – separate internal entity models from exposed API data.  
6. **Specification Layer** – enables advanced searching and filtering.  
7. **Exception Layer** – provides centralized exception handling.

---

## Main Features

### Projects
- Create, read, update, and delete projects.
- Search projects by `id`, `name`, and `description`.
- Paginate and sort search results.
- Add and remove tasks from a project.

### Tasks
- Create, read, update, and delete tasks.
- Filter tasks by:
  - Status (`TODO`, `IN_PROGRESS`, `BLOCKED`, `DONE`)
  - Priority (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`)
  - Project ID
  - Due date range
  - Search term (`name`, `description`)
- Paginate and sort search results.

---

## API Endpoints

### Project Endpoints (`/api/projects`)
| Method | Endpoint | Description |
|--------|-----------|-------------|
| `POST` | `/api/projects` | Create a new project |
| `GET` | `/api/projects` | Get all projects |
| `GET` | `/api/projects/{id}` | Get project by ID |
| `PUT` | `/api/projects/{id}` | Update project |
| `DELETE` | `/api/projects/{id}` | Delete project |
| `GET` | `/api/projects/search` | Search projects with filters |
| `POST` | `/api/projects/{projectId}/tasks/{taskId}` | Add task to project |
| `DELETE` | `/api/projects/{projectId}/tasks/{taskId}` | Remove task from project |

### Task Endpoints (`/api/tasks`)
| Method | Endpoint | Description |
|--------|-----------|-------------|
| `POST` | `/api/tasks` | Create a new task |
| `GET` | `/api/tasks` | Get all tasks |
| `GET` | `/api/tasks/{id}` | Get task by ID |
| `PUT` | `/api/tasks/{id}` | Update task |
| `DELETE` | `/api/tasks/{id}` | Delete task |
| `GET` | `/api/tasks/search` | Search tasks with filters |

---

## Data Transfer Objects (DTOs)

### ProjectRequestDTO
| Field | Type | Description |
|--------|------|-------------|
| `name` | `String` | Project name (3–100 characters) |
| `description` | `String` | Optional description (up to 500 characters) |

### ProjectResponseDTO
| Field | Type | Description |
|--------|------|-------------|
| `id` | `Long` | Project ID |
| `name` | `String` | Project name |
| `description` | `String` | Project description |
| `tasks` | `List<TaskResponseDTO>` | List of associated tasks |

### TaskResponseDTO
| Field | Type | Description |
|--------|------|-------------|
| `id` | `Long` | Task ID |
| `name` | `String` | Task name |
| `description` | `String` | Task description |
| `priority` | `Priority` | Task priority (`LOW`–`CRITICAL`) |
| `status` | `Status` | Task status (`TODO`–`DONE`) |
| `dueDate` | `LocalDate` | Task due date |
| `projectId` | `Long` | Associated project ID |

---

## Validation Rules

| Field | Rule |
|-------|------|
| Project name | 3–100 characters |
| Task name | 3–100 characters |
| Description | ≤ 500 characters |
| Due date | Must be today or in the future |
| ID parameters | Minimum value: 1 |

---

## Exception Handling

Handled globally by `GlobalExceptionHandler`:

| Exception | HTTP Status | Message Example |
|------------|-------------|-----------------|
| `TaskNotFoundException` | 404 | `Task not found with ID: 7` |
| `ProjectNotFoundException` | 404 | `Project not found with ID: 3` |
| `MethodArgumentNotValidException` | 400 | `name: must not be null` |
| `IllegalArgumentException` | 400 | `Task is not assigned to this project.` |
| `Exception` | 500 | `An unexpected error occurred: ...` |

---

## Search and Filtering (Specifications)

The application uses **Spring Data JPA Specifications** for flexible query generation.  
Each specification returns a `Predicate` that can be dynamically combined.

Example methods:
- `TaskSpecification.hasStatus(Status status)`
- `TaskSpecification.hasPriority(Priority priority)`
- `TaskSpecification.dueDateBeforeOrEqual(LocalDate date)`
- `ProjectSpecification.nameContains(String keyword)`

---

## Example Requests

### Create a Project
```
POST /api/projects
Content-Type: application/json

{
  "name": "Website Redesign",
  "description": "Frontend modernization for the company website."
}
```

### Create a Task
```
POST /api/tasks
Content-Type: application/json

{
  "name": "Create Landing Page",
  "description": "Design and implement the new landing page.",
  "priority": "HIGH",
  "status": "TODO",
  "dueDate": "2025-11-30"
}
```

### Assign Task to Project
```
POST /api/projects/1/tasks/5
```

### Example Error Response
```
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "error": "Project not found with ID: 99"
}
```

---

## Running the Application

1. Clone the repository:
   ```
   git clone https://github.com/your-username/taskmanager-server.git
   cd taskmanager-server
   ```

2. Configure your database in `src/main/resources/application.properties`:
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
   spring.datasource.username=yourusername
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Build and run the server:
   ```
   mvn spring-boot:run
   ```

4. The server will start at:
   ```
   http://localhost:8080/api/
   ```

---

## Notes

- The project uses manual mapping between entities and DTOs for full control and clarity.
- All validation annotations are based on `jakarta.validation`.
- Search endpoints support pagination and sorting using standard Spring parameters:
  - `page`, `size`, `sort` (e.g., `?page=0&size=10&sort=name,desc`)

## Author

Created by Mateusz Turowicz
