package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    private static UserService userService;

    @BeforeAll
    public static void createUserService() throws DataAccessException {
        userService = new UserService();
    }

    @Test
    public void successfulNewRegister () throws DataAccessException{
        RegisterRequest newRegRequest = new RegisterRequest("Benji", "password", "benji@swagg.com");
        RegAndLoginResult registerResult = userService.register(newRegRequest);
        Assertions.assertNotNull(registerResult, "User already exists");
        Assertions.assertEquals("Benji", registerResult.username(), "Username should match");
        Assertions.assertNotNull(registerResult.authToken(), "authToken shouldn't be null");
    }

    @Test
    public void duplicateRegister () throws DataAccessException{
        RegisterRequest newRegRequest = new RegisterRequest("Benji", "password", "benji@swagg.com");
        RegisterRequest duplicateRegRequest = new RegisterRequest("Benji", "newPassword", "benjipups@swagg.com");
        userService.register(newRegRequest);
        RegAndLoginResult duplicateRegResult = userService.register(duplicateRegRequest);
        Assertions.assertNull(duplicateRegResult, "Shouldn't allow duplicate registration");

    }

}
