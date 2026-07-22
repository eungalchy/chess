package dataaccess;

import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlDataAccessTests {
    private MySqlDataAccess dataAccess;

    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        dataAccess.clear();
    }
    @Test
    public void createUserSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("alice", "hashed", "a@b.com"));
        assertNotNull(dataAccess.getUser("alice"));
    }

    @Test
    public void createUserDuplicate() throws DataAccessException {
        dataAccess.createUser(new UserData("alice", "hashed", "a@b.com"));
        assertThrows(DataAccessException.class,
                () -> dataAccess.createUser(new UserData("alice", "hashed", "a@b.com")));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("alice", "hashed", "a@b.com"));
        assertEquals("a@b.com", dataAccess.getUser("alice").email());
    }

    @Test
    public void getUserNotFound() throws DataAccessException {
        assertNull(dataAccess.getUser("nobody"));
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        dataAccess.createAuth(new AuthData("token123", "alice"));
        assertNotNull(dataAccess.getAuth("token123"));
    }

    @Test
    public void getAuthNotFound() throws DataAccessException {
        assertNull(dataAccess.getAuth("nothing"));
    }

    @Test
    public void createAuthDuplicate() throws DataAccessException {
        dataAccess.createAuth(new AuthData("token123", "alice"));
        assertThrows(DataAccessException.class,
                () -> dataAccess.createAuth(new AuthData("token123", "alice")));
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        dataAccess.createAuth(new AuthData("token123", "alice"));
        assertEquals("alice", dataAccess.getAuth("token123").username());
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        dataAccess.createAuth(new AuthData("token123", "alice"));
        dataAccess.deleteAuth("token123");
        assertNull(dataAccess.getAuth("token123"));
    }

    @Test
    public void deleteAuthNotFound() {
        assertDoesNotThrow(() -> dataAccess.deleteAuth("nothing"));
    }
}
