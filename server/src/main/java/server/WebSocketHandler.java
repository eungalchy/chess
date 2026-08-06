package server;

import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.*;
import websocket.messages.*;
import service.GamePlayService;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final GamePlayService gamePlayService;
    private final DataAccess dataAccess;
    private static final Map<Integer, Map<String, WsMessageContext>> GAME_SESSIONS = new HashMap<>();
    private static final Set<Integer> FINISHED_GAMES = new HashSet<>();

    public WebSocketHandler() throws DataAccessException {
        this.dataAccess = new MySqlDataAccess();
        this.gamePlayService = new GamePlayService(dataAccess);
    }

    public void handleMessage(WsMessageContext ctx, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            String username = getUsername(command.getAuthToken());
            if (username == null) {
                ctx.send(gson.toJson(new ErrorMessage("Error: unauthorized")));
                return;
            }

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(ctx, command, username);
                case MAKE_MOVE -> handleMakeMove(ctx, message, username);
                case LEAVE -> handleLeave(ctx, command, username);
                case RESIGN -> handleResign(ctx, command, username);
            }
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private String getUsername(String authToken) {
        try {
            AuthData auth = dataAccess.getAuth(authToken);
            return auth == null ? null : auth.username();
        } catch (Exception e) {
            return null;
        }
    }

    private void handleConnect(WsMessageContext ctx, UserGameCommand command, String username) {
        try {
            GameData game = gamePlayService.getGame(command.getGameID());
            if (game == null) {
                ctx.send(gson.toJson(new ErrorMessage("Error: invalid game")));
                return;
            }

            GAME_SESSIONS.computeIfAbsent(command.getGameID(), k -> new HashMap<>()).put(username, ctx);
            ctx.send(gson.toJson(new LoadGameMessage(game)));

            String role;
            if (username.equals(game.whiteUsername())) {
                role = username + " joined as WHITE";
            } else if (username.equals(game.blackUsername())) {
                role = username + " joined as BLACK";
            } else {
                role = username + " joined as an observer";
            }
            broadcastToOthers(command.getGameID(), username, new NotificationMessage(role));
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void handleMakeMove(WsMessageContext ctx, String message, String username) {
        try {
            MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
            int gameID = moveCommand.getGameID();

            if (FINISHED_GAMES.contains(gameID)) {
                ctx.send(gson.toJson(new ErrorMessage("Error: game is over")));
                return;
            }

            GameData game = gamePlayService.getGame(gameID);
            if (game == null) {
                ctx.send(gson.toJson(new ErrorMessage("Error: invalid game")));
                return;
            }

            boolean isWhite = username.equals(game.whiteUsername());
            boolean isBlack = username.equals(game.blackUsername());
            if (!isWhite && !isBlack) {
                ctx.send(gson.toJson(new ErrorMessage("Error: observers cannot move")));
                return;
            }

            var turn = game.game().getTeamTurn();
            boolean whiteTurn = turn == chess.ChessGame.TeamColor.WHITE;
            if ((whiteTurn && !isWhite) || (!whiteTurn && !isBlack)) {
                ctx.send(gson.toJson(new ErrorMessage("Error: not your turn")));
                return;
            }

            boolean success = gamePlayService.makeMove(gameID, moveCommand.getMove());
            if (!success) {
                ctx.send(gson.toJson(new ErrorMessage("Error: invalid move")));
                return;
            }

            GameData updated = gamePlayService.getGame(gameID);
            broadcastToGame(gameID, new LoadGameMessage(updated));
            broadcastToOthers(gameID, username, new NotificationMessage(username + " made a move"));

            // check/checkmate/stalemate 확인
            chess.ChessGame chessGame = updated.game();
            chess.ChessGame.TeamColor opponent = whiteTurn ? chess.ChessGame.TeamColor.BLACK : chess.ChessGame.TeamColor.WHITE;
            String opponentName = whiteTurn ? updated.blackUsername() : updated.whiteUsername();

            if (chessGame.isInCheckmate(opponent)) {
                broadcastToGame(gameID, new NotificationMessage(opponentName + " is in checkmate"));
                FINISHED_GAMES.add(gameID);
            } else if (chessGame.isInStalemate(opponent)) {
                broadcastToGame(gameID, new NotificationMessage("Stalemate"));
                FINISHED_GAMES.add(gameID);
            } else if (chessGame.isInCheck(opponent)) {
                broadcastToGame(gameID, new NotificationMessage(opponentName + " is in check"));
            }
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage("Error: invalid move")));
        }
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command, String username) {
        try {
            gamePlayService.leaveGame(command.getGameID(), username);
        } catch (Exception e) {
            // ignore
        }
        removePlayerFromGame(command.getGameID(), username);
        broadcastToOthers(command.getGameID(), username, new NotificationMessage(username + " left the game"));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command, String username) {
        try {
            int gameID = command.getGameID();

            if (FINISHED_GAMES.contains(gameID)) {
                ctx.send(gson.toJson(new ErrorMessage("Error: game is already over")));
                return;
            }

            GameData game = gamePlayService.getGame(gameID);
            boolean isPlayer = username.equals(game.whiteUsername()) || username.equals(game.blackUsername());
            if (!isPlayer) {
                ctx.send(gson.toJson(new ErrorMessage("Error: observers cannot resign")));
                return;
            }

            FINISHED_GAMES.add(gameID);
            broadcastToGame(gameID, new NotificationMessage(username + " resigned"));
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void broadcastToGame(int gameID, ServerMessage message) {
        Map<String, WsMessageContext> sessions = GAME_SESSIONS.getOrDefault(gameID, new HashMap<>());
        String json = gson.toJson(message);
        for (WsMessageContext session : sessions.values()) {
            try {
                session.send(json);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void broadcastToOthers(int gameID, String exceptUsername, ServerMessage message) {
        Map<String, WsMessageContext> sessions = GAME_SESSIONS.getOrDefault(gameID, new HashMap<>());
        String json = gson.toJson(message);
        for (Map.Entry<String, WsMessageContext> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(exceptUsername)) {
                try {
                    entry.getValue().send(json);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    private void removePlayerFromGame(int gameID, String username) {
        Map<String, WsMessageContext> sessions = GAME_SESSIONS.get(gameID);
        if (sessions != null) {
            sessions.remove(username);
            if (sessions.isEmpty()) {
                GAME_SESSIONS.remove(gameID);
            }
        }
    }
}