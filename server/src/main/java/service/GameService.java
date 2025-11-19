package service;

import chess.ChessGame;
import dataaccess.LocalGameDB;
import dataaccess.SqlGameDAO;
import model.GameData;

import java.util.*;

public class GameService {
    //If I move the local db to the service classes I might be able to keep persistence for local db
    //I'd probably have to pass in the db as an argument in all of these functions though. Or maybe
    //put in a constructor...
    public final SqlGameDAO gameDAO = new SqlGameDAO();

    public int createGame(CreateGameRequest createGameRequest){
        if(createGameRequest.gameName() == null){
            return 0;
        }
        ChessGame newChessGame = new ChessGame();
        String gameName = createGameRequest.gameName();
        GameData newGameData = new GameData(0, null, null, gameName, newChessGame);
        gameDAO.addGame(newGameData);
        return gameDAO.numOfGames();
    }

    public List<ListGamesResult> gameList(){
        List<ListGamesResult> listGamesResults = new ArrayList<>();
        List<GameData> gameList = gameDAO.getGamesList();
        for(GameData game : gameList){
            int curGameID = game.gameID();
            String curWhiteUsername = game.whiteUsername();
            String curBlackUsername = game.blackUsername();
            String curGameName = game.gameName();
            ListGamesResult listGamesResult = new ListGamesResult(curGameID, curWhiteUsername, curBlackUsername, curGameName);
            listGamesResults.add(listGamesResult);
        }
        return listGamesResults;
    }

    public GameData joinGame(String username, String playerColor, int gameID){
        GameData curGame = gameDAO.getGame(gameID);
        if(curGame == null){
            return null;
        }
        if(Objects.equals(playerColor, "WHITE")){
            if(curGame.whiteUsername() != null){
                if(Objects.equals(username, curGame.whiteUsername())){
                    return curGame;
                }
                else{
                    return null;
                }
            }
            else{
                GameData updatedGame = new GameData(curGame.gameID(), username, curGame.blackUsername(), curGame.gameName(), curGame.game());
                gameDAO.updateGame(gameID, updatedGame);
                return updatedGame;
            }
        }
        else if(Objects.equals(playerColor, "BLACK")){
            if(curGame.blackUsername() != null){
                if(Objects.equals(username, curGame.blackUsername())){
                    return curGame;
                }
            }
            else{
                GameData updatedGame = new GameData(curGame.gameID(), curGame.whiteUsername(), username, curGame.gameName(), curGame.game());
                gameDAO.updateGame(gameID, updatedGame);
                return updatedGame;
            }
        }
        else{
            return null;
        }
        return null;
    }
}
