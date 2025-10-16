package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void addAuth (AuthData newAuth);
    public AuthData getAuth(String authToken) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;
    public void clearAuthDB() throws DataAccessException;
    public boolean emptyDB();
    public int numAuth();
}
