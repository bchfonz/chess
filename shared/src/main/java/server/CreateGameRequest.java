package server;

public record CreateGameRequest(String authToken, String gameName) {
}
