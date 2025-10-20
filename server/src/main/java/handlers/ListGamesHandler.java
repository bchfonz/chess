package handlers;

import com.google.gson.Gson;
import dataaccess.GameDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.ListGamesResult;
import service.UserService;

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
