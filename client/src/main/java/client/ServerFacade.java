package client;

import model.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        return null;
    }

    public LoginResult login(String username, String password) throws Exception {
        return null;
    }

    public void logout(String authToken) throws Exception {

    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception {
        return null;
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        return null;
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws Exception {

    }
}
