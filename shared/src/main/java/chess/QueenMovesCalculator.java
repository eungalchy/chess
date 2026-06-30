package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myposition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1,1}, {1,-1}, {-1, 1}, {-1, -1}, {1,0}, {-1,0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int dRow = dir[0];
            int dCol = dir[1];
            int row = myposition.getRow();
            int col = myposition.getColumn();
            while (true) {
                row += dRow;
                col += dCol;
                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece occupant = board.getPiece(newPos);
                if (occupant == null) {
                    moves.add(new ChessMove(myposition, newPos, null));
                } else {
                    if (occupant.getTeamColor() != board.getPiece(myposition).getTeamColor()) {
                        moves.add(new ChessMove(myposition, newPos, null));
                    }
                    break;
                }
            }
        }
        return moves;
    }
}
