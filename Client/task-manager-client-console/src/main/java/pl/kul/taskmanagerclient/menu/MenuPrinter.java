package pl.kul.taskmanagerclient.menu;

public class MenuPrinter {
    public void printMainMenu() {
        System.out.println("\n--- Task Manager Client ---");
        System.out.println("1. Show all tasks");
        System.out.println("2. Create a new task");
        System.out.println("3. Delete a task");
        System.out.println("4. Edit a task");
        System.out.println("5. Show all projects");
        System.out.println("6. Create a new project");
        System.out.println("7. Delete a project");
        System.out.println("8. Edit a project");
        System.out.println("9. Add task to project");
        System.out.println("10. Remove task from project");
        System.out.println("11. Show project with tasks");
        System.out.println("12. Search");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    public void printSearchMenu() {
        System.out.println("\n--- Search Menu ---");
        System.out.println("1. Search Tasks");
        System.out.println("2. Search Projects");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose an option: ");
    }
}
