package client;

import com.google.gson.Gson;
import chess.ChessGame;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.ResignCommand;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.Scanner;

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
                .get();

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

            try {
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
                        handleResign();
                        break;
                    case "leave":
                        handleLeave();
                        break;
                    case "quit":
                        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Goodbye");
                        return;
                    default:
                        System.out.println("Unknown command. Type help for options.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleMove(String from, String to) throws Exception {
        MakeMoveCommand cmd = new MakeMoveCommand(authToken, gameID, null);
        webSocket.sendText(gson.toJson(cmd), true);
    }

    private void handleResign() throws Exception {
        ResignCommand cmd = new ResignCommand(authToken, gameID);
        webSocket.sendText(gson.toJson(cmd), true);
    }

    private void handleLeave() throws Exception {
        LeaveCommand cmd = new LeaveCommand(authToken, gameID);
        webSocket.sendText(gson.toJson(cmd), true);
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  move <from> <to> - make a move");
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
            System.out.println("Server: " + data);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.out.println("Error: " + error.getMessage());
        }
    }
}