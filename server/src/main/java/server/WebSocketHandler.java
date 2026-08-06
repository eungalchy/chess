package server;

import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.*;
import websocket.messages.*;

public class WebSocketHandler {
    private final Gson gson = new Gson();

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
        System.out.println("Player connected to game: " + command.getGameID());
        ctx.send(gson.toJson(new NotificationMessage("Connected to game " + command.getGameID())));
    }

    private void handleMakeMove(WsMessageContext ctx, MakeMoveCommand command) {
        System.out.println("Move: " + command.getMove());
        ctx.send(gson.toJson(new NotificationMessage("Move processed")));
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command) {
        System.out.println("Player left game: " + command.getGameID());
        ctx.send(gson.toJson(new NotificationMessage("Left game")));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command) {
        System.out.println("Player resigned from game: " + command.getGameID());
        ctx.send(gson.toJson(new NotificationMessage("Game over - resigned")));
    }
}