package handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class ListGamesHandler implements Handler {
    GameService gameServiceObj;
    public ListGamesHandler(GameService gameServiceObj) {
        this.gameServiceObj = gameServiceObj;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {

    }
}
