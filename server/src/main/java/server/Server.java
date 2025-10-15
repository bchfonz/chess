package server;

import handlers.*;
import io.javalin.*;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        UserService userServiceObj = new UserService();
        // Register your endpoints and exception handlers here.
        javalin.delete("/db", new ClearHandler());
        javalin.post("/user", new RegisterHandler(userServiceObj));
        javalin.post("/session", new LoginHandler(userServiceObj));
        javalin.delete("/session", new LogoutHandler(userServiceObj));
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
