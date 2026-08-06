package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import chess.ChessMove;
import chess.ChessGame;

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
            ChessGame chessGame = game.game();
            chessGame.makeMove(move);

            GameData updatedGame = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    chessGame
            );
            dataAccess.updateGame(updatedGame);
            return true;
        } catch (Exception e) {
            System.out.println("Move error: " + e.getMessage());
            return false;
        }
    }
}