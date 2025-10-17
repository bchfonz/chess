package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.localGameDB;
import model.GameData;

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
}
