package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {
    public UserData getUser(String username) throws DataAccessException;
    public void addUser (UserData newUser);
    public void deleteUser(String username);
    public void clearUserDB();
    public boolean emptyDB();
    public int numUsers();
}
