package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    public GameData createGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
        return gameData;
    }
    public GameData updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
        return gameData;
    }
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }
    public GameData deleteGame(int gameID) throws DataAccessException {
        return games.remove(gameID);
    }
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }
    public void clearData() {
        // No game data to clear in memory implementation
    }
    
}
