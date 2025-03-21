package dataaccess;

import model.AuthData;

// import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface AuthDAO {
    public final Map<String, AuthData> authTokens = new HashMap<>();
    public AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuthData(String username) throws DataAccessException;
    AuthData deleteAuth(String username) throws DataAccessException;
    void clearData();
}
