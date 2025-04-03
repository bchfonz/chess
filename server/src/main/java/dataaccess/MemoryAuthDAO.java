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
        authTokens.put(username, authData);
        return authData;
    }

    public AuthData getAuthData(String username) throws DataAccessException {
        return authTokens.get(username);
    }

    public AuthData deleteAuth(String username) throws DataAccessException {
        return authTokens.remove(username);
    }

    public void clearAuth() {
        authTokens.clear();
    }

    private static String generateToken() {
      return UUID.randomUUID().toString();
    }

  
}
