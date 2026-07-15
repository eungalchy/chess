package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private DataAccess dataAccess;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
    }

    @Test
    public void registerSuccess() throws DataAccessException {
        UserService service = new UserService(dataAccess);
        RegisterResult result = service.register(new RegisterRequest("alice", "pass", "a@b.com"));
        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerDuplicate() throws DataAccessException {
        UserService service = new UserService(dataAccess);
        service.register(new RegisterRequest("alice", "pass", "a@b.com"));
        assertThrows(DataAccessException.class, () -> service.register(new RegisterRequest("alice", "pass", "a@b.com")));
    }

    @Test
    public void loginSuccess() throws DataAccessException {
        UserService service = new UserService(dataAccess);
        service.register(new RegisterRequest("alice", "pass", "a@b.com"));
        LoginResult result = service.login(new LoginRequest("alice", "pass"));
        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginWrongPassword() throws DataAccessException {
        UserService service = new UserService(dataAccess);
        service.register(new RegisterRequest("alice", "pass", "a@b.com"));
        assertThrows(DataAccessException.class,
                () -> service.login(new LoginRequest("alice", "wrong")));
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        UserService service = new UserService(dataAccess);
        RegisterResult reg = service.register(new RegisterRequest("alice", "pass", "a@b.com"));
        service.logout(reg.authToken());
        assertThrows(DataAccessException.class, () -> service.logout(reg.authToken()));
    }

    @Test
    public void logoutBadToken() {
        UserService service = new UserService(dataAccess);
        assertThrows(DataAccessException.class, () -> service.logout("badtoken"));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        UserService userService = new UserService(dataAccess);
        RegisterResult reg = userService.register(new RegisterRequest("alice", "pass", "a@b.com"));
        GameService gameService = new GameService(dataAccess);
        CreateGameResult result = gameService.createGame(reg.authToken(), new CreateGameRequest("mygame"));
        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    public void createGameBadAuth() {
        GameService gameService = new GameService(dataAccess);
        assertThrows(DataAccessException.class,
                () -> gameService.createGame("badtoken", new CreateGameRequest("mygame")));
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        UserService userService = new UserService(dataAccess);
        RegisterResult reg = userService.register(new RegisterRequest("alice", "pass", "a@b.com"));
        GameService gameService = new GameService(dataAccess);
        gameService.createGame(reg.authToken(), new CreateGameRequest("mygame"));
        ListGamesResult result = gameService.listGamesResult(reg.authToken());
        assertEquals(1, result.games().size());
    }

    @Test
    public void listGamesBadAuth() {
        GameService gameService = new GameService(dataAccess);
        assertThrows(DataAccessException.class,
                () -> gameService.listGamesResult("badtoken"));
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        UserService userService = new UserService(dataAccess);
        RegisterResult reg = userService.register(new RegisterRequest("alice", "pass", "a@b.com"));
        GameService gameService = new GameService(dataAccess);
        CreateGameResult game = gameService.createGame(reg.authToken(), new CreateGameRequest("mygame"));
        gameService.joinGame(reg.authToken(), new JoinGameRequest("WHITE", game.gameID()));
        assertEquals(1, gameService.listGamesResult(reg.authToken()).games().size());
    }

    @Test
    public void joinGameColorTaken() throws DataAccessException {
        UserService userService = new UserService(dataAccess);
        RegisterResult reg = userService.register(new RegisterRequest("alice", "pass", "a@b.com"));
        GameService gameService = new GameService(dataAccess);
        CreateGameResult game = gameService.createGame(reg.authToken(), new CreateGameRequest("mygame"));
        gameService.joinGame(reg.authToken(), new JoinGameRequest("WHITE", game.gameID()));
        assertThrows(DataAccessException.class,
                () -> gameService.joinGame(reg.authToken(), new JoinGameRequest("WHITE", game.gameID())));
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        UserService userService = new UserService(dataAccess);
        userService.register(new RegisterRequest("alice", "pass", "a@b.com"));
        ClearService clearService = new ClearService(dataAccess);
        clearService.clear();
        assertThrows(DataAccessException.class,
                () -> userService.login(new LoginRequest("alice", "pass")));
    }
}