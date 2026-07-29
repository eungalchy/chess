package client;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        var request = new RegisterRequest(username, password, email);
        var httpRequest = buildRequest("POST", "/user", request, null);
        var response = sendRequest(httpRequest);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws Exception {
        var request = new LoginRequest(username, password);
        var httpRequest = buildRequest("POST", "/session", request, null);
        var response = sendRequest(httpRequest);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(String authToken) throws Exception {
        var httpRequest = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(httpRequest);
        handleResponse(response, null);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception {
        var request = new CreateGameRequest(gameName);
        var httpRequest = buildRequest("POST", "/game", request, authToken);
        var response = sendRequest(httpRequest);
        return handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        var httpRequest = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(httpRequest);
        return handleResponse(response, ListGamesResult.class);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws Exception {
        var request = new JoinGameRequest(playerColor, gameID);
        var httpRequest = buildRequest("PUT", "/game", request, authToken);
        var response = sendRequest(httpRequest);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(body));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (status / 100 != 2) {
            String message = switch (status) {
                case 400 -> "Invalid request. Please check your input.";
                case 401 -> "Unauthorized. Please log in again.";
                case 403 -> "That username or game is already taken.";
                default -> "Something went wrong. Please try again.";
            };
            throw new Exception(message);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    public void clear() throws Exception {
        var httpRequest = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(httpRequest);
        handleResponse(response, null);
    }
}
