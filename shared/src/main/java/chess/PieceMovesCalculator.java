package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PieceMovesCalculator {

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    protected Collection<ChessMove> slidingMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();

        for (int[] dir : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while(true) {
                row += dir[0];
                col += dir[1];
                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece occupant = board.getPiece(newPos);
                if (occupant == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else {
                    if (occupant.getTeamColor() != myColor) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
            }
        }
        return moves;
    }

    protected Collection<ChessMove> singleStepMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();

        for (int[] dir : directions) {
            int row = myPosition.getRow() + dir[0];
            int col = myPosition.getColumn() + dir[1];
            if (row < 1 || row > 8 || col < 1 || col > 8) {
                continue;
            }
            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece occupant = board.getPiece(newPos);
            if (occupant == null || occupant.getTeamColor() != myColor) {
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }
        return moves;
    }
}
