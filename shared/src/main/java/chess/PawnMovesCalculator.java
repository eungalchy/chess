package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myposition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myposition.getRow();
        int col = myposition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myposition).getTeamColor();

        int direction;
        int startRow;
        int promoRow;
        if (color == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
            promoRow = 8;
        } else {
            direction = -1;
            startRow = 7;
            promoRow = 1;
        }
        int oneRow = row + direction;
        if (oneRow >= 1 && oneRow <= 8) {
            ChessPosition oneStep = new ChessPosition(oneRow, col);
            if (board.getPiece(oneStep) == null) {
                addPawnMove(moves, myposition, oneStep, promoRow);
                if (row == startRow) {
                    ChessPosition twoStep = new ChessPosition(row + 2 * direction, col);
                    if (board.getPiece(twoStep) == null) {
                        addPawnMove(moves, myposition, twoStep, promoRow);
                    }
                }
            }
        }
        int[] captureCol = {col - 1, col + 1};
        for (int newCol : captureCol) {
            if (oneRow >= 1 && oneRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition capturePos = new ChessPosition(oneRow, newCol);
                ChessPiece occupant = board.getPiece(capturePos);
                if (occupant != null && occupant.getTeamColor() != color) {
                    addPawnMove(moves, myposition, capturePos, promoRow);
                }
            }
        }
        return moves;
    }
    private void addPawnMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, int promoRow) {
        if (end.getRow() == promoRow) {
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }
}
