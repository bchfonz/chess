package dataaccess;

import model.GameData;

public interface GameDAO {
    public void addGame(GameData newGame);
    public GameData getGame(int gameID);
    public int numOfGames();
    public void clearGameDB();
    public boolean emptyDB();
}
