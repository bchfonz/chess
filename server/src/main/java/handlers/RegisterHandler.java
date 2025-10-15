package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.eclipse.jetty.server.Authentication;
import service.RegAndLoginResult;
import service.RegisterRequest;
import service.UserService;

import java.util.Map;

public class RegisterHandler implements Handler {

    private final Gson gson = new Gson();
    private final UserService userServiceObj;
    public RegisterHandler(UserService userServiceObj){
        this.userServiceObj = userServiceObj;
    }
    @Override
    public void handle(Context ctx) {
        // Example: read JSON and respond
        RegisterRequest user = gson.fromJson(ctx.body(), RegisterRequest.class);
        System.out.println("Username: " + user.username() + " Password: " + user.password() + " Email: " + user.email());
        RegAndLoginResult result = userServiceObj.register(user);
        if(user.username() == null || user.password() == null || user.email() == null){
            System.out.println("In bad requests");
            ctx.status(400);
            ctx.json(gson.toJson(Map.of("message", "Error: bad request")));
        }
        else if(result == null){
            System.out.println("In username already taken");
            ctx.status(403);
            ctx.json(gson.toJson(Map.of("message", "Error: username already taken")));
        }
        else{
            ctx.status(200);
            System.out.println("In successful register");
            System.out.println("Response: " + ctx.status());

            ctx.result(gson.toJson(result));
        }
    }
}
