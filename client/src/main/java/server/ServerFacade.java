package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import service.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    Gson gson = new Gson();


    public ServerFacade(String url){
        serverUrl = url;
    }

    public RegAndLoginResult register(RegisterRequest regRequest) throws ResponseException {
        var request = buildRequest("POST", "/user", regRequest, null);
        var response = sendRequest(request);
        System.out.println("register response code: " + response.statusCode());
        if(!isSuccessful(response.statusCode())){
            if(response.statusCode() == 403){
                System.out.println("Error " + response.statusCode() + ": message:  \"Error: username already taken\"");
            }
            else if(response.statusCode() == 400){
                error400();
            }
            return null;
        }
        return handleResponse(response, RegAndLoginResult.class);
    }
    public RegAndLoginResult login(LoginRequest loginRequest) throws ResponseException {
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        System.out.println("login response code: " + response.statusCode());
        if(!isSuccessful(response.statusCode())){
            if(response.statusCode() == 401){
                error401();
            }
            else if(response.statusCode() == 400){
                error400();
            }
            return null;
        }
        return handleResponse(response, RegAndLoginResult.class);
    }
    public boolean logout(LogoutRequest logoutRequest) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, logoutRequest.authToken());
        var response = sendRequest(request);
        System.out.println("logout response code: " + response.statusCode());
        return isSuccessful(response.statusCode());
    }
    public List<ListGamesResult> listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        System.out.println("ListGames response:" + response.body());
        if(!isSuccessful(response.statusCode())){
            return null;
        }
        else{
            var result = handleResponse(response, ListGamesResponse.class);
            assert result != null;
            return result.games();
        }
    }
    public boolean createGame(CreateGameRequest createGameRequest, String authToken) throws ResponseException {
        var request = buildRequest("POST", "/game", createGameRequest, authToken);
        var response = sendRequest(request);
        System.out.println("createGame response code: " + response.statusCode());
        return isSuccessful(response.statusCode());

    }
    public GameData joinGame(JoinGameRequest joinGameRequest, String authToken) throws ResponseException {
        var request = buildRequest("PUT", "/game", joinGameRequest, authToken);
        var response = sendRequest(request);
        if(!isSuccessful(response.statusCode())){
            if(response.statusCode() == 401){
                error401();
            }
            else if(response.statusCode() == 400){
                error400();
            }
            else if(response.statusCode() == 403){
                System.out.println("Error 403: message:  \"Error: already taken\"");
            }
        }
        if(isSuccessful(response.statusCode())){
            return gson.fromJson(response.body(), GameData.class);
        }
        else{
            return null;
        }
    }

//    public GameData observeGame(int id, String authToken) throws ResponseException {
//        var request = buildRequest("GET", "/game", id, authToken);
//        var resonse = sendRequest(request);
//    }
    public boolean clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        return isSuccessful(response.statusCode());
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path));

        // Add JSON body if provided
        if (body != null) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)));
            builder.setHeader("Content-Type", "application/json");
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        // Add optional auth header
        if (authToken != null) {
            builder.setHeader("authorization", authToken);
        }

        return builder.build();
    }

    public void error401(){
        System.out.println("Error 401: message:  \"Error: unauthorized\"");
    }
    public void error400(){
        System.out.println("Error 400: message:  \"Error: bad request\"");
    }

//    private HttpRequest buildRequest(String method, String path, Object body) {
//        var request = HttpRequest.newBuilder()
//                .uri(URI.create(serverUrl + path))
//                .method(method, makeRequestBody(body));
//        if (body != null) {
//            request.setHeader("Content-Type", "application/json");
//        }
//        return request.build();
//    }
//
//    // Header-only request (no JSON body)
//    private HttpRequest buildRequest(String method, String path, String authToken) {
//        var request = HttpRequest.newBuilder()
//                .uri(URI.create(serverUrl + path))
//                .method(method, HttpRequest.BodyPublishers.noBody());
//
//        if (authToken != null) {
//            request.setHeader("authorization", authToken);
//        }
//
//        return request.build();
//    }

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


