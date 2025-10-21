package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GameServiceTest {
    private static GameService gameService;
    private static UserService userService;
    private static String authToken;

    @BeforeAll
    public static void initServices() throws DataAccessException {
        gameService = new GameService();
        userService = new UserService();
        RegAndLoginResult newUser =  userService.register(new RegisterRequest("username", "password", "email"));
        authToken = newUser.authToken();
    }

    @Test
    public void validCreateGame(){
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "My first new game :)");
        int createGameResult = gameService.createGame(createGameRequest);
        Assertions.assertNotEquals(0, createGameResult);
    }

    @Test
    public void unauthorizedCreateGame(){
        CreateGameRequest createGameRequest = new CreateGameRequest("Not a valid authToken", null);
        int createGameResult = gameService.createGame(createGameRequest);
        Assertions.assertEquals(0, createGameResult);
    }

}
