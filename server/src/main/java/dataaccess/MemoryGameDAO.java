package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    public void createGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }
    public void updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }
    public void deleteGame(int gameID) throws DataAccessException {
        games.remove(gameID);
    }
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }
    public void clearGames() {
        // No game data to clear in memory implementation
        games.clear();
    }
    
}
