package client;

import com.google.gson.Gson;
import chess.ChessGame;
import websocket.commands.ConnectCommand;
import websocket.messages.LoadGameMessage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class GameplayUI {
    private final WebSocket webSocket;
    private final String authToken;
    private final int gameID;
    private ChessGame currentGame;
    private final Gson gson = new Gson();

    public GameplayUI(String serverUrl, String authToken, int gameID) throws Exception {
        this.authToken = authToken;
        this.gameID = gameID;

        // WebSocket 연결
        String wsUrl = serverUrl.replace("http", "ws") + "/ws";
        HttpClient client = HttpClient.newHttpClient();
        this.webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), new WebSocketListener())
                .get();

        // CONNECT 메시지 전송
        ConnectCommand connect = new ConnectCommand(authToken, gameID);
        webSocket.sendText(gson.toJson(connect), true);
    }

    public void run() {
        System.out.println("Game started. Commands: move, resign, leave, quit");
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (true) {
            System.out.print(">>> ");
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("quit")) {
                break;
            }

            switch (input) {
                case "help":
                    printHelp();
                    break;
                // TODO: move, resign, leave 구현
                default:
                    System.out.println("Unknown command. Type help for options.");
            }
        }

        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Goodbye");
    }

    private void printHelp() {
        System.out.println("""
            move <from> <to> - make a move (e.g., move e2 e4)
            resign - resign from the game
            leave - leave the game
            quit - exit
            help - show this help
            """);
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