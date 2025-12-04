package ui;

import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import server.CreateGameRequest;
import server.JoinGameRequest;
import server.ListGamesResult;
import websocket.WebSocketFacade;

import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class PostLoginUI {
    ServerFacade facade;
    GameplayUI gameplayUI;
    String url;
    public PostLoginUI(ServerFacade facade, String url) throws ResponseException {
        this.facade = facade;
        this.url = url;
        this.gameplayUI = new GameplayUI(url);
    }

    public boolean postLogin(String authToken) throws ResponseException {
        boolean exit = false;
        boolean quit = false;
        HashMap<Integer, Integer> gameMap = gameListHelper(authToken);
        while(!exit){
            System.out.print("[LOGGED_IN] >>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var inputs = line.split(" ");
            var input = inputs[0];
            switch (input) {
                case "help" -> {
                    System.out.println("List of commands: (Commands are case sensitive)");
                    commandList();
                }
                case "create" -> {
                    if(!validNumArgs(2, inputs.length)){
                        System.out.println("Invalid number of arguments");
                        System.out.println("Game names can't have a space");
                        continue;
                    }
                    String gameName = inputs[1];
                    CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
                    facade.createGame(createGameRequest, authToken);
                    System.out.println("Created a game named " + gameName);
                }
                case "list" -> {
                    gameMap.clear();
                    int i = 1;
                    List<ListGamesResult> games = facade.listGames(authToken);
                    System.out.println("Game list:");
                    for(ListGamesResult game : games){
                        String whiteUsername = (game.whiteUsername() == null) ? "empty" : game.whiteUsername();
                        String blackUsername = (game.blackUsername() == null) ? "empty" : game.blackUsername();
                        System.out.println(i +". " + game.gameName() + ", White player: " + whiteUsername + ", Black player: " + blackUsername);
                        gameMap.put(i, game.gameID());
                        i++;
                    }
                }
                case "join" -> {
                    if(!validNumArgs(3, inputs.length)){
                        System.out.println("Invalid number of arguments");
                        continue;
                    }
                    String team = inputs[2];
                    if(!numFromString(inputs[1])){
                        System.out.println("Invalid command. A number is expected after \"join\"");
                        continue;
                    }
                    int id = Integer.parseInt(inputs[1]);
                    if(gameMap.get(id) == null){
                        System.out.println("Invalid game ID");
                        continue;
                    }
                    team = team.toUpperCase();
                    if(!(Objects.equals(team, "WHITE") || Objects.equals(team, "BLACK"))){
                        System.out.println("Invalid team. Must be BLACK or WHITE");
                        continue;
                    }
                    int gameID = gameMap.get(id);
                    gameplayUI.joinGame(authToken, gameID, team);
                }
                case "observe" -> {
                    if(!validNumArgs(2, inputs.length)){
                        System.out.println("Invalid number of arguments");
                        continue;
                    }
                    if(!numFromString(inputs[1])){
                        System.out.println("Invalid command. A number is expected after \"observe\"");
                        continue;
                    }
                    int id = Integer.parseInt(inputs[1]);
                    if(id > gameMap.size()){
                        System.out.println("Invalid ID");
                        continue;
                    }
                    int gameID = gameMap.get(id);
                    gameplayUI.observeGame(authToken, gameID);
                }
                case "logout" -> {
                    exit = true;
                    System.out.println("Logged out successfully.");
                }
                case "quit" -> {
                    exit = true;
                    quit = true;
                }
                default ->{
                    System.out.println("Unknown command. Commands:");
                    commandList();
                }
            }
        }
        return quit;
    }

    private void commandList() {
        System.out.println("create <NAME> - a game");
        System.out.println("list - games");
        System.out.println("join <ID> [WHITE|BLACK] - a game");
        System.out.println("observe <ID> - a game");
        System.out.println("logout - when you are done");
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }

    private boolean validNumArgs(int expected, int actual){
        return expected == actual;
    }

    public HashMap<Integer, Integer> gameListHelper(String authToken) throws ResponseException {
        HashMap<Integer, Integer> tempGameMap = new HashMap<>();
        int i = 1;
        List<ListGamesResult> games = facade.listGames(authToken);
        for(ListGamesResult game : games){
            tempGameMap.put(i, game.gameID());
            i++;
        }
        return tempGameMap;
    }

    private boolean numFromString(String input) {
        if (input == null) {
            return false;
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
