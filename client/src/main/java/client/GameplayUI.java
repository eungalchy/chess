package client;

import com.google.gson.Gson;
import websocket.commands.ConnectCommand;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;
import websocket.messages.LoadGameMessage;
import ui.BoardPrinter;


public class GameplayUI {
    private final WebSocket webSocket;
    private final String authToken;
    private final int gameID;
    private final Gson gson;

    public GameplayUI(String serverUrl, String authToken, int gameID) throws Exception {
        this.authToken = authToken;
        this.gameID = gameID;
        this.gson = new Gson();

        String wsUrl = serverUrl.replace("http", "ws") + "/ws";
        HttpClient client = HttpClient.newHttpClient();
        this.webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), new WebSocketListener())
                .join();

        Thread.sleep(500);
        ConnectCommand connect = new ConnectCommand(authToken, gameID);
        webSocket.sendText(gson.toJson(connect), true);
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
                        handleMove(parts[1], parts[2]);
                    } else {
                        System.out.println("Usage: move <from> <to>");
                    }
                    break;
                case "resign":
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "resign");
                    return;
                case "leave":
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "leave");
                    return;
                case "quit":
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "quit");
                    return;
                default:
                    System.out.println("Unknown command");
            }
        }
    }

    private void printHelp() {
        System.out.println("Commands: move, resign, leave, quit, help");
    }

    private class WebSocketListener implements WebSocket.Listener {
        public void onOpen(WebSocket webSocket) {
            System.out.println("Connected");
        }

        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("\n");
            BoardPrinter.printBoard(true);
            System.out.print(">>> ");
            return null;
        }

        public void onError(WebSocket webSocket, Throwable error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    private void handleMove(String from, String to) {
        try {
            System.out.println("Move: " + from + " to " + to);
            webSocket.sendText("{\"commandType\":\"MAKE_MOVE\",\"authToken\":\"" + authToken + "\",\"gameID\":" + gameID + "}", true);

            Thread.sleep(1000);
            System.out.println("\n=== Board ===");
            BoardPrinter.printBoard(true);
            System.out.println("=============\n");
            System.out.print(">>> ");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}