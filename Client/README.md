# TaskManager Client

## Overview
The **TaskManager Client** is a Java-based console application designed to interact with the **TaskManager Server** via REST API.  
It provides a user-friendly text interface for managing projects and tasks, allowing users to perform all major operations (create, edit, delete, search) directly from the terminal.

The client communicates with the backend through the `HttpClient` API and exchanges JSON data using Jackson.

---

## Technologies

| Category | Technology |
|-----------|-------------|
| Language | Java 17+ |
| HTTP Communication | Java `HttpClient` (java.net.http) |
| JSON Processing | Jackson (`ObjectMapper`) |
| Architecture | Layered console client (API → Console → Menu) |
| Dependencies | Lombok |
| Build Tool | Maven |

---

## Architecture Overview

The client is built around three primary layers:

1. **API Layer (`api`)**  
   Handles HTTP communication with the server. Each API service corresponds to an entity (`ProjectApiService`, `TaskApiService`).

2. **Console Layer (`console`)**  
   Provides interactive input/output in the terminal. Each handler manages operations for a specific entity (`ProjectConsoleHandler`, `TaskConsoleHandler`).

3. **Menu Layer (`menu`)**  
   Displays navigation menus and routes user actions to the appropriate console handlers.

The application entry point is `ConsoleApplication.java`.

---

## Project Structure

```
pl.kul.taskmanagerclient
│
├── api                 # Classes for HTTP communication (BaseApiService, ProjectApiService, TaskApiService)
├── console             # Classes handling user input/output (ProjectConsoleHandler, TaskConsoleHandler)
├── dto                 # Data transfer objects used between client and server
├── enums               # Enums for Priority and Status
├── menu                # Menu system for navigation
├── Config.java         # Base server configuration (BASE_URL)
└── ConsoleApplication.java  # Application entry point
```

---

## Main Features

### General
- Interactive text-based UI
- Full CRUD operations on tasks and projects
- Task–project association management
- Searching, filtering, and paginated browsing
- Validation of user input (IDs, enums, dates)

### Project Management
- List all projects with tasks
- Create, edit, and delete projects
- Assign and remove tasks within projects
- Search projects by ID, name, or description

### Task Management
- List all tasks
- Create, edit, and delete tasks
- Set task priority and status
- Assign or unassign tasks to/from projects
- Search tasks by ID, name, status, or priority
- Paginate through search results

---

## Menu Overview

### Main Menu
```
--- Task Manager Client ---
1. Show all tasks
2. Create a new task
3. Delete a task
4. Edit a task
5. Show all projects
6. Create a new project
7. Delete a project
8. Edit a project
9. Add task to project
10. Remove task from project
11. Show project with tasks
12. Search
0. Exit
```

### Search Menu
```
--- Search Menu ---
1. Search Tasks
2. Search Projects
0. Back to Main Menu
```

---

## Key Components

### BaseApiService
A reusable foundation for all API services.  
Provides:
- Request building (`createRequest`)
- Query parameter handling (`buildUrlWithQuery`)
- JSON serialization/deserialization
- Error and response handling

### ProjectApiService / TaskApiService
Encapsulates HTTP operations related to **projects** and **tasks**:
- CRUD operations
- Search endpoints (`/search`)
- Task–project linking (POST/DELETE on `/projects/{id}/tasks/{id}`)

### ProjectConsoleHandler / TaskConsoleHandler
Implements user-facing logic:
- Reads console input
- Displays formatted output
- Handles exceptions gracefully
- Validates and sanitizes user data

### MenuHandler
Controls navigation between menus and invokes appropriate console handlers.

---

## Data Transfer Objects (DTOs)

### ProjectDto
| Field | Type | Description |
|--------|------|-------------|
| `id` | `Long` | Project ID |
| `name` | `String` | Project name |
| `description` | `String` | Optional description |
| `tasks` | `List<TaskDto>` | Associated tasks |

### TaskDto
| Field | Type | Description |
|--------|------|-------------|
| `id` | `Long` | Task ID |
| `name` | `String` | Task name |
| `description` | `String` | Optional description |
| `priority` | `Priority` | LOW, MEDIUM, HIGH, CRITICAL |
| `status` | `Status` | TODO, IN_PROGRESS, BLOCKED, DONE |
| `dueDate` | `LocalDate` | Due date (optional) |
| `projectId` | `Long` | Linked project ID (optional) |

### PageResponse<T>
Generic wrapper for paginated results from the server.

| Field | Type | Description |
|--------|------|-------------|
| `content` | `List<T>` | List of results |
| `pageNumber` | `int` | Current page number |
| `pageSize` | `int` | Number of items per page |
| `totalPages` | `int` | Total number of pages |
| `totalElements` | `long` | Total number of elements |
| `first` / `last` | `boolean` | Page position indicators |

---

## Enums

### Priority
```
LOW
MEDIUM
HIGH
CRITICAL
```

### Status
```
TODO
IN_PROGRESS
BLOCKED
DONE
```

---

## Configuration

The base URL for server communication is defined in `Config.java`:

```java
public class Config {
    public static final String BASE_URL = "http://localhost:8080/api";
}
```

If the server runs on a different port or host, modify this constant.

---

## Example Workflows

### Creating a New Task
1. Choose option `2` (Create a new task).  
2. Enter:
   - Task name
   - Description (optional)
   - Priority (LOW/MEDIUM/HIGH/CRITICAL)
   - Status (TODO/IN_PROGRESS/BLOCKED/DONE)
   - Due date (yyyy-MM-dd or leave blank)
3. The new task is sent via `POST /api/tasks`.

### Assigning a Task to a Project
1. Choose option `9` (Add task to project).  
2. Enter project ID and task ID.  
3. The client sends `POST /api/projects/{projectId}/tasks/{taskId}`.

### Searching Tasks
1. Choose option `12` → `1` (Search Tasks).  
2. Select search parameter (ID, Name, Status, Priority).  
3. Enter search term.  
4. Results are displayed page by page with navigation options (N, P, Q).

---

## Error Handling

- Invalid input (IDs, enums, or dates) triggers validation messages in the console.
- API errors (e.g., 404, 400) are displayed with readable messages from server responses.
- Network or deserialization issues are caught and printed via `handleException`.

---

## Running the Application

1. Ensure the **TaskManager Server** is running:
   ```
   http://localhost:8080/api/
   ```

2. Build and run the client:
   ```
   mvn clean compile exec:java -Dexec.mainClass="pl.kul.taskmanagerclient.ConsoleApplication"
   ```

3. Follow on-screen prompts to manage tasks and projects.

---

## Notes

- The client is fully decoupled from the server’s implementation and communicates via REST only.  
- `HttpClient` ensures compatibility without external HTTP libraries.  
- The code uses **Lombok** for boilerplate reduction (`@Data`, `@RequiredArgsConstructor`).  
- The `ObjectMapper` is configured with `findAndRegisterModules()` to handle `LocalDate` serialization.

## Author

Created by Mateusz Turowicz
