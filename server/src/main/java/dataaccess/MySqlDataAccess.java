package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.List;

public class MySqlDataAccess implements DataAccess{
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(256) NOT NULL,
                password VARCHAR(256) NOT NULL,
                email VARCHAR(256) NOT NULL,
                PRIMARY KEY (username)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(256) NOT NULL,
                username VARCHAR(256) NOT NULL,
                PRIMARY KEY (authToken)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(256),
                blackUsername VARCHAR(256),
                gameName VARCHAR(256) NOT NULL,
                game TEXT NOT NULL,
                PRIMARY KEY (gameID)
            )
            """
    };

    public MySqlDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to configure database", ex);
        }
    }



    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            for (var table : new String[]{"auth", "game", "user"}) {
                try (var ps = conn.prepareStatement("DELETE  FROM " + table)) {
                    ps.executeUpdate();
                }
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to clear database", ex);
        }

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(user.password(), org.mindrot.jbcrypt.BCrypt.gensalt());
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")) {
                ps.setString(1, user.username());
                ps.setString(2, hashed);
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
