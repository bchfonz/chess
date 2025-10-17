package dataaccess;

import model.GameData;

import java.util.HashMap;

public interface GameDAO {
    public void addGame(GameData newGame);
    public GameData getGame(int gameID);
    public int numOfGames();
    public HashMap<Integer, GameData> getGamesList();
    public void clearGameDB();
    public boolean emptyDB();
}
