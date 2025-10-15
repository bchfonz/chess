package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class LogoutHandler implements Handler {
    private final Gson gson = new Gson();
    private final UserService userServiceObj;
    public LogoutHandler(UserService userServiceObj){
        this.userServiceObj = userServiceObj;
    }
    @Override
    public void handle(@NotNull Context ctx) throws Exception {


    }
}
