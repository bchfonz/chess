package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {

    private static SqlGameDAO sqlGameDaoObject;

    @BeforeAll
    static void setupDatabase() throws Exception {
        try {
            new DatabaseManager();
            System.out.println("Database and tables successfully set up!");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        sqlGameDaoObject = new SqlGameDAO();
    }

    @BeforeEach
    void clearBeforeEach() throws DataAccessException {
        sqlGameDaoObject.clearGameDB();
    }

    @Test
    void successfulAddGame() {
        ChessGame chessGame = new ChessGame();
        GameData gameData1 = new GameData(1, "whiteUsername", "blackUsername", "FunGame1", chessGame);
        sqlGameDaoObject.addGame(gameData1);
        assertEquals(1, sqlGameDaoObject.numOfGames());
    }

    @Test
    void invalidAddGame() {
        boolean exceptionThrown = false;
        try {
            GameData badGame = new GameData(2, null, null, null, null);
            sqlGameDaoObject.addGame(badGame);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            exceptionThrown = true;
            System.out.println("Caught expected RuntimeException: " + e.getMessage());
        }
        assertTrue(exceptionThrown);
    }

    @Test
    void successfulTestGetGame() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "gaaammeee", chessGame);
        sqlGameDaoObject.addGame(gameData);

        GameData retrievedGame = sqlGameDaoObject.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals("gaaammeee", retrievedGame.gameName());
    }

    @Test
    void invalidTestGetGame() {
        GameData retrievedGame = sqlGameDaoObject.getGame(9999);
        assertNull(retrievedGame);
    }

    @Test
    void successfulTestNumOfGames() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "nameGame", chessGame);
        GameData gameData2 = new GameData(2, "whiteUser42", "blackUser42", "gameName", chessGame);
        sqlGameDaoObject.addGame(gameData);
        sqlGameDaoObject.addGame(gameData2);
        assertEquals(2, sqlGameDaoObject.numOfGames());
    }

    @Test
    void invalidTestNumOfGames() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "goime", chessGame);
        sqlGameDaoObject.addGame(gameData);
        assertNotEquals(2, sqlGameDaoObject.numOfGames());
    }

    @Test
    void successfulTestGetGamesList() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "Game1", chessGame);
        GameData gameData2 = new GameData(2, "whiteUserF", "blackUserF", "Game2", chessGame);
        sqlGameDaoObject.addGame(gameData);
        sqlGameDaoObject.addGame(gameData2);

        List<GameData> gamesList = sqlGameDaoObject.getGamesList();
        assertEquals(2, gamesList.size());
    }

    @Test
    void invalidTestGetGamesList() {
        List<GameData> gamesList = sqlGameDaoObject.getGamesList();
        assertNotEquals(5, gamesList.size());
    }

    @Test
    void successfulTestClearGameDB() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "chip", chessGame);
        GameData gameData2 = new GameData(2, "whiteUsher", "blackUsher", "dip", chessGame);
        sqlGameDaoObject.addGame(gameData);
        sqlGameDaoObject.addGame(gameData2);
        sqlGameDaoObject.clearGameDB();
        assertEquals(0, sqlGameDaoObject.numOfGames());
    }

    @Test
    void invalidTestClearGameDB() {
        assertNotEquals(10, sqlGameDaoObject.numOfGames()); // intentional fail
    }

    @Test
    void successfulTestUpdateGame() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, null, null, "NewGame", chessGame);
        sqlGameDaoObject.addGame(gameData);
        GameData updatedGameData = new GameData(1, "whitePlayer", "blackPlayer", "NewGame", chessGame);
        sqlGameDaoObject.updateGame(1, updatedGameData);
        GameData getGameData = sqlGameDaoObject.getGame(1);
        assertEquals("whitePlayer", getGameData.whiteUsername());
    }

    @Test
    void invalidTestUpdateGame() {
        boolean exceptionThrown = false;
        try {
            ChessGame chessGame = new ChessGame();
            GameData updatedGameData = new GameData(999, "white", "black", "NoGameHere", chessGame);
            sqlGameDaoObject.updateGame(999, updatedGameData);
        } catch (RuntimeException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
        assertNull(sqlGameDaoObject.getGame(999));
    }

    @Test
    void successfulTestEmptyDB() {
        sqlGameDaoObject.clearGameDB();
        assertTrue(sqlGameDaoObject.emptyDB());
    }

    @Test
    void invalidTestEmptyDB() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, "whiteUser", "blackUser", "spookySzn", chessGame);
        sqlGameDaoObject.addGame(gameData);
        assertFalse(sqlGameDaoObject.emptyDB()); // passes, but inverted to fail intentionally
    }
}
