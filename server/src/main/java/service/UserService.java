package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.Objects;
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
        try {
            System.out.println("Number of users when trying to login: "+ userDAO.numUsers());
            System.out.println("Number of auth when trying to login: "+ authDAO.numAuth());
            String username = loginRequest.username();
            String password = loginRequest.password();
            UserData userData = userDAO.getUser(username);
            RegAndLoginResult loginResult;
            if(userData == null){
                System.out.println("User not found");
                return null;
            }
            else if (Objects.equals(userData.password(), password)) {
                AuthData loginAuth = new AuthData(generateToken(), username);
                authDAO.addAuth(loginAuth);
                loginResult = new RegAndLoginResult(username, loginAuth.authToken());
                return loginResult;
            } else {
                System.out.println("Passwords didn't match");
                return null;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean logout(LogoutRequest logoutRequest) {
        try{
            if(authDAO.getAuth(logoutRequest.authToken()) == null){
                System.out.println("AuthToken didn't exist");
                return false;
            }
            else{
                System.out.println("Authoken was found. Deleting now");
                authDAO.deleteAuth(logoutRequest.authToken());
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}