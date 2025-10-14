package dataaccess;

import model.UserData;

import java.util.HashMap;

public class localUserDB implements UserDAO{
    private final HashMap<String, UserData> userDB = new HashMap<>();
    public UserData getUser(String username) throws DataAccessException {
        return userDB.get(username);
    }

    public void addUser(UserData newUser) {
        userDB.put(newUser.username(), newUser);
    }

    @Override
    public void deleteUser(String username) {
        userDB.remove(username);
    }

    @Override
    public void clearUserDB() {
        userDB.clear();
    }

    public boolean emptyDB(){
        return userDB.isEmpty();
    }
}
