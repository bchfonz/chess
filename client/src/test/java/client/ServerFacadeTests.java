package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.RegAndLoginResult;
import service.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
//        facade = new server.ServerFacade(String.format("localhost:%d", port));
//        Not sure if I need the http://, so I have this here just in case I want to switch it real quick
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void validRegister() throws Exception {
        RegAndLoginResult authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void sampleTest() {
        assertTrue(true);
    }

}

