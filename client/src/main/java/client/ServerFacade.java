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
        var httpRequest = buildRequest("POST", "/user", request);
        var response = sendRequest(httpRequest);
        return handleResponse(response, RegisterResult.class);
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

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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
            throw new Exception("failure: " + status);
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;

    }
}
