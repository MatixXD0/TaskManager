package pl.kul.taskmanagerclient;

import lombok.RequiredArgsConstructor;
import pl.kul.taskmanagerclient.api.ProjectApiService;
import pl.kul.taskmanagerclient.api.TaskApiService;
import pl.kul.taskmanagerclient.console.ProjectConsoleHandler;
import pl.kul.taskmanagerclient.console.TaskConsoleHandler;
import pl.kul.taskmanagerclient.menu.MenuHandler;
import pl.kul.taskmanagerclient.menu.MenuPrinter;

import java.util.Scanner;

@RequiredArgsConstructor
public class ConsoleApplication {
    private final Scanner scanner = new Scanner(System.in);
    private final TaskConsoleHandler taskHandler = new TaskConsoleHandler(new TaskApiService(), scanner);
    private final ProjectConsoleHandler projectHandler = new ProjectConsoleHandler(new ProjectApiService(), scanner);
    private final MenuPrinter menuPrinter = new MenuPrinter();
    private final MenuHandler menuHandler = new MenuHandler(taskHandler, projectHandler, scanner, menuPrinter);

    public static void main(String[] args) {
        new ConsoleApplication().run();
    }

    private void run() {
        menuHandler.handleMainMenu();
    }
}
