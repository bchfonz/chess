package dataaccess;

import model.AuthData;

import java.util.UUID;


public class MemoryAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String token = generateToken();
        AuthData authData = new AuthData(token, username);
        return authData;
    }

    @Override
    public AuthData getAuthData(String username) throws DataAccessException {
        return authTokens.get(username);
    }

    @Override
    public AuthData deleteAuth(String username) throws DataAccessException {
        return authTokens.remove(username);
    }

    @Override
    public void clearData() {
        authTokens.clear();
    }

    private static String generateToken() {
      return UUID.randomUUID().toString();
    }

  
}
