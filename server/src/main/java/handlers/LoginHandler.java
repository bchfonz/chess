package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.LoginRequest;
import service.RegAndLoginResult;
import service.RegisterRequest;
import service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class LoginHandler implements Handler {

    private final Gson gson = new Gson();
    private final UserService userServiceObj;
    public LoginHandler(UserService userServiceObj){
        this.userServiceObj = userServiceObj;
    }

    @Override
    public void handle(@NotNull Context ctx){
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("Database connection credentials are correct");
        } catch (DataAccessException | SQLException e) {
            ctx.status(500);
            ctx.json(gson.toJson(Map.of("message", "Error: unable to connect to database")));
            return;
        }
        //I need to make sure they aren't already logged in
        LoginRequest user = gson.fromJson(ctx.body(), LoginRequest.class);
        RegAndLoginResult loginResult = userServiceObj.login(user);
        if(user.username() == null || user.password() == null){
            ctx.status(400);
            ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
        }
        else if(loginResult != null){
            ctx.status(200);
            ctx.result(gson.toJson(loginResult));
        }
        else{
            ctx.status(401);
            ctx.json(gson.toJson(Map.of("message", "Error: unauthorized")));
        }
    }
}
