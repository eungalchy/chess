package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import chess.ChessMove;

public class GamePlayService {
    private final DataAccess dataAccess;

    public GamePlayService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return dataAccess.getGame(gameID);
    }

    public boolean makeMove(int gameID, ChessMove move) throws DataAccessException {
        GameData game = getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return true;
    }
}