package client;

import com.google.gson.Gson;
import websocket.commands.ConnectCommand;
import websocket.messages.LoadGameMessage;
import model.GameData;
import chess.ChessBoard;
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
    private ChessBoard currentBoard;
    private chess.ChessGame currentGame;

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
                case "redraw":
                    if (currentBoard != null) {
                        ui.BoardPrinter.printBoard(currentBoard, whitePerspective);
                    } else {
                        System.out.println("No board to redraw yet.");
                    }
                    break;
                case "highlight":
                    if (parts.length == 2) {
                        handleHighlight(parts[1]);
                    } else {
                        System.out.println("Usage: highlight <position> (e.g., highlight e2)");
                    }
                    break;
                case "move":
                    if (parts.length == 3) {
                        handleMove(parts[1], parts[2]);
                    } else {
                        System.out.println("Usage: move <from> <to>");
                    }
                    break;
                case "resign":
                    System.out.print("Are you sure you want to resign? (yes/no): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("yes")) {
                        sendResign();
                    } else {
                        System.out.println("Resign cancelled.");
                    }
                    break;
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

    private void sendResign() {
        try {
            String json = "{\"commandType\":\"RESIGN\","
                    + "\"authToken\":\"" + authToken + "\","
                    + "\"gameID\":" + gameID + "}";
            webSocket.sendText(json, true);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  help - show this help");
        System.out.println("  redraw - redraw the chess board");
        System.out.println("  move <from> <to> - make a move (e.g., move e2 e4)");
        System.out.println("  resign - resign from the game");
        System.out.println("  leave - leave the game");
    }

    private void handleMove(String from, String to) {
        try {
            int startCol = from.charAt(0) - 'a' + 1;
            int startRow = Character.getNumericValue(from.charAt(1));
            int endCol = to.charAt(0) - 'a' + 1;
            int endRow = Character.getNumericValue(to.charAt(1));

            String moveJson = "{\"commandType\":\"MAKE_MOVE\",\"authToken\":\""
                    + authToken + "\",\"gameID\":"
                    + gameID + ",\"move\":{\"startPosition\":{\"row\":"
                    + startRow + ",\"col\":" + startCol + "},\"endPosition\":{\"row\":"
                    + endRow + ",\"col\":" + endCol + "}}}";
            webSocket.sendText(moveJson, true);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleHighlight(String position) {
        if (currentGame == null || currentBoard == null) {
            System.out.println("No game loaded yet.");
            return;
        }
        try {
            int col = position.charAt(0) - 'a' + 1;
            int row = Character.getNumericValue(position.charAt(1));
            chess.ChessPosition pos = new chess.ChessPosition(row, col);

            java.util.Collection<chess.ChessMove> moves = currentGame.validMoves(pos);
            java.util.Collection<chess.ChessPosition> highlights = new java.util.ArrayList<>();
            if (moves != null) {
                for (chess.ChessMove m : moves) {
                    highlights.add(m.getEndPosition());
                }
            }
            ui.BoardPrinter.printBoardHighlighted(currentBoard, whitePerspective, pos, highlights);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private class WebSocketListener implements WebSocket.Listener {
        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            try {
                String json = data.toString();
                if (json.contains("LOAD_GAME")) {
                    LoadGameMessage msg = gson.fromJson(json, LoadGameMessage.class);
                    GameData gameData = msg.getGame();
                    if (gameData != null && gameData.game() != null) {
                        currentGame = gameData.game();
                        currentBoard = gameData.game().getBoard();
                        System.out.println();
                        ui.BoardPrinter.printBoard(currentBoard, whitePerspective);
                        System.out.print(">>> ");
                    }
                } else if (json.contains("NOTIFICATION")) {
                    var msg = gson.fromJson(json, java.util.Map.class);
                    System.out.println("\n[NOTIFICATION] " + msg.get("message"));
                    System.out.print(">>> ");
                } else if (json.contains("ERROR")) {
                    var msg = gson.fromJson(json, java.util.Map.class);
                    System.out.println("\n[ERROR] " + msg.get("errorMessage"));
                    System.out.print(">>> ");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            webSocket.request(1);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.out.println("Error: " + error.getMessage());
        }
    }
}