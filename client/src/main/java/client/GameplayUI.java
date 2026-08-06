package client;

import com.google.gson.Gson;
import websocket.commands.ConnectCommand;
import websocket.messages.LoadGameMessage;
import model.GameData;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;

public class GameplayUI {
    private final WebSocket webSocket;
    private final String authToken;
    private final int gameID;
    private final Gson gson;
    private final boolean whitePerspective;

    public GameplayUI(String serverUrl, String authToken, int gameID, boolean whitePerspective) throws Exception {
        this.authToken = authToken;
        this.gameID = gameID;
        this.gson = new Gson();
        this.whitePerspective = whitePerspective;

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

    private void handleMove(String from, String to) {
        try {
            int startCol = from.charAt(0) - 'a' + 1;
            int startRow = Character.getNumericValue(from.charAt(1));
            int endCol = to.charAt(0) - 'a' + 1;
            int endRow = Character.getNumericValue(to.charAt(1));

            String moveJson = "{\"commandType\":\"MAKE_MOVE\",\"authToken\":\"" + authToken + "\",\"gameID\":" + gameID + ",\"move\":{\"startPosition\":{\"row\":" + startRow + ",\"col\":" + startCol + "},\"endPosition\":{\"row\":" + endRow + ",\"col\":" + endCol + "}}}";
            webSocket.sendText(moveJson, true);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private class WebSocketListener implements WebSocket.Listener {
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1);
            System.out.println("Connected");
        }

        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            try {
                String json = data.toString();
                if (json.contains("LOAD_GAME")) {
                    LoadGameMessage msg = gson.fromJson(json, LoadGameMessage.class);
                    GameData gameData = msg.getGame();
                    if (gameData != null && gameData.game() != null) {
                        System.out.println();
                        ui.BoardPrinter.printBoard(gameData.game().getBoard(), whitePerspective);
                        System.out.print(">>> ");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            webSocket.request(1);
            return null;
        }

        public void onError(WebSocket webSocket, Throwable error) {
            System.out.println("Error: " + error.getMessage());
        }
    }
}