package server;

import org.eclipse.jetty.server.Authentication.User;

import com.google.gson.Gson;

import model.UserData;
import spark.*;
import service.*;
import java.util.*;

public class Server {
    // private final Service service = new Service(null, null, null);
    Gson gson = new Gson();
    UserService userServiceObject = new UserService();
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // This line initializes the server and can be removed once you have a
        // functioning endpoint
        Spark.init();

        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clearApplication);

        Spark.awaitInitialization();


 
        return Spark.port();

    }
    private Object registerUser (Request req, Response res) {
        // UserData userData = gson.fromJson(req.body(), UserData.class); // Convert JSON to Java Object
        RegisterRequest user = gson.fromJson(req.body(), RegisterRequest.class);
        RegisterResult regResult = userServiceObject.register(user ,res);
        System.out.println(res.status());
        return gson.toJson(regResult);

        //Bad request: Doesn't provide all the needed info
        // if (userData.username() == null || userData.password() == null || userData.email() == null) {
        //     res.status(400);
        //     return gson.toJson(Map.of("message", "Error: bad request"));
        // }else if(userData.email() != null && userData.password() != null && userData.email() != null){
        //     //Successful registration
        //     if(regResult != null){
        //         res.status(200);
        //         return gson.toJson(regResult);
        //     //User already exists
        //     }else{
        //         res.status(403);
        //         return gson.toJson(regResult);
        //     }
        // //Other errors    
        // }else{
        //     res.status(500);
        //     return gson.toJson("{\"message\": \"Error: (description of error\"}");
        // }
    }

    private Object login (Request req, Response res) {
        LoginRequest loginData = gson.fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = userServiceObject.login(loginData, res);
        return gson.toJson(loginResult);
    }
    private Object logout (Request req, Response res) {
        String authToken = gson.fromJson(req.headers("authorization"), String.class);
        System.out.println("In logout. String authToken = " + authToken);
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutResult logoutResult = userServiceObject.logout(logoutRequest, res);
        System.out.println(logoutRequest.authToken());
        return gson.toJson(logoutResult);
    }
    private Object listGames (Request req, Response res) {
        return "";
    }
    private Object createGame (Request req, Response res) {
        return "";
    }
    private Object joinGame (Request req, Response res) {
        return "";
    }
    private Object clearApplication (Request req, Response res) {
        res.status(200);
        // service.clearAllData();
        return "";
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
