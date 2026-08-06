package client;

import com.google.gson.Gson;
import websocket.commands.ConnectCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;

public class GameplayUI {
    private final WebSocket webSocket;
    private final String authToken;
    private final int gameID;
    private final Gson gson = new Gson();

    public GameplayUI(String serverUrl, String authToken, int gameID) throws Exception {
        this.authToken = authToken;
        this.gameID = gameID;

        String wsUrl = serverUrl.replace("http", "ws") + "/ws";
        HttpClient client = HttpClient.newHttpClient();
        this.webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), new WebSocketListener())
                .join();

        Thread.sleep(500);

        ConnectCommand connect = new ConnectCommand(authToken, gameID);
        webSocket.sendText(gson.toJson(connect), true);

        Thread.sleep(500);
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
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "resign");
                    return;
                case "leave":
                    System.out.println("You left the game.");
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "leave");
                    return;
                case "quit":
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "quit");
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

    private class WebSocketListener implements WebSocket.Listener {
        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("Connected to game server");
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            try {
                String json = data.toString();

                if (json.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
                    System.out.println("\n=== Game State Updated ===");
                    LoadGameMessage msg = gson.fromJson(json, LoadGameMessage.class);
                    System.out.println("Game: " + msg.getGame().gameName());
                    System.out.println(">>> ");
                } else if (json.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
                    NotificationMessage msg = gson.fromJson(json, NotificationMessage.class);
                    System.out.println("\n[Server] " + msg.getMessage());
                    System.out.print(">>> ");
                } else if (json.contains("\"serverMessageType\":\"ERROR\"")) {
                    ErrorMessage msg = gson.fromJson(json, ErrorMessage.class);
                    System.out.println("\n[Error] " + msg.getErrorMessage());
                    System.out.print(">>> ");
                }
            } catch (Exception e) {
                System.out.println("Failed to parse message: " + e.getMessage());
            }
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.out.println("Error: " + error.getMessage());
        }
    }
}