package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MemoryAuthDAO implements AuthDAO {
    // private String username;
    // private String authToken;
    private final Map<String, AuthData> authTokens = new HashMap<>();
    // public MemoryAuthDAO(String username){
    //     this.username = username;
    //     authToken = generateToken();
    // }
    public AuthData createAuth(String username) throws DataAccessException {
        String token = generateToken();
        AuthData authData = new AuthData(token, username);
        authTokens.put(token, authData);
        return authData;
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    public boolean deleteAuth(String authToken) throws DataAccessException {
        System.out.println("In delete auth");
        if(getAuthData(authToken) == null){
            System.out.println("AuthData didn't exist");
            return false;
        }else{
            System.out.println("Deleting AuthData");
            authTokens.remove(authToken);
            return true;
        }

    }

    public void clearAuth() {
        authTokens.clear();
    }

    private static String generateToken() {
      return UUID.randomUUID().toString();
    }

  
}
