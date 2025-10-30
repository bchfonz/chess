package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class LocalUserDB implements UserDAO{
    private final HashMap<String, UserData> userDB = new HashMap<>();
    public UserData getUser(String username) throws DataAccessException {
        return userDB.get(username);
    }

    public void addUser(UserData newUser) {
        userDB.put(newUser.username(), newUser);
    }

    public void clearUserDB() {
        userDB.clear();
    }

    public boolean emptyDB(){
        return userDB.isEmpty();
    }

    public int numUsers(){
        return userDB.size();
    }
}
