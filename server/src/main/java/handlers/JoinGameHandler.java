package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.JoinGameRequest;
import service.ListGamesResult;
import service.UserService;

import java.util.List;
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
        String authToken = ctx.header("authorization");
        JoinGameRequest joinGameRequest = gson.fromJson(ctx.body(), JoinGameRequest.class);
        AuthData authData = userServiceObj.authDAO.getAuth(authToken);
        try{
            if(authData == null){
                System.out.println("Authdata: " + userServiceObj.authDAO.getAuth(authToken));
                ctx.status(401);
                ctx.json(gson.toJson(Map.of("message", "Error: unauthorized")));
            }
            else if(joinGameRequest.playerColor() == null || joinGameRequest.gameID() == 0 ||
                    (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK"))){
                ctx.status(400);
                ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
            }
            else if(!gameServiceObj.joinGame(authData.username(), joinGameRequest.playerColor(), joinGameRequest.gameID())){
                ctx.status(403);
                ctx.json(gson.toJson(Map.of("message", "Error: already taken")));
            }
            else{
                ctx.status(200);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
