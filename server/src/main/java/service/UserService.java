package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.LoginRequest;
import server.LogoutRequest;
import server.RegAndLoginResult;
import server.RegisterRequest;

import java.util.UUID;

public class UserService {
    public final SqlUserDAO userDAO = new SqlUserDAO();
    public final SqlAuthDAO authDAO = new SqlAuthDAO();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }



    public RegAndLoginResult register(RegisterRequest registerRequest) {
        UserData userFromReq = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        try{
            if(userDAO.getUser(userFromReq.username()) == null){
                String hashedPassword = BCrypt.hashpw(userFromReq.password(), BCrypt.gensalt());
                userDAO.addUser(new UserData(userFromReq.username(), hashedPassword, userFromReq.email()));
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
        try {
            String username = loginRequest.username();
            String password = loginRequest.password();
            UserData userData = userDAO.getUser(username);
            RegAndLoginResult loginResult;
            if(userData == null){
                return null;
            }
            String hashedPassword = userData.password();
            if (BCrypt.checkpw(password, hashedPassword)) {
                AuthData loginAuth = new AuthData(generateToken(), username);
                authDAO.addAuth(loginAuth);
                loginResult = new RegAndLoginResult(username, loginAuth.authToken());
                return loginResult;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean logout(LogoutRequest logoutRequest) {
        try{
            if(authDAO.getAuth(logoutRequest.authToken()) == null){
                return false;
            }
            else{
                authDAO.deleteAuth(logoutRequest.authToken());
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}