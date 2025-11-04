package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {

    private static SqlAuthDAO sqlAuthDaoObject;

    @BeforeAll
    static void setupDatabase() throws Exception {
        try {
            new DatabaseManager();
            System.out.println("Database and tables successfully set up!");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        sqlAuthDaoObject = new SqlAuthDAO();
    }

    @BeforeEach
    void clearBeforeEach() throws DataAccessException {
        sqlAuthDaoObject.clearAuthDB();
    }

    @Test
    void successfulAddAuth() {
        AuthData auth = new AuthData("authToken", "username");
        sqlAuthDaoObject.addAuth(auth);
        assertEquals(1, sqlAuthDaoObject.numAuth());
    }

    @Test
    void invalidAddAuth() {
        boolean exceptionThrown = false;
        try {
            sqlAuthDaoObject.addAuth(new AuthData(null, null));
        } catch (RuntimeException e) {
            exceptionThrown = true;  // We expected this
        }
        assertTrue(exceptionThrown);
    }


    @Test
    void successfulTestGetAuth() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        sqlAuthDaoObject.addAuth(auth);
        AuthData authData = sqlAuthDaoObject.getAuth("authToken");
        assertNotNull(authData);
        assertEquals("username", authData.username());
    }

    @Test
    void invalidTestGetAuth() throws DataAccessException {
        AuthData authData = sqlAuthDaoObject.getAuth("invalidAuth");
        assertNull(authData);
    }

    @Test
    void successfulTestDeleteAuth() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        sqlAuthDaoObject.addAuth(auth);
        sqlAuthDaoObject.deleteAuth("authToken");
        assertEquals(0, sqlAuthDaoObject.numAuth());
    }

    @Test
    void invalidTestDeleteAuth() throws DataAccessException {
        int before = sqlAuthDaoObject.numAuth();
        sqlAuthDaoObject.deleteAuth("invalidAuth");
        int after = sqlAuthDaoObject.numAuth();
        assertEquals(before, after);
    }

    @Test
    void successfulTestClearAuthDB() throws DataAccessException {
        sqlAuthDaoObject.addAuth(new AuthData("authToken", "username"));
        sqlAuthDaoObject.addAuth(new AuthData("authToken2", "username2"));
        sqlAuthDaoObject.clearAuthDB();
        assertEquals(0, sqlAuthDaoObject.numAuth());
    }

    @Test
    void invalidTestClearAuthDB() {
        assertNotEquals(5, sqlAuthDaoObject.numAuth());
    }

    @Test
    void successfulTestEmptyDB() throws DataAccessException {
        sqlAuthDaoObject.clearAuthDB();
        assertTrue(sqlAuthDaoObject.emptyDB());
    }

    @Test
    void invalidTestEmptyDB() {
        sqlAuthDaoObject.addAuth(new AuthData("authToken", "username"));
        assertFalse(sqlAuthDaoObject.emptyDB());
    }

    @Test
    void successfulTestNumAuth() {
        sqlAuthDaoObject.addAuth(new AuthData("authToken", "username"));
        sqlAuthDaoObject.addAuth(new AuthData("authToken2", "username2"));
        assertEquals(2, sqlAuthDaoObject.numAuth());
    }

    @Test
    void invalidTestNumAuth() {
        sqlAuthDaoObject.addAuth(new AuthData("authToken", "username"));
        assertNotEquals(2, sqlAuthDaoObject.numAuth());
    }
}
