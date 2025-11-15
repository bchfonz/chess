package server;

import com.google.gson.Gson;
import exception.ResponseException;
import service.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;


    public ServerFacade(String url){
        serverUrl = url;
    }

    public RegAndLoginResult register(RegisterRequest regRequest) throws ResponseException {
        var request = buildRequest("POST", "/user", regRequest);
        var response = sendRequest(request);
        return handleResponse(response, RegAndLoginResult.class);
    }
    public RegAndLoginResult login(LoginRequest loginRequest) throws ResponseException {
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, RegAndLoginResult.class);
    }
    public boolean logout(LogoutRequest logoutRequest) throws ResponseException {
        var request = buildRequest("DELETE", "/session", logoutRequest);
        var response = sendRequest(request);
        return isSuccessful(response.statusCode());
    }
    public ListGamesResult listGames() throws ResponseException {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }
    public boolean createGame(CreateGameRequest createGameRequest) throws ResponseException {
        var request = buildRequest("POST", "/game", createGameRequest);
        var response = sendRequest(request);
        System.out.println("Response code: " + response.statusCode());
        return isSuccessful(response.statusCode());

    }
    public void joinGame(JoinGameRequest joinGameRequest) throws ResponseException {
        var request = buildRequest("PUT", "/game", joinGameRequest);
        sendRequest(request);
    }
    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
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

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}


