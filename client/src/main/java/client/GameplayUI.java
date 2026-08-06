package client;

import java.util.Scanner;

public class GameplayUI {
    private final String authToken;
    private final int gameID;

    public GameplayUI(String serverUrl, String authToken, int gameID) throws Exception {
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void run() {
        System.out.println("Game started. Type help for commands.");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(">>> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split(" ");
            String command = parts[0].toLowerCase();

            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "move":
                    if (parts.length == 3) {
                        System.out.println("Move: " + parts[1] + " to " + parts[2]);
                    } else {
                        System.out.println("Usage: move <from> <to>");
                    }
                    break;
                case "resign":
                    System.out.println("You resigned from the game.");
                    return;
                case "leave":
                    System.out.println("You left the game.");
                    return;
                case "quit":
                    return;
                default:
                    System.out.println("Unknown command. Type help for options.");
            }
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  move <from> <to> - make a move (e.g., move e2 e4)");
        System.out.println("  resign - resign from game");
        System.out.println("  leave - leave game");
        System.out.println("  quit - exit");
        System.out.println("  help - show this help");
    }
}