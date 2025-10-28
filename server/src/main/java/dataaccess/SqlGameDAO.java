package dataaccess;

import model.GameData;

import java.util.HashMap;

public class SqlGameDAO implements GameDAO{
    @Override
    public void addGame(GameData newGame) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public int numOfGames() {
        return 0;
    }

    @Override
    public HashMap<Integer, GameData> getGamesList() {
        return null;
    }

    @Override
    public void clearGameDB() {

    }

    @Override
    public boolean emptyDB() {
        return false;
    }
}
