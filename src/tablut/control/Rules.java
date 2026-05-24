package tablut.control;

import tablut.model.ModelBoard;

/**
 * Rules contains the universal movement rules for the game.
 */
public class Rules {

    /**
     * Checks if a movement from a source to a destination is allowed.
     *
     * @param board The current game board.
     * @param sourceRow The starting row of the piece.
     * @param sourceColumn The starting column of the piece.
     * @param destinationRow The target row.
     * @param destinationColumn The target column.
     * @param isKing True if the moving piece is the king, false otherwise.
     * @return True if the move is valid, false otherwise.
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
