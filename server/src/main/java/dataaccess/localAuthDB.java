package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class localAuthDB implements AuthDAO{
    private final HashMap<String, AuthData> authDB = new HashMap<>();
    public void addAuth(AuthData newAuth) {
        authDB.put(newAuth.authToken(), newAuth);
    }

    public AuthData getAuth(String authToken) {
        return authDB.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authDB.remove(authToken);
    }

    @Override
    public void clearAuthDB() {
        authDB.clear();
    }

    public boolean emptyDB(){
        return authDB.isEmpty();
    }
}
