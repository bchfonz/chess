package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

public class UserServiceTest {

    private static UserService userService;

    @BeforeAll
    public static void createUserService() throws DataAccessException {
        userService = new UserService();
    }
    @BeforeEach
    public void createUser(){
        userService.register(new RegisterRequest("username", "password", "email"));
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
        RegisterRequest duplicateRegRequest = new RegisterRequest("username", "newPassword", "benjipups@swagg.com");
        RegAndLoginResult duplicateRegResult = userService.register(duplicateRegRequest);
        Assertions.assertNull(duplicateRegResult, "Shouldn't allow duplicate registration");

    }

    @Test
    public void successfulLogin() throws DataAccessException{
        LoginRequest newLoginRequest = new LoginRequest("username", "password");
        Assertions.assertNotNull(userService.login(newLoginRequest));
    }

    @Test
    public void invalidLogin() throws DataAccessException{
        LoginRequest newLoginRequest = new LoginRequest("username", "wrongPassword");
        Assertions.assertNull(userService.login(newLoginRequest));
    }
}
