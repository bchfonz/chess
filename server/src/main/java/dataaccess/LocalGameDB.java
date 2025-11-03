package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalGameDB implements GameDAO{
    private final HashMap<Integer, GameData> gameDB = new HashMap<>();
    public void addGame(GameData newGame){
        gameDB.put(newGame.gameID(), newGame);
    }

    public GameData getGame(int gameID){
        return gameDB.get(gameID);
    }

    public int numOfGames(){
        return gameDB.size();
    }

    public void updateGame(int gameID, GameData updatedGamed){
        gameDB.remove(gameID);
        gameDB.put(gameID, updatedGamed);
    }

    public List<GameData> getGamesList() {
        return new ArrayList<>(gameDB.values());
    }
    @Override
    public void clearGameDB() {
        gameDB.clear();
    }

    public boolean emptyDB(){
        return gameDB.isEmpty();
    }
}
