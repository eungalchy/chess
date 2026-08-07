package ui;

import static ui.EscapeSequences.*;

public class BoardPrinter {
    private static final String[][] INITIAL_BOARD = {
            {"R", "N", "B", "Q", "K", "B", "N", "R"},
            {"P", "P", "P", "P", "P", "P", "P", "P"},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {"p", "p", "p", "p", "p", "p", "p", "p"},
            {"r", "n", "b", "q", "k", "b", "n", "r"}
    };

    public static void printBoard(boolean whitePerspective) {
        var columns = "abcdefgh";
        var out = new StringBuilder();

        int startRow = whitePerspective ? 7 : 0;
        int endRow = whitePerspective ? -1 : 8;
        int rowStep = whitePerspective ? -1 : 1;

        out.append(headFooter(whitePerspective, columns));
        for (int row = startRow; row != endRow; row += rowStep) {
            out.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row + 1).append(" ").append(RESET_BG_COLOR);

            int startCol = whitePerspective ? 0 : 7;
            int endCol = whitePerspective ? 8 : -1;
            int colStep = whitePerspective ? 1 : -1;

            for (int col = startCol; col != endCol; col += colStep) {
                boolean isLight = (row + col) % 2 != 0;
                out.append(isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_GREEN);
                out.append(pieceSymbol(INITIAL_BOARD[row][col]));
            }

            out.append(RESET_BG_COLOR);
            out.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row + 1).append(RESET_BG_COLOR);
            out.append("\n");
        }
        out.append(headFooter(whitePerspective, columns));
        System.out.print(out);
    }


    private static String headFooter(boolean whitePerspective, String columns) {
        var sb = new StringBuilder();
        sb.append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(RESET_BG_COLOR);
        if (whitePerspective) {
            for (char c : columns.toCharArray()) {
                sb.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(c).append(" ").append(RESET_BG_COLOR);
            }
        } else {
            for (int i = columns.length() - 1; i >= 0; i--) {
                sb.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(columns.charAt(i)).append(" ").append(RESET_BG_COLOR);
            }
        }
        sb.append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(RESET_BG_COLOR).append("\n");
        return sb.toString();
    };

    private static String pieceSymbol(String piece) {
        return switch (piece) {
            case "R" -> SET_TEXT_COLOR_RED + WHITE_ROOK;
            case "N" -> SET_TEXT_COLOR_RED + WHITE_KNIGHT;
            case "B" -> SET_TEXT_COLOR_RED + WHITE_BISHOP;
            case "Q" -> SET_TEXT_COLOR_RED + WHITE_QUEEN;
            case "K" -> SET_TEXT_COLOR_RED + WHITE_KING;
            case "P" -> SET_TEXT_COLOR_RED + WHITE_PAWN;
            case "r" -> SET_TEXT_COLOR_BLUE + BLACK_ROOK;
            case "n" -> SET_TEXT_COLOR_BLUE + BLACK_KNIGHT;
            case "b" -> SET_TEXT_COLOR_BLUE + BLACK_BISHOP;
            case "q" -> SET_TEXT_COLOR_BLUE + BLACK_QUEEN;
            case "k" -> SET_TEXT_COLOR_BLUE + BLACK_KING;
            case "p" -> SET_TEXT_COLOR_BLUE + BLACK_PAWN;
            default -> EMPTY;
        };
    }

    public static void printBoard(chess.ChessBoard board, boolean whitePerspective) {
        var columns = "abcdefgh";
        var out = new StringBuilder();

        int startRow = whitePerspective ? 7 : 0;
        int endRow = whitePerspective ? -1 : 8;
        int rowStep = whitePerspective ? -1 : 1;

        out.append(headFooter(whitePerspective, columns));
        for (int row = startRow; row != endRow; row += rowStep) {
            out.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row + 1).append(" ").append(RESET_BG_COLOR);

            int startCol = whitePerspective ? 0 : 7;
            int endCol = whitePerspective ? 8 : -1;
            int colStep = whitePerspective ? 1 : -1;

            for (int col = startCol; col != endCol; col += colStep) {
                boolean isLight = (row + col) % 2 != 0;
                out.append(isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_GREEN);

                chess.ChessPiece piece = board.getPiece(new chess.ChessPosition(row + 1, col + 1));
                out.append(pieceSymbol(pieceToString(piece)));
            }

            out.append(RESET_BG_COLOR);
            out.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row + 1).append(RESET_BG_COLOR);
            out.append("\n");
        }
        out.append(headFooter(whitePerspective, columns));
        System.out.print(out);
    }

    private static String pieceToString(chess.ChessPiece piece) {
        if (piece == null) {
            return " ";
        }
        String type = switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };
        return piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE ? type : type.toLowerCase();
    }

    public static void printBoardHighlighted(chess.ChessBoard board, boolean whitePerspective,
                                             chess.ChessPosition selected,
                                             java.util.Collection<chess.ChessPosition> highlights) {

        var columns = "abcdefgh";
        var out = new StringBuilder();

        int startRow = whitePerspective ? 7 : 0;
        int endRow = whitePerspective ? -1 : 8;
        int rowStep = whitePerspective ? -1 : 1;

        out.append(headFooter(whitePerspective, columns));
        for (int row = startRow; row != endRow; row += rowStep) {
            out.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row + 1).append(" ").append(RESET_BG_COLOR);

            int startCol = whitePerspective ? 0 : 7;
            int endCol = whitePerspective ? 8 : -1;
            int colStep = whitePerspective ? 1 : -1;

            for (int col = startCol; col != endCol; col += colStep) {
                chess.ChessPosition pos = new chess.ChessPosition(row + 1, col + 1);
                boolean isLight = (row + col) % 2 != 0;

                if (selected != null && pos.equals(selected)) {
                    out.append(SET_BG_COLOR_YELLOW);
                } else if (highlights != null && highlights.contains(pos)) {
                    out.append(SET_BG_COLOR_YELLOW);
                } else {
                    out.append(isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_GREEN);
                }

                chess.ChessPiece piece = board.getPiece(pos);
                out.append(pieceSymbol(pieceToString(piece)));
            }

            out.append(RESET_BG_COLOR);
            out.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(row + 1).append(RESET_BG_COLOR);
            out.append("\n");
        }
        out.append(headFooter(whitePerspective, columns));
        System.out.print(out);
    }

}
