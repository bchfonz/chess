package handlers;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import server.RegAndLoginResult;
import server.RegisterRequest;
import service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class RegisterHandler implements Handler {

    private final Gson gson = new Gson();
    private final UserService userServiceObj;
    public RegisterHandler(UserService userServiceObj){
        this.userServiceObj = userServiceObj;
    }
    @Override
    public void handle(Context ctx) {

//        Test DataBase connection
        try (Connection conn = DatabaseManager.getConnection()) {
//            System.out.println("Database connection credentials are correct");
        } catch (DataAccessException | SQLException e) {
            ctx.status(500);
            ctx.json(gson.toJson(Map.of("message", "Error: unable to connect to database")));
            return;
        }



        RegisterRequest user = gson.fromJson(ctx.body(), RegisterRequest.class);
//        System.out.println("Username: " + user.username() + " Password: " + user.password() + " Email: " + user.email());
        RegAndLoginResult result = userServiceObj.register(user);
        if(user.username() == null || user.password() == null || user.email() == null){
            System.out.println("In bad requests");
            ctx.status(400);
            ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
        }
        else if(result == null){
            System.out.println("In username already taken");
            ctx.status(403);
            ctx.json(gson.toJson(Map.of("message", "Error: username already taken")));
        }
        else{
            ctx.status(200);
//            System.out.println("In successful register");
//            System.out.println("Response: " + ctx.status());

            ctx.result(gson.toJson(result));
        }
    }
}
