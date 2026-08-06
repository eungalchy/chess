package server;

import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.*;
import websocket.messages.*;
import service.GamePlayService;
import dataaccess.DataAccess;
import dataaccess.MySqlDataAccess;
import java.util.*;

public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final GamePlayService gamePlayService;
    private static final Map<Integer, Set<WsMessageContext>> gameSessions = new HashMap<>();

    public WebSocketHandler() throws Exception {
        DataAccess dataAccess = new MySqlDataAccess();
        this.gamePlayService = new GamePlayService(dataAccess);
    }

    public void handleMessage(WsMessageContext ctx, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            if (command == null) {
                ctx.send(gson.toJson(new ErrorMessage("Invalid command")));
                return;
            }

            switch (command.getCommandType()) {
                case CONNECT:
                    handleConnect(ctx, command);
                    break;
                case MAKE_MOVE:
                    handleMakeMove(ctx, (MakeMoveCommand) command);
                    break;
                case LEAVE:
                    handleLeave(ctx, command);
                    break;
                case RESIGN:
                    handleResign(ctx, command);
                    break;
            }
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private void handleConnect(WsMessageContext ctx, UserGameCommand command) {
        try {
            var game = gamePlayService.getGame(command.getGameID());

            gameSessions.computeIfAbsent(command.getGameID(), k -> new HashSet<>()).add(ctx);

            ctx.send(gson.toJson(new LoadGameMessage(game)));
            broadcastToGame(command.getGameID(), new NotificationMessage("Player connected"));
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    private void handleMakeMove(WsMessageContext ctx, MakeMoveCommand command) {
        try {
            boolean success = gamePlayService.makeMove(command.getGameID(), command.getMove());
            var game = gamePlayService.getGame(command.getGameID());

            broadcastToGame(command.getGameID(), new LoadGameMessage(game));
            broadcastToGame(command.getGameID(), new NotificationMessage("Move made"));
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command) {
        removePlayerFromGame(command.getGameID(), ctx);
        ctx.send(gson.toJson(new NotificationMessage("Left game")));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command) {
        broadcastToGame(command.getGameID(), new NotificationMessage("Player resigned"));
        removePlayerFromGame(command.getGameID(), ctx);
    }

    private void broadcastToGame(int gameID, ServerMessage message) {
        Set<WsMessageContext> sessions = gameSessions.getOrDefault(gameID, new HashSet<>());
        String json = new Gson().toJson(message);
        for (WsMessageContext session : sessions) {
            try {
                session.send(json);
            } catch (Exception e) {
                System.out.println("Failed to send message: " + e.getMessage());
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