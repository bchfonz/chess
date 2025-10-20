package handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;

public class ClearHandler implements Handler {
    private final ClearService clearServiceObj = new ClearService();
    private final UserService userServiceObj;
    private final GameService gameServiceObj;

    public ClearHandler(UserService userServiceObj, GameService gameServiceObj) {
        this.userServiceObj = userServiceObj;
        this.gameServiceObj = gameServiceObj;
    }

    @Override
    public void handle(Context ctx){
        clearServiceObj.clearAllDB(userServiceObj, gameServiceObj);
        if(clearServiceObj.isCleared()){
            ctx.status(200);
        }
        else{
            ctx.status(500);
        }
    }
}
