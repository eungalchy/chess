package chess;

import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {
    private static final int[][] DIRECTIONS = {{1,1}, {1,-1}, {-1, 1}, {-1, -1}};

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return slidingMoves(board, myPosition, DIRECTIONS);
    }
}
