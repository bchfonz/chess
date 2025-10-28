package dataaccess;

import model.AuthData;

public class SqlAuthDAO implements AuthDAO{
    @Override
    public void addAuth(AuthData newAuth) {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clearAuthDB() throws DataAccessException {

    }

    @Override
    public boolean emptyDB() {
        return false;
    }

    @Override
    public int numAuth() {
        return 0;
    }
}
