package pl.kul.taskmanagerclient.console;

import pl.kul.taskmanagerclient.api.TaskApiService;
import pl.kul.taskmanagerclient.dto.PageResponse;
import pl.kul.taskmanagerclient.dto.TaskDto;
import pl.kul.taskmanagerclient.enums.Priority;
import pl.kul.taskmanagerclient.enums.Status;

import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

public class TaskConsoleHandler extends BaseConsoleHandler {
    private final TaskApiService taskApi;

    public TaskConsoleHandler(TaskApiService taskApi, Scanner scanner) {
        super(scanner);
        this.taskApi = taskApi;
    }

    private void printTask(TaskDto task) {
        String dueDate = task.getDueDate() != null ? task.getDueDate().toString() : "No due date";
        String projectId = task.getProjectId() != null ? task.getProjectId().toString() : "No project";

        System.out.printf(
                "ID: %d | Name: %s | Priority: %s | Status: %s | Due Date: %s | Project ID: %s\n",
                task.getId(),
                task.getName(),
                task.getPriority(),
                task.getStatus(),
                dueDate,
                projectId
        );
    }

    public void showAllTasks() {
        try {
            var tasks = taskApi.getAllTasks();
            if (tasks.isEmpty()) {
                System.out.println("No tasks found.");
            } else {
                System.out.println("\n--- Task List ---");
                tasks.forEach(this::printTask);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void createTask() {
        try {
            TaskDto task = new TaskDto();

            task.setName(promptForNonEmptyInput("Task name: "));
            task.setDescription(promptInput("Description (optional): "));
            task.setPriority(promptForValidPriority());
            task.setStatus(promptForValidStatus());
            task.setDueDate(promptForValidDate());

            TaskDto createdTask = taskApi.createTask(task);
            System.out.println("Task created successfully with ID: " + createdTask.getId());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void deleteTask() {
        try {
            Long id = promptForValidId("Enter task ID to delete: ");
            taskApi.deleteTask(id);
            System.out.println("Task deleted successfully!");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void editTask() {
        try {
            Long id = promptForValidId("Enter task ID to edit: ");
            TaskDto task = taskApi.getTaskById(id);

            displayCurrentTask(task);

            updateTaskName(task);
            updateTaskDescription(task);
            updateTaskPriority(task);
            updateTaskStatus(task);
            updateTaskProject(task);

            TaskDto updatedTask = taskApi.updateTask(id, task);
            System.out.println("Task updated successfully:");
            printTask(updatedTask);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void searchTasks() {
        try {
            System.out.println("\n--- Search Tasks ---");
            System.out.println("Choose a parameter to search by:");
            System.out.println("1. ID");
            System.out.println("2. Name");
            System.out.println("3. Status");
            System.out.println("4. Priority");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            String parameter = switch (choice) {
                case "1" -> "id";
                case "2" -> "name";
                case "3" -> "status";
                case "4" -> "priority";
                default -> {
                    System.out.println("Invalid choice.");
                    yield null;
                }
            };

            if (parameter == null) return;

            String value = promptInput("Enter value to search: ");

            int page = 0;
            int size = 10;
            boolean continuePagination = true;

            while (continuePagination) {
                Map<String, String> queryParams = Map.of(
                        parameter, value,
                        "page", String.valueOf(page),
                        "size", String.valueOf(size)
                );

                PageResponse<TaskDto> response = taskApi.searchTasks(queryParams);

                if (response.getContent().isEmpty()) {
                    if (page == 0) {
                        System.out.println("No tasks found matching the criteria.");
                    } else {
                        System.out.println("No more tasks to display.");
                    }
                    break;
                } else {
                    System.out.println("\n--- Search Results ---");
                    response.getContent().forEach(this::printTask);
                    System.out.printf("Page %d of %d\n", response.getPageNumber() + 1, response.getTotalPages());

                    if (response.getTotalPages() <= 1) {
                        break;
                    }

                    System.out.println("Options:");
                    if (page > 0) {
                        System.out.println("P - Previous page");
                    }
                    if (page < response.getTotalPages() - 1) {
                        System.out.println("N - Next page");
                    }
                    System.out.println("Q - Quit");

                    System.out.print("Enter your choice (N/P/Q): ");
                    String navChoice = scanner.nextLine().trim().toUpperCase();

                    switch (navChoice) {
                        case "N":
                            if (page < response.getTotalPages() - 1) {
                                page++;
                            } else {
                                System.out.println("You are on the last page.");
                            }
                            break;
                        case "P":
                            if (page > 0) {
                                page--;
                            } else {
                                System.out.println("You are on the first page.");
                            }
                            break;
                        case "Q":
                            continuePagination = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter N, P, or Q.");
                    }
                }
            }

        } catch (Exception e) {
            handleException(e);
        }
    }

    private Priority promptForValidPriority() {
        while (true) {
            String input = promptInput("Priority (LOW/MEDIUM/HIGH/CRITICAL): ").toUpperCase();
            try {
                return Priority.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid priority. Please enter one of: LOW, MEDIUM, HIGH, CRITICAL.");
            }
        }
    }

    private Status promptForValidStatus() {
        while (true) {
            String input = promptInput("Status (TODO/IN_PROGRESS/BLOCKED/DONE): ").toUpperCase();
            try {
                return Status.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Please enter one of: TODO, IN_PROGRESS, BLOCKED, DONE.");
            }
        }
    }

    private LocalDate promptForValidDate() {
        while (true) {
            String input = promptInput("Due Date (yyyy-MM-dd or leave empty): ");
            if (input.isEmpty()) {
                return null; // Pozwalamy na brak daty
            }

            try {
                LocalDate date = LocalDate.parse(input);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Due date cannot be in the past. Please enter a valid date.");
                } else {
                    return date;
                }
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }

    private void displayCurrentTask(TaskDto task) {
        String currentProject = task.getProjectId() != null
                ? "Project ID: " + task.getProjectId()
                : "No project assigned";

        System.out.println("\nEditing Task:");
        System.out.printf("Current Name: %s | Current Description: %s | Current Priority: %s | Current Status: %s | %s\n",
                task.getName(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                currentProject);
    }

    private void updateTaskName(TaskDto task) {
        String newName = promptInput("New name (" + task.getName() + "): ");
        if (!newName.isEmpty()) {
            task.setName(newName);
        }
    }

    private void updateTaskDescription(TaskDto task) {
        String newDesc = promptInput("New description (" + task.getDescription() + "): ");
        if (!newDesc.isEmpty()) {
            task.setDescription(newDesc);
        }
    }

    private void updateTaskPriority(TaskDto task) {
        String newPriority = promptInput("New priority (LOW/MEDIUM/HIGH/CRITICAL, current: " + task.getPriority() + "): ").toUpperCase();
        if (!newPriority.isEmpty()) {
            try {
                task.setPriority(Priority.valueOf(newPriority));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid priority. Priority not changed.");
            }
        }
    }

    private void updateTaskStatus(TaskDto task) {
        String newStatus = promptInput("New status (TODO/IN_PROGRESS/BLOCKED/DONE, current: " + task.getStatus() + "): ").toUpperCase();
        if (!newStatus.isEmpty()) {
            try {
                task.setStatus(Status.valueOf(newStatus));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Status not changed.");
            }
        }
    }

    private void updateTaskProject(TaskDto task) {
        String currentProject = task.getProjectId() != null ? task.getProjectId().toString() : "No project assigned";
        String changeProject = promptInput("Change project? (yes/no, current: " + currentProject + "): ").toLowerCase();
        if ("yes".equals(changeProject)) {
            String projectId = promptInput("Enter new project ID or leave empty to unassign: ");
            if (projectId.isEmpty()) {
                task.setProjectId(null);
            } else {
                try {
                    task.setProjectId(Long.parseLong(projectId));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid project ID. Project not changed.");
                }
            }
        }
    }
}
