package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private static SqlUserDAO sqlUserDaoObject;

    @BeforeAll
    static void setupDatabase() throws Exception {
        try {
            new DatabaseManager();
            System.out.println("Database and tables successfully set up!");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        sqlUserDaoObject = new SqlUserDAO();
    }

    @BeforeEach
    void clearBeforeEach() {
        sqlUserDaoObject.clearUserDB();
    }

    @Test
    void successfulAddUser() {
        UserData newUser = new UserData("username", "password", "swaggy@byu.edu");
        sqlUserDaoObject.addUser(newUser);
        assertEquals(1, sqlUserDaoObject.numUsers());
    }

    @Test
    void invalidAddUser() {
        assertThrows(RuntimeException.class, () ->
                sqlUserDaoObject.addUser(new UserData(null, null, null)));
    }

    @Test
    void successfulGetUser() throws DataAccessException {
        UserData newUser = new UserData("username", "password", "swaggy@byu.edu");
        sqlUserDaoObject.addUser(newUser);
        UserData userData = sqlUserDaoObject.getUser("username");
        assertNotNull(userData);
        assertEquals("username", userData.username());
    }

    @Test
    void invalidGetUser() throws DataAccessException {
        UserData retrieved = sqlUserDaoObject.getUser("invalidUser");
        assertNull(retrieved);
    }

    @Test
    void successfulClearUserDB() {
        sqlUserDaoObject.addUser(new UserData("username", "password", "swaggy@byu.edu"));
        sqlUserDaoObject.addUser(new UserData("username2", "password2", "swaggy@byu.edu"));
        sqlUserDaoObject.clearUserDB();
        assertEquals(0, sqlUserDaoObject.numUsers());
    }

    @Test
    void invalidClearUserDB() {
        assertNotEquals(5, sqlUserDaoObject.numUsers());
    }

    @Test
    void successfulEmptyDB() {
        sqlUserDaoObject.clearUserDB();
        assertTrue(sqlUserDaoObject.emptyDB());
    }

    @Test
    void invalidEmptyDB() {
        sqlUserDaoObject.addUser(new UserData("username", "password", "swaggy@byu.edu"));
        assertFalse(sqlUserDaoObject.emptyDB());
    }

    @Test
    void successfulNumUsers() {
        sqlUserDaoObject.addUser(new UserData("username", "password", "swaggy@byu.edu"));
        sqlUserDaoObject.addUser(new UserData("username2", "password2", "swaggy@byu.edu"));
        assertEquals(2, sqlUserDaoObject.numUsers());
    }

    @Test
    void invalidNumUsers() {
        sqlUserDaoObject.addUser(new UserData("username", "password", "swagilicious@deeznuts.com"));
        assertNotEquals(2, sqlUserDaoObject.numUsers());
    }
}
