package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import model.LoginRequest;
import model.RegisterRequest;
import service.ClearService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        DataAccess dataAccess = new MemoryDataAccess();

        javalin.delete("/db", ctx -> {
            new ClearService(dataAccess).clear();
            ctx.status(200);
            ctx.result("{}");
        });

        javalin.post("/user", ctx -> {
            var request = new com.google.gson.Gson().fromJson(ctx.body(), RegisterRequest.class);
            var result = new UserService(dataAccess).register(request);
            ctx.status(200);
            ctx.result(new com.google.gson.Gson().toJson(result));
        });

        javalin.post("/session", ctx -> {
            var request = new com.google.gson.Gson().fromJson(ctx.body(), LoginRequest.class);
            var result = new UserService(dataAccess).login(request);
            ctx.status(200);
            ctx.result(new com.google.gson.Gson().toJson(result));
        });

        javalin.exception(DataAccessException.class, (e, ctx) -> {
            String message = e.getMessage();
            int status;
            if (message.contains("already taken")) {
                status = 403;
            } else if (message.contains("unauthorized")) {
                status = 401;
            } else if (message.contains("bad request")) {
                status = 400;
            } else {
                status = 500;
            }
            ctx.status(status);
            ctx.result(new com.google.gson.Gson().toJson(java.util.Map.of("message", message)));
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public static void main(String[] args) {
        new Server().run(8080);
    }
}
