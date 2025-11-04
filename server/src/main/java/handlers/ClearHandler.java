package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class ClearHandler implements Handler {
    private final ClearService clearServiceObj = new ClearService();
    private final UserService userServiceObj;
    private final GameService gameServiceObj;
    Gson gson = new Gson();

    public ClearHandler(UserService userServiceObj, GameService gameServiceObj) {
        this.userServiceObj = userServiceObj;
        this.gameServiceObj = gameServiceObj;
    }

    @Override
    public void handle(Context ctx){
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("Database connection credentials are correct");
        } catch (DataAccessException | SQLException e) {
            ctx.status(500);
            ctx.json(gson.toJson(Map.of("message", "Error: unable to connect to database")));
            return;
        }
        clearServiceObj.clearAllDB(userServiceObj, gameServiceObj);
        if(clearServiceObj.isCleared()){
            ctx.status(200);
        }
        else{
            ctx.status(500);
        }
    }
}
