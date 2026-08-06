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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final GamePlayService gamePlayService;
    private final DataAccess dataAccess;
    private static final Map<Integer, Set<WsMessageContext>> gameSessions = new HashMap<>();

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
            var game = gamePlayService.getGame(command.getGameID());
            if (game == null) {
                ctx.send(gson.toJson(new ErrorMessage("Error: invalid game")));
                return;
            }

            gameSessions.computeIfAbsent(command.getGameID(), k -> new HashSet<>()).add(ctx);

            ctx.send(gson.toJson(new LoadGameMessage(game)));

            String role;
            if (username.equals(game.whiteUsername())) {
                role = username + " joined as WHITE";
            } else if (username.equals(game.blackUsername())) {
                role = username + " joined as BLACK";
            } else {
                role = username + " joined as an observer";
            }
            broadcastToOthers(command.getGameID(), ctx, new NotificationMessage(role));
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void handleMakeMove(WsMessageContext ctx, String message, String username) {
        try {
            MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);

            boolean success = gamePlayService.makeMove(moveCommand.getGameID(), moveCommand.getMove());
            if (!success) {
                ctx.send(gson.toJson(new ErrorMessage("Error: invalid move")));
                return;
            }

            var game = gamePlayService.getGame(moveCommand.getGameID());
            broadcastToGame(moveCommand.getGameID(), new LoadGameMessage(game));
            broadcastToOthers(moveCommand.getGameID(), ctx, new NotificationMessage(username + " made a move"));
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage("Error: invalid move")));
        }
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command, String username) {
        removePlayerFromGame(command.getGameID(), ctx);
        broadcastToOthers(command.getGameID(), ctx, new NotificationMessage(username + " left the game"));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command, String username) {
        broadcastToGame(command.getGameID(), new NotificationMessage(username + " resigned"));
    }

    private void broadcastToGame(int gameID, ServerMessage message) {
        Set<WsMessageContext> sessions = gameSessions.getOrDefault(gameID, new HashSet<>());
        String json = gson.toJson(message);
        for (WsMessageContext session : sessions) {
            try {
                session.send(json);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void broadcastToOthers(int gameID, WsMessageContext except, ServerMessage message) {
        Set<WsMessageContext> sessions = gameSessions.getOrDefault(gameID, new HashSet<>());
        String json = gson.toJson(message);
        for (WsMessageContext session : sessions) {
            if (session != except) {
                try {
                    session.send(json);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    private void removePlayerFromGame(int gameID, WsMessageContext ctx) {
        Set<WsMessageContext> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            sessions.remove(ctx);
            if (sessions.isEmpty()) {
                gameSessions.remove(gameID);
            }
        }
    }
}