package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.List;

public class MySqlDataAccess implements DataAccess {
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
                gameID INT NOT NULL,
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
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    }
                    return null;
                }
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to get user", ex);
        }
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        var json = new com.google.gson.Gson().toJson(game.game());
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(
                    "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, game.gameID());
                ps.setString(2, game.whiteUsername());
                ps.setString(3, game.blackUsername());
                ps.setString(4, game.gameName());
                ps.setString(5, json);
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to create game", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?")) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var chessGame = new com.google.gson.Gson().fromJson(rs.getString("game"),
                                chess.ChessGame.class);
                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                chessGame);
                    }
                    return null;
                }
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to get game", ex);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var games = new java.util.ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(
                    "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game")) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var chessGame = new com.google.gson.Gson()
                                .fromJson(rs.getString("game"), chess.ChessGame.class);
                        games.add(new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                chessGame));
                    }
                }
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to list games", ex);
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var json = new com.google.gson.Gson().toJson(game.game());
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(
                    "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?")) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());
                ps.setString(4, json);
                ps.setInt(5, game.gameID());
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to update game", ex);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
                ps.setString(1, auth.authToken());
                ps.setString(2, auth.username());
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to create auth", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT authToken, username FROM auth WHERE authToken=?")) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
                    }
                    return null;
                }
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to get auth", ex);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException ex) {
            throw new DataAccessException("Unable to delete auth", ex);
        }
    }
}
