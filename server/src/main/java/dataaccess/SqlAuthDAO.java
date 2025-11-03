package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SqlAuthDAO implements AuthDAO{
    @Override
    public void addAuth(AuthData newAuth) {
        String addAuthStmt = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        executeUpdate(addAuthStmt, newAuth.username(), newAuth.authToken());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String getAuthStatement = "SELECT id, authToken, username FROM auth WHERE authToken = ?";
        try(var conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(getAuthStatement);
            preparedStatement.setString(1, authToken);
            try(ResultSet result = preparedStatement.executeQuery()){
                if(result.next()){
                    String dbUsername = result.getString("username");
                    String dbAuthToken = result.getString("authToken");
                    return new AuthData(dbAuthToken, dbUsername);
                }
                else{
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String deleteAuthStatement = "DELETE FROM auth WHERE authToken = ?";
        try(Connection conn = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(deleteAuthStatement);
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new DataAccessException("Unable to delete auth: " + e.getMessage());
        }
    }

    @Override
    public void clearAuthDB() throws DataAccessException {
        String clearStatement = "TRUNCATE TABLE auth";
//        executeUpdate(clearStatement, (Object) null);
        SqlGameDAO.updateNoParams(clearStatement);
    }

    @Override
    public boolean emptyDB() {
        return numAuth() == 0;
    }

    @Override
    public int numAuth() {
        String sql = "SELECT COUNT(*) AS count FROM auth";
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

    private void executeUpdate(String statement, Object... params)  {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                if((params == null) || (params.length == 0)) {
                    preparedStatement.executeUpdate();
                    return;
                }
                System.out.println(Arrays.toString(params));
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    preparedStatement.setString(i + 1, param.toString());
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
