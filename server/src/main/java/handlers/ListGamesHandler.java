package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import server.ListGamesResult;
import service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ListGamesHandler implements Handler {
    GameService gameServiceObj;
    UserService userServiceObj;
    Gson gson = new Gson();
    public ListGamesHandler(GameService gameServiceObj, UserService userServiceObj) {
        this.gameServiceObj = gameServiceObj;
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
        String authToken = ctx.header("authorization");
        try{
            if(userServiceObj.authDAO.getAuth(authToken) == null){
                ctx.status(401);
                ctx.json(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            else{
                List<ListGamesResult> gameList = gameServiceObj.gameList();
                ctx.status(200);
                ctx.json(gson.toJson(Map.of("games" ,gameList)));

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
