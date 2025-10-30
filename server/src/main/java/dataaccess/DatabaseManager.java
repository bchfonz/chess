package dataaccess;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;


    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
        try{
            System.out.println("Creating database (if not exists)...");
            DatabaseManager.createDatabase();
            System.out.println("Database ready.");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DatabaseManager(){
        System.out.println("Setting up tables...");
        dataTableSetup();
    }


    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    private final String[] setupTablesStatements = {
        """
        CREATE TABLE IF NOT EXISTS users(
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(50) NOT NULL UNIQUE,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(100) NOT NULL UNIQUE
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS auth(
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(50) NOT NULL,
            authToken VARCHAR(64) NOT NULL UNIQUE
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS chessGames(
            gameID INT AUTO_INCREMENT PRIMARY KEY,
            whiteUsername VARCHAR(50) NOT NULL UNIQUE,
            blackUsername VARCHAR(50) NOT NULL UNIQUE,
            gameName VARCHAR(50) NOT NULL,
            game JSON NOT NULL
        )
        """
    };

    public void dataTableSetup() {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : setupTablesStatements) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            System.out.println("User tables created successfully.");
        } catch (SQLException | DataAccessException ex) {
            System.err.println("Error setting up user tables: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

//    private void UserTableSetup(){
//        try (Connection conn = DatabaseManager.getConnection()) {
//            for (String statement : setupTablesStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException ex) {
//
//        }
//    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public void example() throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1+1")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        }
    }


    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
