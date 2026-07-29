package chess;

import java.util.Collection;

public class QueenMovesCalculator extends PieceMovesCalculator {
    private static final int[][] DIRECTIONS = {
            {1,1}, {1,-1}, {-1, 1}, {-1, -1},
            {1,0}, {-1,0}, {0, 1}, {0, -1}

    };

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return slidingMoves(board, myPosition, DIRECTIONS);
    }
}
