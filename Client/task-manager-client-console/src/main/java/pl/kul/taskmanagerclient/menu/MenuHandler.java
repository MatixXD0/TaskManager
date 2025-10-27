package pl.kul.taskmanagerclient.menu;

import pl.kul.taskmanagerclient.console.ProjectConsoleHandler;
import pl.kul.taskmanagerclient.console.TaskConsoleHandler;

import java.util.Scanner;

public class MenuHandler {
    private final TaskConsoleHandler taskHandler;
    private final ProjectConsoleHandler projectHandler;
    private final Scanner scanner;
    private final MenuPrinter menuPrinter;

    public MenuHandler(TaskConsoleHandler taskHandler, ProjectConsoleHandler projectHandler, Scanner scanner, MenuPrinter menuPrinter) {
        this.taskHandler = taskHandler;
        this.projectHandler = projectHandler;
        this.scanner = scanner;
        this.menuPrinter = menuPrinter;
    }

    public void handleMainMenu() {
        boolean isRunning = true;

        while (isRunning) {
            menuPrinter.printMainMenu();
            String choice = scanner.nextLine().trim();

            isRunning = handleMenuChoice(choice);
        }
    }

    private boolean handleMenuChoice(String choice) {
        switch (choice) {
            case "1" -> taskHandler.showAllTasks();
            case "2" -> taskHandler.createTask();
            case "3" -> taskHandler.deleteTask();
            case "4" -> taskHandler.editTask();
            case "5" -> projectHandler.showAllProjects();
            case "6" -> projectHandler.createProject();
            case "7" -> projectHandler.deleteProject();
            case "8" -> projectHandler.editProject();
            case "9" -> projectHandler.addTaskToProject();
            case "10" -> projectHandler.removeTaskFromProject();
            case "11" -> projectHandler.showProjectWithTasks();
            case "12" -> handleSearchMenu();
            case "0" -> {
                System.out.println("Exiting...");
                return false;
            }
            default -> System.out.println("Unknown option. Try again.");
        }
        return true;
    }

    private void handleSearchMenu() {
        menuPrinter.printSearchMenu();
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> taskHandler.searchTasks();
            case "2" -> projectHandler.searchProjects();
            case "0" -> System.out.println("Returning to main menu...");
            default -> System.out.println("Unknown option. Try again.");
        }
    }
}
