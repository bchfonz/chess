package server;

import handlers.*;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", new ClearHandler());
        javalin.post("/user", new RegisterHandler());
        javalin.post("/session", new LoginHandler());
        javalin.delete("/session", new LogoutHandler());
        javalin.get("/game", new ListGamesHandler());
        javalin.post("/game", new CreateGameHandler());




    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
