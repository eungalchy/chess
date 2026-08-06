package server;

import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.*;
import websocket.messages.*;
import service.GamePlayService;
import dataaccess.DataAccess;
import dataaccess.MySqlDataAccess;

public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final GamePlayService gamePlayService;

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
            ctx.send(gson.toJson(new LoadGameMessage(game)));
            ctx.send(gson.toJson(new NotificationMessage("Connected to game")));
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    private void handleMakeMove(WsMessageContext ctx, MakeMoveCommand command) {
        try {
            boolean success = gamePlayService.makeMove(command.getGameID(), command.getMove());
            if (success) {
                var game = gamePlayService.getGame(command.getGameID());
                ctx.send(gson.toJson(new LoadGameMessage(game)));
                ctx.send(gson.toJson(new NotificationMessage("Move successful")));
            } else {
                ctx.send(gson.toJson(new ErrorMessage("Invalid move")));
            }
        } catch (Exception e) {
            ctx.send(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command) {
        ctx.send(gson.toJson(new NotificationMessage("Left game")));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command) {
        ctx.send(gson.toJson(new NotificationMessage("Game over - resigned")));
    }
}