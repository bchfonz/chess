package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
        return userData;
    }
    @Override
    public UserData getUser() throws DataAccessException {
        // Assuming we want to return the first user in the map
        if (users.isEmpty()) {
            return null;
        }
        return users.values().iterator().next();
    }
    @Override
    public void clearData() {
        users.clear();
    }
}
