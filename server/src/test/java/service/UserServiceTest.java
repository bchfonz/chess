package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

public class UserServiceTest {

    private static UserService userService;
    private static String usernameAuthToken;

    @BeforeAll
    public static void createUserService() throws DataAccessException {
        userService = new UserService();
        RegAndLoginResult newUser =  userService.register(new RegisterRequest("username", "password", "email"));
        usernameAuthToken = newUser.authToken();
    }

    @Test
    public void successfulNewRegister (){
        RegisterRequest newRegRequest = new RegisterRequest("Benji", "password", "benji@swagg.com");
        RegAndLoginResult registerResult = userService.register(newRegRequest);
        Assertions.assertNotNull(registerResult, "User already exists");
        Assertions.assertEquals("Benji", registerResult.username(), "Username should match");
        Assertions.assertNotNull(registerResult.authToken(), "authToken shouldn't be null");
    }

    @Test
    public void duplicateRegister (){
        RegisterRequest duplicateRegRequest = new RegisterRequest("username", "newPassword", "benjipups@swagg.com");
        RegAndLoginResult duplicateRegResult = userService.register(duplicateRegRequest);
        Assertions.assertNull(duplicateRegResult, "Shouldn't allow duplicate registration");

    }

    @Test
    public void successfulLogin(){
        LoginRequest newLoginRequest = new LoginRequest("username", "password");
        Assertions.assertNotNull(userService.login(newLoginRequest));
    }

    @Test
    public void invalidLogin(){
        LoginRequest newLoginRequest = new LoginRequest("username", "wrongPassword");
        Assertions.assertNull(userService.login(newLoginRequest));
    }

    @Test
    public void successfulLogout(){
        LogoutRequest newLogout = new LogoutRequest(usernameAuthToken);
        Assertions.assertTrue(userService.logout(newLogout));
    }

    @Test
    public void invalidLogout(){
        LogoutRequest newLogout = new LogoutRequest("Not an authToken");
        Assertions.assertFalse(userService.logout(newLogout));
    }
}
