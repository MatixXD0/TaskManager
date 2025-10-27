package pl.kul.taskmanagerclient.console;

import pl.kul.taskmanagerclient.api.ProjectApiService;
import pl.kul.taskmanagerclient.dto.PageResponse;
import pl.kul.taskmanagerclient.dto.ProjectDto;
import pl.kul.taskmanagerclient.dto.TaskDto;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProjectConsoleHandler extends BaseConsoleHandler {
    private final ProjectApiService projectApi;

    public ProjectConsoleHandler(ProjectApiService projectApi, Scanner scanner) {
        super(scanner);
        this.projectApi = projectApi;
    }

    private void printProject(ProjectDto project) {
        System.out.printf("ID: %d | Name: %s | Description: %s\n",
                project.getId(),
                project.getName(),
                project.getDescription());
        if (project.getTasks() != null && !project.getTasks().isEmpty()) {
            System.out.println("--- Assigned Tasks ---");
            project.getTasks().forEach(this::printTask);
        } else {
            System.out.println("    No tasks assigned.");
        }
    }

    private void printTask(TaskDto task) {
        System.out.printf("    Task ID: %d | Name: %s | Status: %s\n",
                task.getId(),
                task.getName(),
                task.getStatus());
    }

    public void showAllProjects() {
        try {
            List<ProjectDto> projects = projectApi.getAllProjects();
            if (projects.isEmpty()) {
                System.out.println("No projects found.");
            } else {
                System.out.println("\n--- Project List ---");
                projects.forEach(this::printProject);
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void createProject() {
        try {
            ProjectDto project = new ProjectDto();

            project.setName(promptForNonEmptyInput("Project name: "));
            project.setDescription(promptInput("Description (optional): "));

            ProjectDto createdProject = projectApi.createProject(project);
            System.out.println("Project created successfully with ID: " + createdProject.getId());
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void deleteProject() {
        try {
            Long id = promptForValidId("Enter project ID to delete: ");
            projectApi.deleteProject(id);
            System.out.println("Project deleted successfully!");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void editProject() {
        try {
            Long id = promptForValidId("Enter project ID to edit: ");
            ProjectDto project = projectApi.getProjectById(id);

            updateProjectName(project);
            updateProjectDescription(project);

            ProjectDto updatedProject = projectApi.updateProject(id, project);
            System.out.println("Project updated successfully: " + updatedProject);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void addTaskToProject() {
        try {
            Long projectId = promptForValidId("Enter project ID: ");
            Long taskId = promptForValidId("Enter task ID: ");

            ProjectDto updatedProject = projectApi.addTaskToProject(projectId, taskId);
            System.out.println("Task successfully added to project. Updated project details:");
            printProject(updatedProject);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void removeTaskFromProject() {
        try {
            Long projectId = promptForValidId("Enter project ID: ");
            Long taskId = promptForValidId("Enter task ID: ");

            ProjectDto updatedProject = projectApi.removeTaskFromProject(projectId, taskId);
            System.out.println("Task successfully removed from project. Updated project details:");
            printProject(updatedProject);
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void showProjectWithTasks() {
        try {
            Long projectId = promptForValidId("Enter project ID to view: ");
            ProjectDto project = projectApi.getProjectById(projectId);

            System.out.println("\n--- Project Details ---");
            System.out.printf("ID: %d | Name: %s | Description: %s\n",
                    project.getId(),
                    project.getName(),
                    project.getDescription());

            if (project.getTasks() != null && !project.getTasks().isEmpty()) {
                System.out.println("\n--- Tasks in this Project ---");
                project.getTasks().forEach(this::printDetailedTask);
            } else {
                System.out.println("No tasks are assigned to this project.");
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void printDetailedTask(TaskDto task) {
        System.out.printf("ID: %d | Name: %s | Priority: %s | Status: %s | Due Date: %s\n",
                task.getId(),
                task.getName(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate() != null ? task.getDueDate() : "No due date");
    }

    public void searchProjects() {
        try {
            System.out.println("\n--- Search Projects ---");
            System.out.println("Choose a parameter to search by:");
            System.out.println("1. ID");
            System.out.println("2. Name");
            System.out.println("3. Description");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            String parameter = switch (choice) {
                case "1" -> "id";
                case "2" -> "name";
                case "3" -> "description";
                default -> {
                    System.out.println("Invalid choice.");
                    yield null;
                }
            };

            if (parameter == null) return;

            String value = promptInput("Enter value to search: ");
            int page = 0;
            int size = 10;

            Map<String, String> queryParams = Map.of(
                    parameter, value,
                    "page", String.valueOf(page),
                    "size", String.valueOf(size)
            );

            PageResponse<ProjectDto> response = projectApi.searchProjects(queryParams);

            if (response.getContent().isEmpty()) {
                System.out.println("No projects found matching the criteria.");
            } else {
                System.out.println("\n--- Search Results ---");
                response.getContent().forEach(this::printProject);
                System.out.printf("Page %d of %d\n", response.getPageNumber() + 1, response.getTotalPages());
            }

        } catch (Exception e) {
            handleException(e);
        }
    }

    private void updateProjectName(ProjectDto project) {
        String newName = promptInput("New name (" + project.getName() + "): ");
        if (!newName.isEmpty()) {
            project.setName(newName);
        }
    }

    private void updateProjectDescription(ProjectDto project) {
        String newDesc = promptInput("New description (" + project.getDescription() + "): ");
        if (!newDesc.isEmpty()) {
            project.setDescription(newDesc);
        }
    }
}
