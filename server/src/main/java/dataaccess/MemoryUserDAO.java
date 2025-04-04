package dataaccess;

import model.UserData;


import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    //Key = username, Value = UserData (username, password, email)
    private final Map<String, UserData> users = new HashMap<>();
    
    public void createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
    }
    public UserData getUser(String username) throws DataAccessException {
        // Assuming we want to return the first user in the map
        // if (!users.containsKey(username)) {
        //     return null;
        // }
        System.out.println("In get user");
        if(users.containsKey(username)){
            UserData testUser = users.get(username);
            System.out.println("Username from testUser: " + testUser.username());
            System.out.println("Password from testUser: " + testUser.password());
        }
        
        
        return users.get(username);
    }
    public boolean userExists (String username){
        if(users.containsKey(username)){
            return true;
        }else{
            return false;
        }
    }
    public void clearUsers() {
        users.clear();
    }
}
