package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SqlUserDAO implements UserDAO{
    @Override
    public UserData getUser(String username) throws DataAccessException {
        String getUserStatement = "SELECT id, username, password, email FROM users WHERE username = ?";
        try(var conn = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(getUserStatement);
            preparedStatement.setString(1, username);
            try(ResultSet result = preparedStatement.executeQuery()){
                if(result.next()){
                    String dbUsername = result.getString("username");
                    String password = result.getString("password");
                    String email = result.getString("email");
                    return new UserData(dbUsername, password, email);
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
    public void addUser(UserData newUser) {
        String addUserStmt = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        int id = executeUpdate(addUserStmt, newUser.username(), newUser.password(), newUser.email());
        if(id == 0){
            System.out.println("User not added");
        }
    }

    @Override
    public void clearUserDB() {

    }

    @Override
    public boolean emptyDB() {
        return false;
    }

    @Override
    public int numUsers() {
        return 0;
    }

    private int executeUpdate(String statement, Object... params)  {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    preparedStatement.setString(i + 1, param.toString());
                }
                preparedStatement.executeUpdate();

                ResultSet result = preparedStatement.getGeneratedKeys();
                if (result.next()) {
                    System.out.println("Successfully added user to database");
                    return result.getInt(1);
                }
                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
