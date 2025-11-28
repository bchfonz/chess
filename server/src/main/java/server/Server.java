package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import handlers.*;
import io.javalin.*;
import service.GameService;
import service.UserService;
import websocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;
    public UserService userServiceObj;
    public GameService gameServiceObj;

    public Server() {
        Gson gson = new Gson();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        userServiceObj = new UserService();
        gameServiceObj = new GameService();
        WebSocketHandler webSocetkHandler = new WebSocketHandler();
//        UserService userServiceObj = new UserService();
//        GameService gameServiceObj = new GameService();
        // Register your endpoints and exception handlers here.
        javalin.delete("/db", new ClearHandler(userServiceObj, gameServiceObj));
        javalin.post("/user", new RegisterHandler(userServiceObj));
        javalin.post("/session", new LoginHandler(userServiceObj));
        javalin.delete("/session", new LogoutHandler(userServiceObj));
        javalin.get("/game", new ListGamesHandler(gameServiceObj, userServiceObj));
        javalin.post("/game", new CreateGameHandler(gameServiceObj, userServiceObj));
        javalin.put("/game", new JoinGameHandler(gameServiceObj, userServiceObj));
        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocetkHandler);
            ws.onMessage(webSocetkHandler);
            ws.onClose(webSocetkHandler);

            // When a client connects
//            ws.onConnect(ctx -> {
//                ctx.enableAutomaticPings();
//                System.out.println("New client connected: ");
//                ctx.send( gson.toJson("Welcome! Your session id is cool") );
//            });
//
//            // When a message is received
//            ws.onMessage(ctx -> {
//                String msg = ctx.message();
//                // Ignore heartbeat messages
//                System.out.println("This is the message:" + msg);
//                ctx.send(gson.toJson("Server echo: " + msg));
//
//
//                // Echo back to the sender
//
//
//                // Broadcast to all other clients
//                System.out.println("Your session ID is: " + ctx.sessionId());
//
//
//            });
//
//            // When a client disconnects
//            ws.onClose(ctx -> {
//                System.out.println("Client disconnected: ");
//            });
//
//            // On error
            ws.onError(ctx -> {
                System.err.println(
                        "WebSocket error from " + ": " + ctx.error()
                );
            });

        });
        try {
            new DatabaseManager();
            System.out.println("Database and tables successfully set up!");
        } catch (DataAccessException e) {
            System.out.println("what da fweak");
        }



    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
