package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.*;
import dataaccess.DataAccessException;

public class UserServiceTest {

  private static UserService userService;

  @BeforeAll
  public static void createUserService() throws DataAccessException{
    userService = new UserService();
  }

  @Test
  public void registeNewUser () throws DataAccessException{
    RegisterRequest newRegRequest = new RegisterRequest("Benji", "password", "benji@swagg.com");
    Response res = null;
    Assertions.assertEquals(200, userService.register(newRegRequest, res).message());
  }

}
