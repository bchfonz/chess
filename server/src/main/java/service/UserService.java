package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import io.javalin.http.Context;
import java.util.UUID;

public class UserService {
    private final localUserDB userDAO = new localUserDB();
    private final localAuthDB authDAO = new localAuthDB();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegAndLoginResult register(RegisterRequest registerRequest) {
        UserData userFromReq = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        try{
            if(userDAO.getUser(userFromReq.username()) == null){
                userDAO.addUser(userFromReq);
                AuthData newUserAuth = new AuthData(generateToken(), userFromReq.username());
                authDAO.addAuth(newUserAuth);
                return new RegAndLoginResult(userFromReq.username(), newUserAuth.authToken());
            }
            else{
                return null;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }
    public RegAndLoginResult login(LoginRequest loginRequest) {
        return null;
    }
    public void logout(LogoutRequest logoutRequest) {}
}