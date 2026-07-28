package client;

import model.AuthData;

import java.util.Arrays;

public class PreloginClient {
    private final ServerFacade server;
    private AuthData authData;

    public PreloginClient(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = tokens.length > 0 ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> {
                    authData = login(params);
                    yield "Logged in as " + authData.username();
                }
                case "register" -> {
                    authData = register(params);
                    yield "Registered and logged in as " + authData.username();
                }
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public AuthData getAuthData() {
        return authData;
    }

    public boolean isLoggedIn() {
        return authData != null;
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }

    public AuthData login(String... params) throws Exception {
        if (params.length != 2) {
            throw new Exception("Expected: login <USERNAME> <PASSWORD>");
        }
        var result = server.login(params[0], params[1]);
        return new AuthData(result.authToken(), result.username());
    }

    public AuthData register(String... params) throws Exception {
        if (params.length != 3) {
            throw new Exception("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
        }
        var result = server.register(params[0], params[1], params[2]);
        return new AuthData(result.authToken(), result.username());
    }
}