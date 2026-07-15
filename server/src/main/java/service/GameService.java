package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        int gameID = Math.abs(java.util.UUID.randomUUID().hashCode());
        GameData game = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        dataAccess.createGame(game);
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGamesResult(String authToken) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return new ListGamesResult(dataAccess.listGames());
    }

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData game = dataAccess.getGame(request.gameID());
        if (game == null || request.playerColor() == null) {
            throw new DataAccessException("Error: bad request");
        }

        String username = auth.username();
        GameData updated;
        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (request.playerColor().equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            throw new DataAccessException("Error: bad request");
        }

        dataAccess.updateGame(updated);
    }
}
