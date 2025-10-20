package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GameServiceTest {
    private static GameService gameService;
    private static UserService userService;

    @BeforeAll
    public static void createUserService() throws DataAccessException {
        userService = new UserService();
    }

}
