# TaskManager

## Overview

**TaskManager** is a Java-based project management system composed of two integrated parts:

1. **TaskManager Server** – a RESTful backend built with **Spring Boot** that manages projects, tasks, and their relationships.  
2. **TaskManager Client** – a **console application** that interacts with the server via REST API, enabling users to manage projects and tasks directly from the terminal.

The system allows users to:
- Create, edit, delete, and list tasks and projects.
- Assign tasks to projects and manage their relationships.
- Search, filter, and paginate data.
- Ensure data validation and error handling across both client and server sides.

---

## Architecture Overview

The project follows a **client-server architecture**, where:

- The **Server** provides REST endpoints under `/api/` for all operations.
- The **Client** communicates with these endpoints using Java’s `HttpClient` and displays results in a text-based menu interface.

### Components
| Layer | Description |
|--------|--------------|
| **Server** | REST API built with Spring Boot, exposing endpoints for project and task management. |
| **Client** | Java console interface allowing interaction with the server. |
| **DTOs** | Shared data structures exchanged between client and server (Project, Task, PageResponse). |
| **JSON Communication** | All communication between client and server uses JSON serialization handled by Jackson. |

---

## System Overview Diagram

```
+----------------------+       HTTP/JSON       +----------------------+
|   TaskManager Client | <-------------------> |  TaskManager Server  |
|  (Java Console App)  |                       |   (Spring Boot API)  |
|                      |                       |                      |
|  User Input & Menus  |                       |  Controllers         |
|  API Services        |                       |  Services            |
|  Console Handlers    |                       |  Repositories        |
+----------------------+                       +----------------------+
```

---

## Features

### Common
- Full CRUD operations for tasks and projects
- Relationship management between tasks and projects
- Search and filtering with multiple parameters
- Pagination and sorting support
- Centralized error handling
- Data validation on both sides

### Server
- Built with Spring Boot and Spring Data JPA
- REST API endpoints for `projects` and `tasks`
- Global exception handling
- Validation annotations (`@NotNull`, `@Size`, `@FutureOrPresent`)
- Dynamic filtering using JPA Specifications

### Client
- Console-based UI for user interaction
- Menu navigation system for all operations
- User input validation (IDs, enums, dates)
- Paginated search display with navigation (Next/Previous)
- HTTP requests handled via `HttpClient`
- JSON parsing with Jackson

---

## Technologies Used

| Category | Technology |
|-----------|-------------|
| Language | Java 17+ |
| Framework | Spring Boot |
| HTTP Client | java.net.http.HttpClient |
| JSON Processing | Jackson |
| ORM / Persistence | Spring Data JPA |
| Validation | Jakarta Validation |
| Lombok | Used in both client and server |
| Build Tool | Maven |
| Database | Configurable (PostgreSQL / H2) |

---

## Directory Structure

```
taskmanager/
│
├── server/                         # Spring Boot backend
│   ├── controller/
│   ├── dto/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repository/
│   ├── service/
│   ├── specification/
│   └── TaskManagerServerApplication.java
│
├── client/                         # Console-based frontend
│   ├── api/
│   ├── console/
│   ├── dto/
│   ├── enums/
│   ├── menu/
│   ├── Config.java
│   └── ConsoleApplication.java
│
└── README.md                       # General documentation (this file)
```

---

## API Overview

### Project Endpoints
| Method | Endpoint | Description |
|--------|-----------|-------------|
| `POST` | `/api/projects` | Create a new project |
| `GET` | `/api/projects` | Retrieve all projects |
| `GET` | `/api/projects/{id}` | Retrieve project by ID |
| `PUT` | `/api/projects/{id}` | Update existing project |
| `DELETE` | `/api/projects/{id}` | Delete a project |
| `GET` | `/api/projects/search` | Search projects with filters |
| `POST` | `/api/projects/{projectId}/tasks/{taskId}` | Assign task to project |
| `DELETE` | `/api/projects/{projectId}/tasks/{taskId}` | Remove task from project |

### Task Endpoints
| Method | Endpoint | Description |
|--------|-----------|-------------|
| `POST` | `/api/tasks` | Create a new task |
| `GET` | `/api/tasks` | Retrieve all tasks |
| `GET` | `/api/tasks/{id}` | Retrieve task by ID |
| `PUT` | `/api/tasks/{id}` | Update existing task |
| `DELETE` | `/api/tasks/{id}` | Delete a task |
| `GET` | `/api/tasks/search` | Search tasks by multiple parameters |

---

## Example JSON Communication

### Request – Create Task
```json
POST /api/tasks
{
  "name": "Implement login screen",
  "description": "Design and build UI for user authentication",
  "priority": "HIGH",
  "status": "IN_PROGRESS",
  "dueDate": "2025-12-01"
}
```

### Response
```json
{
  "id": 12,
  "name": "Implement login screen",
  "priority": "HIGH",
  "status": "IN_PROGRESS",
  "dueDate": "2025-12-01"
}
```

---

## Running the System

### 1. Run the Server
1. Navigate to the server directory.
2. Configure the database in `application.properties` (e.g., PostgreSQL or H2).
3. Build and start the server:
   ```
   mvn spring-boot:run
   ```
4. The API will be available at:
   ```
   http://localhost:8080/api/
   ```

### 2. Run the Client
1. Ensure the server is running.
2. Navigate to the client directory.
3. Start the console app:
   ```
   mvn exec:java -Dexec.mainClass="pl.kul.taskmanagerclient.ConsoleApplication"
   ```
4. Use the terminal menu to perform operations.

---

## Example Workflow

1. Start the server and client.  
2. Create a new project:  
   ```
   Name: Website Redesign
   Description: Update layout and content
   ```
3. Create a new task:  
   ```
   Name: Implement Homepage
   Priority: HIGH
   Status: TODO
   Due Date: 2025-12-01
   ```
4. Assign the task to the project (via menu option).  
5. Search for projects or tasks to verify association.

---

## Validation and Error Handling

- Server-side validation via Jakarta annotations.
- Client-side validation for:
  - Numeric IDs
  - Enum values (`Status`, `Priority`)
  - Date format (`yyyy-MM-dd`)
- Descriptive error messages printed in the console.
- Global exception handling ensures user-friendly feedback for invalid input or failed API requests.

---

## Future Improvements

- Add user authentication and role-based access.
- Extend the client with GUI (e.g., JavaFX or web frontend).
- Add task dependencies and progress tracking.
- Enable export/import of data (CSV or JSON).
- Dockerize both client and server for easy deployment.

---

## Author

Created by Mateusz Turowicz
