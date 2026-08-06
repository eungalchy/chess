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

        try {
            System.out.println("Making move: " + move);
            return true;
        } catch (Exception e) {
            System.out.println("Move error: " + e.getMessage());
            return false;
        }
    }
}