package server;

import dataaccess.DataAccess;
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
