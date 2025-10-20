package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.localGameDB;
import model.GameData;

import java.util.*;

public class GameService {
    //If I move the local db to the service classes I might be able to keep persistance for local db
    //I'd probably have to pass in the db as an argument in all of these functions though. Or maybe
    //put in a constructor...
    public final localGameDB gameDAO = new localGameDB();

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
            String curGameName = game.getValue().gameName();
            ListGamesResult listGamesResult = new ListGamesResult(curGameID, curWhiteUsername, curBlackUsername, curGameName);
            listGamesResults.add(listGamesResult);
        }
        return listGamesResults;
    }

    public boolean joinGame(String username, String playerColor, int gameID){
        GameData curGame = gameDAO.getGame(gameID);
        if(curGame == null){
            return false;
        }
        if(Objects.equals(playerColor, "WHITE")){
            if(curGame.whiteUsername() != null){
                return false;
            }
            else{
                GameData updatedGame = new GameData(curGame.gameID(), username, curGame.blackUsername(), curGame.gameName(), curGame.game());
                gameDAO.updateGame(gameID, updatedGame);
                return true;
            }
        }
        else if(Objects.equals(playerColor, "BLACK")){
            if(curGame.blackUsername() != null){
                return false;
            }
            else{
                GameData updatedGame = new GameData(curGame.gameID(), curGame.whiteUsername(), username, curGame.gameName(), curGame.game());
                gameDAO.updateGame(gameID, updatedGame);
                return true;
            }
        }
        else{
            return false;
        }
    }
}
