package dataaccess;

import model.GameData;

import java.util.HashMap;

public class localGameDB implements GameDAO{
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

    public HashMap<Integer, GameData> getGamesList(){
        return gameDB;
    }
    @Override
    public void clearGameDB() {
        gameDB.clear();
    }

    public boolean emptyDB(){
        return gameDB.isEmpty();
    }
}
