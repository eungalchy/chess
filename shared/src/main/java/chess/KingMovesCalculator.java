package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myposition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1,1}, {1,-1}, {-1, 1}, {-1, -1}, {1,0}, {-1,0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int row = myposition.getRow() + dir[0];
            int col = myposition.getColumn() + dir[1];
            if (row < 1 || row > 8 || col < 1 || col > 8) {
                continue;
            }
            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece occupant = board.getPiece(newPos);
            if (occupant == null || occupant.getTeamColor() != board.getPiece(myposition).getTeamColor()) {
                moves.add(new ChessMove(myposition, newPos, null));
            }
        }
        return moves;
    }
}