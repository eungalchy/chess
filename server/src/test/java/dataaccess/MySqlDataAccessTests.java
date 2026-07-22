package dataaccess;

import chess.ChessGame;
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

    @Test
    public void createGameSuccess() throws DataAccessException {
        dataAccess.createGame(new GameData(1234, null, null, "test", new chess.ChessGame()));
        assertNotNull(dataAccess.getGame(1234));
    }

    @Test
    public void createGameDuplicate() throws DataAccessException {
        dataAccess.createGame(new GameData(1234, null, null, "test", new chess.ChessGame()));
        assertThrows(DataAccessException.class,
                () -> dataAccess.createGame(new GameData(1234, null, null,  "test", new chess.ChessGame())));
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        dataAccess.createGame(new GameData(1234, null, null, "test", new chess.ChessGame()));
        assertEquals("test", dataAccess.getGame(1234).gameName());
    }

    @Test
    public void getGameNotFound() throws DataAccessException {
        assertNull(dataAccess.getGame(9999));
    }

    @Test
    public void listGameSuccess() throws DataAccessException {
        dataAccess.createGame(new GameData(1234, null, null, "test1", new chess.ChessGame()));
        dataAccess.createGame(new GameData(5678, null, null, "test2", new chess.ChessGame()));
        assertEquals(2, dataAccess.listGames().size());
    }

    @Test
    public void listGamesEmpty() throws DataAccessException {
        assertEquals(0, dataAccess.listGames().size());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        dataAccess.createGame(new GameData(1234, null, null, "test", new chess.ChessGame()));
        dataAccess.updateGame(new GameData(1234, "alice", null, "test", new chess.ChessGame()));
        assertEquals("alice", dataAccess.getGame(1234).whiteUsername());
    }

    @Test
    public void updateGameNotFound() {
        assertDoesNotThrow(()->dataAccess.updateGame(
                new GameData(9999, null, null, "none", new chess.ChessGame())));
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        dataAccess.createUser(new UserData("alice", "hashed", "a@b.com"));
        dataAccess.clear();
        assertNull(dataAccess.getUser("alice"));
    }

}
