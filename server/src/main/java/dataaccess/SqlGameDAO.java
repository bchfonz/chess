package dataaccess;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

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

    private void executeUpdate(String statement, Object... params)  {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) preparedStatement.setString(i + 1, p);
                    else if (param == null) preparedStatement.setString(i + 1, null);
                }
                preparedStatement.executeUpdate();

                ResultSet result = preparedStatement.getGeneratedKeys();
                if (result.next()) {
                    System.out.println("Successfully added user to database");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
