package dataaccess;

import model.GameData;

import java.util.HashMap;

public class localGameDB implements GameDAO{
    private final HashMap<String, GameData> gameDB = new HashMap<>();
    @Override
    public void clearGameDB() {
        gameDB.clear();
    }

    public boolean emptyDB(){
        return gameDB.isEmpty();
    }
}
