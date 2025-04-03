package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
  
  void createGame(GameData gameData) throws DataAccessException;
  void updateGame (GameData gameData) throws DataAccessException;
  GameData getGame(int gameID) throws DataAccessException;
  void deleteGame(int gameID) throws DataAccessException;
  Collection<GameData> listGames() throws DataAccessException;
  void clearGames();

}
