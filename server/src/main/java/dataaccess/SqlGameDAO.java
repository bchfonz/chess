package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SqlGameDAO implements GameDAO{
    @Override
    public void addGame(GameData newGame) {
        String addGameStatement = "INSERT INTO chessGames (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        Gson gson = new Gson();
        String game = gson.toJson(newGame.game());
        executeUpdate(addGameStatement, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), game);
    }

    @Override
    public GameData getGame(int gameID) {
        String getGameStatement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM chessGames WHERE gameID = ?";
        try(var conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(getGameStatement);
            preparedStatement.setInt(1, gameID);
            try(ResultSet result = preparedStatement.executeQuery()){
                if(result.next()){
                    String dbWhiteUsername = result.getString("whiteUsername");
                    String dbBlackUsername = result.getString("blackUsername");
                    String dbGameName = result.getString("gameName");
                    Gson gson = new Gson();
                    ChessGame dbChessGame = gson.fromJson(result.getString("game"), ChessGame.class);
                    return new GameData(gameID, dbWhiteUsername, dbBlackUsername, dbGameName, dbChessGame);
                }
                else{
                    return null;
                }
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int numOfGames() {
        String sql = "SELECT COUNT(*) AS count FROM chessGames";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet result = preparedStatement.executeQuery()) {

            if (result.next()) {
                return result.getInt("count");
            }
            return 0; // no rows = 0 games

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


//    @Override
//    public HashMap<Integer, GameData> getGamesList() {
//        return null;
//    }
    @Override
    public List<GameData> getGamesList() {
        List<GameData> games = new ArrayList<>();
        String getGamesStatement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM chessGames";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(getGamesStatement);
             ResultSet result = preparedStatement.executeQuery()) {

            while (result.next()) {
                int gameID = result.getInt("gameID");
                String whiteUsername = result.getString("whiteUsername");
                String blackUsername = result.getString("blackUsername");
                String gameName = result.getString("gameName");
                ChessGame chessGame = new Gson().fromJson(result.getString("game"), ChessGame.class);

                GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                games.add(game);
            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error retrieving all games", e);
        }

        return games;
    }

    @Override
    public void clearGameDB() {

    }

    public void updateGame(int gameID, GameData updatedGame){
        String joinGameWhiteStatement = "UPDATE chessGames SET whiteUsername = ? WHERE gameID = ?";
        String joinGameBlackStatement = "UPDATE chessGames SET blackUsername = ? WHERE gameID = ?";
        executeUpdate(joinGameWhiteStatement, updatedGame.whiteUsername(), gameID);
        executeUpdate(joinGameBlackStatement, updatedGame.blackUsername(), gameID);
    }

    @Override
    public boolean emptyDB() {
        return false;
    }

    private void executeUpdate(String statement, Object... params)  {
        try (Connection conn = DatabaseManager.getConnection()) {
//            System.out.println("executeUpdate test 1");
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
//                System.out.println("executeUpdate test 2");
                for (int i = 0; i < params.length; i++) {
//                    System.out.println("executeUpdate test 3");
                    Object param = params[i];
                    if (param instanceof String p){
                        preparedStatement.setString(i + 1, p);
//                        System.out.println("executeUpdate test 4");
                    }
                    else if(param instanceof Integer p){
                        preparedStatement.setInt(i + 1, p);
                    }
                    else if (param == null){
                        preparedStatement.setString(i + 1, null);
//                        System.out.println("executeUpdate test 5");
                    }
                }
                preparedStatement.executeUpdate();

                ResultSet result = preparedStatement.getGeneratedKeys();
                if (result.next()) {
                    System.out.println("Successfully edited chessGame database");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
