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
    private static RegAndLoginResult mainAuth;

    @BeforeAll
    public static void init() throws Exception {

        server = new Server();
        ClearService clear = new ClearService();
        clear.clearAllDB(server.userServiceObj, server.gameServiceObj);
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
        mainAuth = facade.register(new RegisterRequest( "mainUser", "mainPassword", "main@email.com"));
//        System.out.println("AUTHTOKEN: " + mainAuth.authToken());
//        try{
//            mainAuth = facade.register(new RegisterRequest( "mainUser", "mainPassword", "main@email.com"));
//        } catch (ResponseException e) {
//            throw new RuntimeException(e);
//        }

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Order(1)
    @Test
    void validRegister() throws Exception {
        RegAndLoginResult authData = facade.register(new RegisterRequest("user", "password", "user@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Order(2)
    @Test
    void duplicateRegister() throws ResponseException {
//        facade.register(new RegisterRequest("sameUser", "password", "user@email.com"));

        assertThrows(Exception.class, () ->
            facade.register(new RegisterRequest("user", "sameUsername", "user@email.com")));
    }

    @Order(3)
    @Test
    void validLogin() throws Exception {
        RegAndLoginResult authData = facade.login(new LoginRequest("mainUser", "mainPassword"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Order(4)
    @Test
    void invalidLogin() throws Exception {
        assertThrows(Exception.class, () ->
                facade.login(new LoginRequest("user", "wrongPassword")));
    }

    @Order(5)
    @Test
    void validLogout() throws Exception {
        RegAndLoginResult authData = facade.register(new RegisterRequest("testUser", "password", "cool@email.com"));
//         = facade.login(new LoginRequest("user", "password"));
        LogoutRequest logoutRequest = new LogoutRequest(authData.authToken());
        assertTrue(facade.logout(logoutRequest));
    }

    @Order(6)
    @Test
    void invalidLogout() throws Exception {
        assertFalse(facade.logout(new LogoutRequest("badtoken")));
    }

    @Order(7)
    @Test
    void validCreateGame() throws Exception {
        CreateGameRequest createGameRequest = new CreateGameRequest(mainAuth.authToken(), "Jesus Loves You");
        assertDoesNotThrow(() -> facade.createGame(createGameRequest, mainAuth.authToken()));
    }

    @Order(8)
    @Test
    void invalidCreateGame() throws Exception {
        CreateGameRequest req = new CreateGameRequest(mainAuth.authToken(), null);
        assertFalse(facade.createGame(req, mainAuth.authToken()));
    }

    @Order(9)
    @Test
    void validListGames() throws Exception {
        facade.createGame(new CreateGameRequest(mainAuth.authToken(), "I need a game in the database"), mainAuth.authToken());
        facade.createGame(new CreateGameRequest(mainAuth.authToken(), "2.0"), mainAuth.authToken());
        var games = facade.listGames(mainAuth.authToken());
        assertNotNull(games);
    }

    @Order(10)
    @Test
    void invalidListGames() throws Exception {
        facade.createGame(new CreateGameRequest(mainAuth.authToken(), "I need a game in the database"), mainAuth.authToken());
        facade.createGame(new CreateGameRequest(mainAuth.authToken(), "2.0"), mainAuth.authToken());
        var games = facade.listGames("Bad Auth Token");
        assertNull(games);
    }

    @Order(11)
    @Test
    void validJoinGame() throws Exception {
        boolean joined = facade.joinGame(new JoinGameRequest("BLACK", 1), mainAuth.authToken());
        assertTrue(joined);
    }
    @Order(11)
    @Test
    void invalidJoinGame() throws Exception {
        boolean joined = facade.joinGame(new JoinGameRequest("YELLOW", 1), mainAuth.authToken());
        assertFalse(joined);
    }

//    @Test
//    public void sampleTest() {
//        assertTrue(true);
//    }

}

