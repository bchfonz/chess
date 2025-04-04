package dataaccess;

import model.AuthData;

// import java.util.Collection;
// import java.util.HashMap;
// import java.util.Map;

public interface AuthDAO {
    public AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuthData(String username) throws DataAccessException;
    boolean deleteAuth(String username) throws DataAccessException;
    void clearAuth();
}
