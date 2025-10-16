package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class localAuthDB implements AuthDAO{
    private final HashMap<String, AuthData> authDB = new HashMap<>();
    public void addAuth(AuthData newAuth) {
        authDB.put(newAuth.authToken(), newAuth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        return authDB.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        authDB.remove(authToken);
    }

    @Override
    public void clearAuthDB() throws DataAccessException{
        authDB.clear();
    }

    public boolean emptyDB(){
        return authDB.isEmpty();
    }

    public int numAuth(){
        return authDB.size();
    }
}
