package dataaccess;

import model.UserData;

public interface UserDAO {
  void createUser(UserData userData) throws DataAccessException;
  UserData getUser(String username) throws DataAccessException;
  void clearUsers();
}
