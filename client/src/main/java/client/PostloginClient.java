package client;

import model.GameData;
import ui.BoardPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostloginClient {
    private final ServerFacade server;
    private final String authToken;
    private List<GameData> lastGameList = new ArrayList<>();

    public PostloginClient(ServerFacade server, String authToken) {
        this.server = server;
        this.authToken = authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = tokens.length > 0 ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public String help() {
        return """
                create <NAME> - a game
                list - games
                play <NUMBER> <WHITE|BLACK> - a game
                observe <NUMBER> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    private boolean loggedOut = false;

    public boolean isLoggedOut() {
        return loggedOut;
    }

    public String logout() throws Exception {
        server.logout(authToken);
        loggedOut = true;
        return "Logged out successfully";
    }

    public String createGame(String... params) throws Exception {
        if (params.length != 1) {
            throw new Exception("Expected: create <NAME>");
        }
        server.createGame(authToken, params[0]);
        return "Created game: " + params[0];
    }

    public String listGames() throws Exception {
        var result = server.listGames(authToken);
        lastGameList = new ArrayList<>(result.games());

        if (lastGameList.isEmpty()) {
            return "No games available";
        }

        var sb = new StringBuilder();
        for (int i = 0; i < lastGameList.size(); i++) {
            var game = lastGameList.get(i);
            sb.append(i + 1).append(". ").append(game.gameName());
            sb.append(" (white: ").append(game.whiteUsername() == null ? "-" : game.whiteUsername());
            sb.append(", black: ").append(game.blackUsername() == null ? "-" : game.blackUsername());
            sb.append(")\n");
        }
        return sb.toString();
    }

    public String playGame(String... params) throws Exception {
        if (params.length != 2) {
            throw new Exception("Expected: play <NUMBER> <WHITE|BLACK>");
        }
        var game = getGameFromNumber(params[0]);
        var color = params[1].toUpperCase();
        server.joinGame(authToken, color, game.gameID());

        boolean whitePerspective = color.equals("WHITE");
        new GameplayUI("http://localhost:8080", authToken, game.gameID(), whitePerspective).run();

        return "";
    }

    public String observeGame(String... params) throws Exception {
        if (params.length != 1) {
            throw new Exception("Expected: observe <NUMBER>");
        }
        var game = getGameFromNumber(params[0]);

        new GameplayUI("http://localhost:8080", authToken, game.gameID(), true).run();

        return "";
    }

    private GameData getGameFromNumber(String numberStr) throws Exception {
        int number;
        try {
            number = Integer.parseInt(numberStr);
        } catch (NumberFormatException ex) {
            throw new Exception("Game number must be a number");
        }
        if (number < 1 || number > lastGameList.size()) {
            throw new Exception("No such game number. Try 'list' first");
        }
        return lastGameList.get(number - 1);
    }
}