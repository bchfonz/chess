package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.CreateGameRequest;
import server.RegisterRequest;

public class ClearServiceTest {
    private static GameService gameService;
    private static UserService userService;
    private static ClearService clearService;
    private static String authToken;
    private static String username;

    @BeforeAll
    public static void initServices() throws DataAccessException {
        gameService = new GameService();
        userService = new UserService();
        clearService = new ClearService();
        userService.register(new RegisterRequest("username", "password", "email"));
        CreateGameRequest createGameRequest1 = new CreateGameRequest(authToken, "New game 1");
        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken, "New game 2");
        CreateGameRequest createGameRequest3 = new CreateGameRequest(authToken, "New game 3");
        gameService.createGame(createGameRequest1);
        gameService.createGame(createGameRequest2);
        gameService.createGame(createGameRequest3);
    }

    @Test
    public void validClearTest(){
        clearService.clearAllDB(userService, gameService);
        Assertions.assertTrue(userService.authDAO.emptyDB(), "authDB should be empty");
        Assertions.assertTrue(userService.userDAO.emptyDB(), "userDB should be empty");
        Assertions.assertTrue(gameService.gameDAO.emptyDB(), "gameDB should be empty");
    }


}
