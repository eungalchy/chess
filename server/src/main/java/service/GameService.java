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
}
