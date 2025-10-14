package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void addAuth (AuthData newAuth);
    public AuthData getAuth(String authToken);
    public void deleteAuth(String authToken);
    public void clearAuthDB();
    public boolean emptyDB();
}
