package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface GameDAO {
  public final Map<Integer, GameData> games = new HashMap<>();
  GameData createGame(GameData gameData) throws DataAccessException;
  GameData updateGame (GameData gameData) throws DataAccessException;
  GameData getGame(int gameID) throws DataAccessException;
  GameData deleteGame(int gameID) throws DataAccessException;
  Collection<GameData> listGames() throws DataAccessException;
  void clearData();

}
