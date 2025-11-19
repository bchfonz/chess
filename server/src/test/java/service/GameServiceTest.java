package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTest {
    private static GameService gameService;
    private static UserService userService;
    private static String authToken;
    private static String username;

    @BeforeAll
    public static void initServices() throws DataAccessException {
        gameService = new GameService();
        userService = new UserService();
        RegAndLoginResult newUser =  userService.register(new RegisterRequest("username", "password", "email"));
        authToken = newUser.authToken();
        username = newUser.username();
    }
    @BeforeEach
    public void clearGameDB(){
        gameService.gameDAO.clearGameDB();
    }

    @Test
    public void validCreateGame(){
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "My first new game :)");
        int createGameResult = gameService.createGame(createGameRequest);
        Assertions.assertNotEquals(0, createGameResult, "Should return the gameID ");
    }

    @Test
    public void unauthorizedCreateGame(){
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, null);
        int createGameResult = gameService.createGame(createGameRequest);
        Assertions.assertEquals(0, createGameResult, "Should return 0 because the authToken was not valid");
    }

    @Test
    public void validListGames(){
        CreateGameRequest createGameRequest1 = new CreateGameRequest(authToken, "New game 1");
        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken, "New game 2");
        CreateGameRequest createGameRequest3 = new CreateGameRequest(authToken, "New game 3");
        gameService.createGame(createGameRequest1);
        gameService.createGame(createGameRequest2);
        gameService.createGame(createGameRequest3);
        Assertions.assertEquals(3, gameService.gameList().size(), "Should return all created games");
    }

    @Test
    public void invalidListGames(){
        CreateGameRequest createGameRequest1 = new CreateGameRequest(authToken, "New game 1");
        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken, "New game 2");
        CreateGameRequest createGameRequest3 = new CreateGameRequest(authToken, "New game 3");
        gameService.createGame(createGameRequest1);
        gameService.createGame(createGameRequest2);
        gameService.createGame(createGameRequest3);
        Assertions.assertNotEquals(2, gameService.gameList().size(), "Should return all created games");
    }

    @Test
    public void validJoinGame(){
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "My first new game :)");
        int createGameResult = gameService.createGame(createGameRequest);
        Assertions.assertNotNull(gameService.joinGame(username, "BLACK", createGameResult), "Should return the game for successful join");
    }

    @Test
    public void alreadyTakenJoinGame(){
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "My first new game :)");
        int createGameResult = gameService.createGame(createGameRequest);
        gameService.joinGame("Random User", "BLACK", createGameResult);
        Assertions.assertNull(gameService.joinGame(username, "BLACK", createGameResult), "Should return the game for successful join");

    }

}
