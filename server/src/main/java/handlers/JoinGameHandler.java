package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import server.JoinGameRequest;
import service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class JoinGameHandler implements Handler {
    private final Gson gson = new Gson();
    private final GameService gameServiceObj;
    private final UserService userServiceObj;
    public JoinGameHandler(GameService gameServiceObj, UserService userServiceObj) {
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
        JoinGameRequest joinGameRequest = gson.fromJson(ctx.body(), JoinGameRequest.class);
        AuthData authData = userServiceObj.authDAO.getAuth(authToken);
        try{
            if(authData == null){
                ctx.status(401);
                ctx.json(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            else if(joinGameRequest.playerColor() == null || joinGameRequest.gameID() == 0 ||
                    (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK"))){
                ctx.status(400);
                ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
            }
            else if(gameServiceObj.joinGame(authData.username(), joinGameRequest.playerColor(), joinGameRequest.gameID()) == null){
                ctx.status(403);
                ctx.json(gson.toJson(Map.of("message", "Error: already taken")));
            }
            else{
                ctx.status(200);
                GameData gameToJoin = gameServiceObj.joinGame(authData.username(), joinGameRequest.playerColor(), joinGameRequest.gameID());
                ctx.json(gson.toJson(gameToJoin));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
