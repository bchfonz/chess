package client;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.*;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        ClearService clear = new ClearService();
        clear.clearAllDB(server.userServiceObj, server.gameServiceObj);
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void validRegister() throws Exception {
        RegAndLoginResult authData = facade.register(new RegisterRequest("user", "password", "user@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void duplicateRegister() throws ResponseException {
//        facade.register(new RegisterRequest("sameUser", "password", "user@email.com"));

        assertThrows(Exception.class, () ->
            facade.register(new RegisterRequest("user", "sameUsername", "user@email.com")));
    }

    @Test
    void validLogin() throws Exception {
        RegAndLoginResult authData = facade.login(new LoginRequest("user", "password"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void invalidLogin() throws Exception {
        assertThrows(Exception.class, () ->
                facade.login(new LoginRequest("user", "wrongPassword")));
    }

    @Test
    void logout() throws Exception{
        RegAndLoginResult authData = facade.login(new LoginRequest("user", "password"));
        LogoutRequest logoutRequest = new LogoutRequest(authData.authToken());
        assertDoesNotThrow(() -> facade.logout(logoutRequest));
    }

    @Test
    void invalidLogout() throws Exception{
        assertFalse(facade.logout(new LogoutRequest("badtoken")));
    }

    @Test
    public void sampleTest() {
        assertTrue(true);
    }

}

