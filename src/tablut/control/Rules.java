package tablut.control;

import tablut.model.ModelBoard;

/**
 * Contains the movement rules for the Tablut game.
 * <p>
 * All methods are static so they can be called from anywhere without
 * creating an instance of this class.
 * </p>
 */
public class Rules {

    /**
     * Checks whether a move from a source cell to a destination cell is legal.
     * <p>
     * A move is illegal if any of the following is true:
     * </p>
     * <ul>
     *   <li>The move is diagonal (row and column both change).</li>
     *   <li>The source and destination are the same cell.</li>
     *   <li>Another piece is blocking the path.</li>
     *   <li>A non-King pawn tries to pass through or land on the throne.</li>
     *   <li>A non-King pawn tries to pass through or land on a corner.</li>
     * </ul>
     *
     * @param board            the current game board
     * @param sourceRow        the row of the piece to move
     * @param sourceColumn     the column of the piece to move
     * @param destinationRow   the target row
     * @param destinationColumn the target column
     * @param isKing           {@code true} if the moving piece is the King
     * @return {@code true} if the move is legal, {@code false} otherwise
     */
    public static boolean checkMoveValidity(ModelBoard board, int sourceRow, int sourceColumn, int destinationRow, int destinationColumn, boolean isKing) {
        if (sourceRow != destinationRow && sourceColumn != destinationColumn) {
            return false;
        }
        if (sourceRow == destinationRow && sourceColumn == destinationColumn) {
            return false;
        }

        if (sourceColumn == destinationColumn) {
            if (sourceRow > destinationRow) {
                for (int index = (sourceRow - 1); index >= destinationRow; index--) {
                    if (board.isElementAt(index, destinationColumn) == true) {
                        return false;
                    }
                    if (board.isThrone(index, destinationColumn) == true && isKing == false) {
                        return false;
                    }
                    if (board.isCorner(index, destinationColumn) == true && isKing == false) {
                        return false;
                    }
                }
            } else if (sourceRow < destinationRow) {
                for (int index = (sourceRow + 1); index <= destinationRow; index++) {
                    if (board.isElementAt(index, destinationColumn) == true) {
                        return false;
                    }
                    if (board.isThrone(index, destinationColumn) == true && isKing == false) {
                        return false;
                    }
                    if (board.isCorner(index, destinationColumn) == true && isKing == false) {
                        return false;
                    }
                }
            }
        } else {
            if (sourceColumn > destinationColumn) {
                for (int index = (sourceColumn - 1); index >= destinationColumn; index--) {
                    if (board.isElementAt(destinationRow, index) == true) {
                        return false;
                    }
                    if (board.isThrone(destinationRow, index) == true && isKing == false) {
                        return false;
                    }
                    if (board.isCorner(destinationRow, index) == true && isKing == false) {
                        return false;
                    }
                }
            } else if (sourceColumn < destinationColumn) {
                for (int index = (sourceColumn + 1); index <= destinationColumn; index++) {
                    if (board.isElementAt(destinationRow, index) == true) {
                        return false;
                    }
                    if (board.isThrone(destinationRow, index) == true && isKing == false) {
                        return false;
                    }
                    if (board.isCorner(destinationRow, index) == true && isKing == false) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
