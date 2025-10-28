package dataaccess;

import model.UserData;

public class SqlUserDAO implements UserDAO{
    private final DatabaseManager dbManager = new DatabaseManager();

    public void example() throws Exception {
        try (var conn = dbManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1+1")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        }
    }
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void addUser(UserData newUser) {

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
}
