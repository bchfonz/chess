package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.LogoutRequest;
import service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class LogoutHandler implements Handler {
    private final Gson gson = new Gson();
    private final UserService userServiceObj;
    public LogoutHandler(UserService userServiceObj){
        this.userServiceObj = userServiceObj;
    }
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
//            System.out.println("Database connection credentials are correct");
        } catch (DataAccessException | SQLException e) {
            ctx.status(500);
            ctx.json(gson.toJson(Map.of("message", "Error: unable to connect to database")));
            return;
        }
        LogoutRequest logoutRequest = new LogoutRequest(ctx.header("authorization"));
        if(userServiceObj.logout(logoutRequest)){
            ctx.status(200);
        }
        else{
            ctx.status(401);
            ctx.json(gson.toJson(Map.of("message", "Error: unauthorized")));
        }

    }
}
