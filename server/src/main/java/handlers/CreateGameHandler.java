package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import server.CreateGameRequest;
import service.GameService;
import service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class CreateGameHandler implements Handler {
    GameService gameServiceObj;
    UserService userServiceObj;
    Gson gson = new Gson();
    public CreateGameHandler(GameService gameServiceObj, UserService userServiceObj) {
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
        System.out.println("Authtoken: " + authToken);
        Map<String, String> gameMap = gson.fromJson(ctx.body(), Map.class);
        String gameName = gameMap.get("gameName");
//        System.out.println("Game name: " + gameName);


        try{
            if(gameName == null){
                System.out.println("Game name is null: " + gameName);
                ctx.status(400);
                ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
            }
            else if(userServiceObj.authDAO.getAuth(authToken) == null){
                ctx.status(401);
                ctx.json(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            else{
                CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
                int gameID = gameServiceObj.createGame(createGameRequest);
                ctx.status(200);
                ctx.json(gson.toJson(Map.of("gameID", gameID)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
