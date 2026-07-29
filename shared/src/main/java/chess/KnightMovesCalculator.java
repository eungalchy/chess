package chess;

import java.util.Collection;

public class KnightMovesCalculator extends PieceMovesCalculator {
    private static final int[][] DIRECTIONS = {
            {2,1}, {2,-1}, {-2, 1}, {-2, -1},
            {1,2}, {-1,2}, {-1, -2}, {1, -2}

    };

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return singleStepMoves(board, myPosition, DIRECTIONS);
    }
}
