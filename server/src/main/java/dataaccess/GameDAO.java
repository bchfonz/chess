package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.List;

public interface GameDAO {
    public void addGame(GameData newGame);
    public GameData getGame(int gameID);
    public int numOfGames();
    public List<GameData> getGamesList();
    public void clearGameDB();
    public boolean emptyDB();
}
