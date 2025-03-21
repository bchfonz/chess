package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface UserDAO {
  public final Map<String, UserData> users = new HashMap<>();
  UserData createUser(UserData userData) throws DataAccessException;
  UserData getUser() throws DataAccessException;
  void clearData();
}
