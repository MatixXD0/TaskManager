package pl.kul.taskmanagerclient.console;

import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public abstract class BaseConsoleHandler {
    protected final Scanner scanner;

    protected String promptInput(String promptMessage) {
        System.out.print(promptMessage);
        return scanner.nextLine().trim();
    }

    protected Long promptForValidId(String promptMessage) {
        while (true) {
            String input = promptInput(promptMessage);
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidlowy format ID. Prosze wprowadzic wartosc numeryczna.");
            }
        }
    }

    protected String promptForNonEmptyInput(String promptMessage) {
        while (true) {
            String input = promptInput(promptMessage);
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Input nie moze byc pusty. Prosze wprowadzic prawidłowa wartosc.");
            }
        }
    }
    
    protected void handleException(Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}
