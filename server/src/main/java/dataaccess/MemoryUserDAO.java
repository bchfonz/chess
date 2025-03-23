package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();
    public UserData createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
        return userData;
    }
    public UserData getUser() throws DataAccessException {
        // Assuming we want to return the first user in the map
        if (users.isEmpty()) {
            return null;
        }
        return users.values().iterator().next();
    }
    public void clearData() {
        users.clear();
    }
}
