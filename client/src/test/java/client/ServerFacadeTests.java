package client;

import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clear();
    }

    @Test
    public void registerPositive() throws Exception {
        var result = facade.register("player1", "password", "p1@mail.com");
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void registerNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> {
            facade.register("player1", "password", "p1@email.com");
        });
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var result = facade.login("player1", "password");
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void loginNegative() throws Exception {
        assertThrows(Exception.class, () -> {
            facade.login("nonexistent", "wrongpassword");
        });
    }

    @Test
    public void logoutPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    public void logoutNegative() throws Exception {
        assertThrows(Exception.class, () -> {
            facade.logout("invalid-token");
        });
    }

    @Test
    public void createGamePositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var result = facade.createGame(authData.authToken(), "myGame");
        assertTrue(result.gameID() > 0);
    }

    @Test
    public void createGameNegative() throws Exception {
        assertThrows(Exception.class, () -> {
            facade.createGame("invalid-token", "myGame");
        });
    }

    @Test
    public void listGamesPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame(authData.authToken(), "myGame");
        var result = facade.listGames(authData.authToken());
        assertEquals(1, result.games().size());
    }

    @Test
    public void listGamesNegative() throws Exception {
        assertThrows(Exception.class, () -> {
            facade.listGames("invalid-token");
        });
    }

    @Test
    public void joinGamePositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var gameResult = facade.createGame(authData.authToken(), "myGame");
        assertDoesNotThrow(() -> facade.joinGame(authData.authToken(), "WHITE", gameResult.gameID()));
    }

    @Test
    public void joinGameNegative() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> {
            facade.joinGame(authData.authToken(), "WHITE", 9999);
        });
    }



}
