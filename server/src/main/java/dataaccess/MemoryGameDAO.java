package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
        return gameData;
    }
    @Override
    public GameData updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
        return gameData;
    }
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }
    @Override
    public GameData deleteGame(int gameID) throws DataAccessException {
        return games.remove(gameID);
    }
    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }
    @Override
    public void clearData() {
        // No game data to clear in memory implementation
    }
    
}
