package server;

public record ListGamesResult(int gameID, String whiteUsername, String blackUsername, String gameName) {
}
