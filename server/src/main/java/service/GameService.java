package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.localGameDB;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameService {
    private final localGameDB gameDAO = new localGameDB();

    public int createGame(CreateGameRequest createGameRequest){
        int gameID = gameDAO.numOfGames() + 1;
        ChessGame newChessGame = new ChessGame();
        String gameName = createGameRequest.gameName();
        GameData newGameData = new GameData(gameID, null, null, gameName, newChessGame);
        gameDAO.addGame(newGameData);
        return gameID;
    }

    public List<ListGamesResult> gameList(){
        List<ListGamesResult> listGamesResults = new ArrayList<>();
        HashMap<Integer, GameData> gameList = gameDAO.getGamesList();
        for(Map.Entry<Integer, GameData> game : gameList.entrySet()){
            int curGameID = game.getValue().gameID();
            String curWhiteUsername = game.getValue().whiteUsername();
            String curBlackUsername = game.getValue().blackUsername();
            if(curWhiteUsername == null){
                curWhiteUsername = "null";
            }
            if(curBlackUsername == null){
                curBlackUsername = "null";
            }
            String curGameName = game.getValue().gameName();
            ListGamesResult listGamesResult = new ListGamesResult(curGameID, curWhiteUsername, curBlackUsername, curGameName);
            listGamesResults.add(listGamesResult);
        }
        return listGamesResults;
    }
}
