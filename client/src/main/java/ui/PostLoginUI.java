package ui;

import dataaccess.SqlGameDAO;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import service.CreateGameRequest;
import service.JoinGameRequest;
import service.ListGamesResult;

import java.net.CacheResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class PostLoginUI {
    ServerFacade facade;
    GameplayUI gameplayUI = new GameplayUI();
    public PostLoginUI(ServerFacade facade){
        this.facade = facade;
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
                    System.out.println("create <NAME> - a game");
                    System.out.println("list - games");
                    System.out.println("join <ID> [WHITE|BLACK] - a game");
                    System.out.println("play <ID> - a game");
                    System.out.println("observe <ID> - a game");
                    System.out.println("logout - when you are done");
                    System.out.println("quit - playing chess");
                    System.out.println("help - with possible commands");
                }
                case "create" -> {
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
                        String whiteUsername;
                        if(game.whiteUsername() == null){
                            whiteUsername = "empty";
                        }
                        else{
                            whiteUsername = game.whiteUsername();
                        }
                        String blackUsername;
                        if(game.blackUsername() == null){
                            blackUsername = "empty";
                        }
                        else{
                            blackUsername = game.blackUsername();
                        }
                        System.out.println(i +". " + game.gameName() + ", White player: " + whiteUsername + ", Black player: " + blackUsername);
                        gameMap.put(i, game.gameID());
                        i++;
                    }
                }
                case "join" -> {
                    String team = inputs[2];
                    int id = Integer.parseInt(inputs[1]);
                    int gameID = gameMap.get(id);
                    JoinGameRequest joinGameRequest = new JoinGameRequest(team, gameID);
                    GameData gameData = facade.joinGame(joinGameRequest, authToken);
                    if(gameData != null){
                        System.out.println("Joined game");
                        gameplayUI.joinGame(gameData, team);
                    }
                    else{
                        System.out.println("Unable to join game");
                    }
                }
                case "observe" -> {
                    int id = Integer.parseInt(inputs[1]);
                    int gameID = 0;
                    if(gameMap.get(id) == null){
                        System.out.println("Invalid game ID");
                    }
                    else{
                        gameID = gameMap.get(id);
                        GameData game = new SqlGameDAO().getGame(gameID);
                        if(game == null){
                            System.out.println("Invalid game ID");
                        }
                        else{
                            gameplayUI.observeGame(game);
                        }
                    }


                }
                case "logout" -> {
                    exit = true;
                    System.out.println("Logged out successfully.");
                }
                case "quit" -> {
                    exit = true;
                    quit = true;
                }
                default -> System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
        }
        return quit;
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
}
