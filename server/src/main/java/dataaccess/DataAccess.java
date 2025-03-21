package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// import exception.DataAccessException;

public interface DataAccess {
    public final Map<String, UserData> users = new HashMap<>();
    public final Map<Integer, GameData> games = new HashMap<>();
    public final Map<String, AuthData> authTokens = new HashMap<>();
    AuthData addAuthData(AuthData authData) throws DataAccessException;
    Collection<AuthData> listAuthData() throws DataAccessException;
    AuthData getAuthData(String username) throws DataAccessException;

    UserData addUserData(UserData userData) throws DataAccessException;
    Collection<UserData> listUserData() throws DataAccessException;

    GameData addGameData(GameData gameData) throws DataAccessException;
    Collection<GameData> listGameData() throws DataAccessException;

    void clearData();
  
}
