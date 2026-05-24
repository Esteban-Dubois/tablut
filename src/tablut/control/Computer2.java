package tablut.control;

import tablut.model.ModelBoard;
import tablut.model.ModelPawn;

/**
 * AI player that uses the Minimax algorithm to choose its move.
 * <p>
 * The board state is encoded as a 9×9 integer grid:
 * 0 = empty, 1 = black pawn, 2 = white pawn, 3 = King.
 * The AI looks one move ahead with Minimax and picks the move
 * with the best score according to {@link #evaluateBoard(int[][])}.
 * </p>
 */
public class Computer2 {

    /**
     * Finds and returns the best move for the given color.
     * <p>
     * The method converts the real board into a virtual integer grid,
     * then tries every legal move for its own pieces. For each candidate
     * move it calls {@link #runMinimax(int[][], int, boolean)} to evaluate
     * the resulting position one ply deep and keeps the move with the
     * highest score (white) or lowest score (black).
     * </p>
     *
     * @param board   the current game board
     * @param myColor the color of this AI ({@code ModelPawn.PAWN_BLACK} or {@code ModelPawn.PAWN_WHITE})
     * @return an array of four integers: {@code [sourceRow, sourceColumn, destinationRow, destinationColumn]}
     */
    public int[] getBestMove(ModelBoard board, int myColor) {
        int[][] virtualBoard = new int[9][9];
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (board.isElementAt(row, column) == true) {
                    ModelPawn pawn = (ModelPawn) board.getElement(row, column);
                    if (pawn.getKing() == true) {
                        virtualBoard[row][column] = 3;
                    } else if (pawn.getColor() == ModelPawn.PAWN_BLACK) {
                        virtualBoard[row][column] = 1;
                    } else {
                        virtualBoard[row][column] = 2;
                    }
                } else {
                    virtualBoard[row][column] = 0;
                }
            }
        }

        int bestSourceRow = -1;
        int bestSourceColumn = -1;
        int bestDestinationRow = -1;
        int bestDestinationColumn = -1;

        int playerType = 1;
        if (myColor != ModelPawn.PAWN_BLACK) {
            playerType = 2;
        }

        if (playerType == 2) {
            int bestValue = -999999;

            for (int sourceRow = 0; sourceRow < 9; sourceRow++) {
                for (int sourceColumn = 0; sourceColumn < 9; sourceColumn++) {
                    if (virtualBoard[sourceRow][sourceColumn] == 2 || virtualBoard[sourceRow][sourceColumn] == 3) {
                        for (int destinationRow = 0; destinationRow < 9; destinationRow++) {
                            for (int destinationColumn = 0; destinationColumn < 9; destinationColumn++) {
                                if (checkVirtualMove(virtualBoard, sourceRow, sourceColumn, destinationRow, destinationColumn, 2) == true) {
                                    int[][] nextBoard = copyBoard(virtualBoard);
                                    executeVirtualMove(nextBoard, sourceRow, sourceColumn, destinationRow, destinationColumn);

                                    int moveValue = runMinimax(nextBoard, 1, false);
                                    if (moveValue > bestValue) {
                                        bestValue = moveValue;
                                        bestSourceRow = sourceRow;
                                        bestSourceColumn = sourceColumn;
                                        bestDestinationRow = destinationRow;
                                        bestDestinationColumn = destinationColumn;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            int bestValue = 999999;

            for (int sourceRow = 0; sourceRow < 9; sourceRow++) {
                for (int sourceColumn = 0; sourceColumn < 9; sourceColumn++) {
                    if (virtualBoard[sourceRow][sourceColumn] == 1) {
                        for (int destinationRow = 0; destinationRow < 9; destinationRow++) {
                            for (int destinationColumn = 0; destinationColumn < 9; destinationColumn++) {
                                if (checkVirtualMove(virtualBoard, sourceRow, sourceColumn, destinationRow, destinationColumn, 1) == true) {
                                    int[][] nextBoard = copyBoard(virtualBoard);
                                    executeVirtualMove(nextBoard, sourceRow, sourceColumn, destinationRow, destinationColumn);

                                    int moveValue = runMinimax(nextBoard, 1, true);
                                    if (moveValue < bestValue) {
                                        bestValue = moveValue;
                                        bestSourceRow = sourceRow;
                                        bestSourceColumn = sourceColumn;
                                        bestDestinationRow = destinationRow;
                                        bestDestinationColumn = destinationColumn;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        int[] result = new int[4];
        result[0] = bestSourceRow;
        result[1] = bestSourceColumn;
        result[2] = bestDestinationRow;
        result[3] = bestDestinationColumn;
        return result;
    }

    /**
     * Recursively evaluates the board using the Minimax algorithm.
     * <p>
     * When {@code isMaximizingPlayer} is {@code true} the method simulates
     * the white player and looks for the highest score. When it is {@code false}
     * it simulates the black player and looks for the lowest score.
     * The recursion stops when {@code depth} reaches 0 or a winning/losing
     * position is detected (score above 90 000 or below −90 000).
     * </p>
     *
     * @param board              the virtual board to evaluate
     * @param depth              the number of half-moves still to simulate
     * @param isMaximizingPlayer {@code true} if it is white's turn to simulate
     * @return the best score found for the current player
     */
    private int runMinimax(int[][] board, int depth, boolean isMaximizingPlayer) {
        int boardScore = evaluateBoard(board);
        if (depth == 0 || boardScore >= 90000 || boardScore <= -90000) {
            return boardScore;
        }

        if (isMaximizingPlayer == true) {
            int maxEvaluation = -999999;

            for (int sourceRow = 0; sourceRow < 9; sourceRow++) {
                for (int sourceColumn = 0; sourceColumn < 9; sourceColumn++) {
                    if (board[sourceRow][sourceColumn] == 2 || board[sourceRow][sourceColumn] == 3) {
                        for (int destinationRow = 0; destinationRow < 9; destinationRow++) {
                            for (int destinationColumn = 0; destinationColumn < 9; destinationColumn++) {
                                if (checkVirtualMove(board, sourceRow, sourceColumn, destinationRow, destinationColumn, 2) == true) {
                                    int[][] nextBoard = copyBoard(board);
                                    executeVirtualMove(nextBoard, sourceRow, sourceColumn, destinationRow, destinationColumn);

                                    int evaluation = runMinimax(nextBoard, depth - 1, false);
                                    if (evaluation > maxEvaluation) {
                                        maxEvaluation = evaluation;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return maxEvaluation;
        } else {
            int minEvaluation = 999999;

            for (int sourceRow = 0; sourceRow < 9; sourceRow++) {
                for (int sourceColumn = 0; sourceColumn < 9; sourceColumn++) {
                    if (board[sourceRow][sourceColumn] == 1) {
                        for (int destinationRow = 0; destinationRow < 9; destinationRow++) {
                            for (int destinationColumn = 0; destinationColumn < 9; destinationColumn++) {
                                if (checkVirtualMove(board, sourceRow, sourceColumn, destinationRow, destinationColumn, 1) == true) {
                                    int[][] nextBoard = copyBoard(board);
                                    executeVirtualMove(nextBoard, sourceRow, sourceColumn, destinationRow, destinationColumn);

                                    int evaluation = runMinimax(nextBoard, depth - 1, true);
                                    if (evaluation < minEvaluation) {
                                        minEvaluation = evaluation;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return minEvaluation;
        }
    }

    /**
     * Checks whether a move is legal on the virtual integer board.
     * <p>
     * The rules applied are the same as in {@link Rules#checkMoveValidity}:
     * no diagonal moves, no jumping over pieces, non-King pawns cannot pass
     * through the throne (4,4) or the four corner cells.
     * </p>
     *
     * @param board             the virtual board
     * @param sourceRow         the row of the piece to move
     * @param sourceColumn      the column of the piece to move
     * @param destinationRow    the target row
     * @param destinationColumn the target column
     * @param playerColor       the color of the player (1 = black, 2 = white)
     * @return {@code true} if the move is legal, {@code false} otherwise
     */
    private boolean checkVirtualMove(int[][] board, int sourceRow, int sourceColumn, int destinationRow, int destinationColumn, int playerColor) {
        if (board[sourceRow][sourceColumn] == 0) {
            return false;
        }

        int pawnType = board[sourceRow][sourceColumn];
        boolean isKing = false;
        if (pawnType == 3) {
            isKing = true;
        }

        if (playerColor == 1 && pawnType == 2) {
            return false;
        }
        if (playerColor == 1 && pawnType == 3) {
            return false;
        }
        if (playerColor == 2 && pawnType == 1) {
            return false;
        }

        if (sourceRow != destinationRow && sourceColumn != destinationColumn) {
            return false;
        }
        if (sourceRow == destinationRow && sourceColumn == destinationColumn) {
            return false;
        }

        if (sourceColumn == destinationColumn) {
            if (sourceRow > destinationRow) {
                for (int i = sourceRow - 1; i >= destinationRow; i--) {
                    if (board[i][destinationColumn] != 0) {
                        return false;
                    }
                    if (i == 4 && destinationColumn == 4 && isKing == false) {
                        return false;
                    }
                    if (((i == 0 && destinationColumn == 0) || (i == 0 && destinationColumn == 8) || (i == 8 && destinationColumn == 0) || (i == 8 && destinationColumn == 8)) && isKing == false) {
                        return false;
                    }
                }
            } else if (sourceRow < destinationRow) {
                for (int i = sourceRow + 1; i <= destinationRow; i++) {
                    if (board[i][destinationColumn] != 0) {
                        return false;
                    }
                    if (i == 4 && destinationColumn == 4 && isKing == false) {
                        return false;
                    }
                    if (((i == 0 && destinationColumn == 0) || (i == 0 && destinationColumn == 8) || (i == 8 && destinationColumn == 0) || (i == 8 && destinationColumn == 8)) && isKing == false) {
                        return false;
                    }
                }
            }
        } else {
            if (sourceColumn > destinationColumn) {
                for (int i = sourceColumn - 1; i >= destinationColumn; i--) {
                    if (board[destinationRow][i] != 0) {
                        return false;
                    }
                    if (destinationRow == 4 && i == 4 && isKing == false) {
                        return false;
                    }
                    if (((destinationRow == 0 && i == 0) || (destinationRow == 0 && i == 8) || (destinationRow == 8 && i == 0) || (destinationRow == 8 && i == 8)) && isKing == false) {
                        return false;
                    }
                }
            } else if (sourceColumn < destinationColumn) {
                for (int i = sourceColumn + 1; i <= destinationColumn; i++) {
                    if (board[destinationRow][i] != 0) {
                        return false;
                    }
                    if (destinationRow == 4 && i == 4 && isKing == false) {
                        return false;
                    }
                    if (((destinationRow == 0 && i == 0) || (destinationRow == 0 && i == 8) || (destinationRow == 8 && i == 0) || (destinationRow == 8 && i == 8)) && isKing == false) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Applies a move on the virtual board, including custodian captures and
     * King capture detection.
     * <p>
     * After placing the piece on the destination cell, the method checks all
     * four adjacent cells for sandwiched enemy pawns and removes them.
     * The King is never removed by the sandwich rule; use
     * {@link #checkVirtualKingCapture(int[][])} for that.
     * </p>
     *
     * @param board             the virtual board to modify
     * @param sourceRow         the row of the piece to move
     * @param sourceColumn      the column of the piece to move
     * @param destinationRow    the target row
     * @param destinationColumn the target column
     */
    private void executeVirtualMove(int[][] board, int sourceRow, int sourceColumn, int destinationRow, int destinationColumn) {
        int pawnType = board[sourceRow][sourceColumn];
        board[sourceRow][sourceColumn] = 0;
        board[destinationRow][destinationColumn] = pawnType;

        int myColor = 1;
        if (pawnType == 2 || pawnType == 3) {
            myColor = 2;
        }

        if (destinationRow >= 2) {
            int target = board[destinationRow - 1][destinationColumn];
            if (target != 0 && isVirtualEnemy(target, myColor) == true) {
                if (target != 3) {
                    if (isVirtualAllyOrTrap(board, destinationRow - 2, destinationColumn, myColor) == true) {
                        board[destinationRow - 1][destinationColumn] = 0;
                    }
                }
            }
        }
        if (destinationRow <= 6) {
            int target = board[destinationRow + 1][destinationColumn];
            if (target != 0 && isVirtualEnemy(target, myColor) == true) {
                if (target != 3) {
                    if (isVirtualAllyOrTrap(board, destinationRow + 2, destinationColumn, myColor) == true) {
                        board[destinationRow + 1][destinationColumn] = 0;
                    }
                }
            }
        }
        if (destinationColumn >= 2) {
            int target = board[destinationRow][destinationColumn - 1];
            if (target != 0 && isVirtualEnemy(target, myColor) == true) {
                if (target != 3) {
                    if (isVirtualAllyOrTrap(board, destinationRow, destinationColumn - 2, myColor) == true) {
                        board[destinationRow][destinationColumn - 1] = 0;
                    }
                }
            }
        }
        if (destinationColumn <= 6) {
            int target = board[destinationRow][destinationColumn + 1];
            if (target != 0 && isVirtualEnemy(target, myColor) == true) {
                if (target != 3) {
                    if (isVirtualAllyOrTrap(board, destinationRow, destinationColumn + 2, myColor) == true) {
                        board[destinationRow][destinationColumn + 1] = 0;
                    }
                }
            }
        }

        checkVirtualKingCapture(board);
    }

    /**
     * Returns {@code true} if the given piece type belongs to the enemy.
     *
     * @param pawnType the integer code of the piece (1 = black, 2 = white, 3 = King)
     * @param myColor  the color of the current player (1 = black, 2 = white)
     * @return {@code true} if the piece is an enemy, {@code false} otherwise
     */
    private boolean isVirtualEnemy(int pawnType, int myColor) {
        if (myColor == 1) {
            if (pawnType == 2 || pawnType == 3) {
                return true;
            }
        } else {
            if (pawnType == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the cell behind a potential capture target
     * counts as the second side of the sandwich.
     * <p>
     * A cell counts if it holds a friendly piece, or if it is one of the
     * four corner cells, or if it is the empty throne at (4, 4).
     * </p>
     *
     * @param board   the virtual board
     * @param row     the row of the cell to check
     * @param column  the column of the cell to check
     * @param myColor the color of the attacking player (1 = black, 2 = white)
     * @return {@code true} if the cell completes a valid sandwich, {@code false} otherwise
     */
    private boolean isVirtualAllyOrTrap(int[][] board, int row, int column, int myColor) {
        int cell = board[row][column];
        if (myColor == 1) {
            if (cell == 1) {
                return true;
            }
        } else {
            if (cell == 2 || cell == 3) {
                return true;
            }
        }
        if ((row == 0 && column == 0) || (row == 0 && column == 8) || (row == 8 && column == 0) || (row == 8 && column == 8)) {
            return true;
        }
        if (row == 4 && column == 4 && cell == 0) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether the King is fully surrounded and removes him from the
     * virtual board if so.
     * <p>
     * The King is considered blocked on a side if that side has a board edge,
     * a black pawn, or the throne cell (4, 4).
     * </p>
     *
     * @param board the virtual board to check and potentially modify
     */
    private void checkVirtualKingCapture(int[][] board) {
        int kingRow = -1;
        int kingColumn = -1;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == 3) {
                    kingRow = r;
                    kingColumn = c;
                }
            }
        }

        if (kingRow == -1) {
            return;
        }

        boolean blockedUp = false;
        boolean blockedDown = false;
        boolean blockedLeft = false;
        boolean blockedRight = false;

        if (kingRow == 0 || board[kingRow - 1][kingColumn] == 1 || (kingRow - 1 == 4 && kingColumn == 4)) {
            blockedUp = true;
        }
        if (kingRow == 8 || board[kingRow + 1][kingColumn] == 1 || (kingRow + 1 == 4 && kingColumn == 4)) {
            blockedDown = true;
        }
        if (kingColumn == 0 || board[kingRow][kingColumn - 1] == 1 || (kingRow == 4 && kingColumn - 1 == 4)) {
            blockedLeft = true;
        }
        if (kingColumn == 8 || board[kingRow][kingColumn + 1] == 1 || (kingRow == 4 && kingColumn + 1 == 4)) {
            blockedRight = true;
        }

        if (blockedUp == true && blockedDown == true && blockedLeft == true && blockedRight == true) {
            board[kingRow][kingColumn] = 0;
        }
    }

    /**
     * Scores the current state of the virtual board from white's perspective.
     * <p>
     * Scoring rules:
     * </p>
     * <ul>
     *   <li>King captured → −100 000 (black wins).</li>
     *   <li>King on a corner → +100 000 (white wins).</li>
     *   <li>Each black pawn on the board → −1 000 points, plus a bonus equal to
     *       its Manhattan distance from the King (encourages black to close in).</li>
     *   <li>Each white pawn on the board → +1 000 points, minus 2 if it shares
     *       a row or column with the King (penalises blocking the King's escape).</li>
     *   <li>King's Manhattan distance to the nearest corner → −10 points per cell
     *       (encourages the King to move toward an exit).</li>
     * </ul>
     *
     * @param board the virtual board to evaluate
     * @return the score of the position (positive = good for white, negative = good for black)
     */
    private int evaluateBoard(int[][] board) {
        boolean kingAlive = false;
        int kingRow = -1;
        int kingColumn = -1;

        for (int ligne = 0; ligne < 9; ligne++) {
            for (int colonne = 0; colonne < 9; colonne++) {
                if (board[ligne][colonne] == 3) {
                    kingAlive = true;
                    kingRow = ligne;
                    kingColumn = colonne;
                }
            }
        }

        if (kingAlive == false) {
            return -100000;
        }

        boolean coinHautGauche = (kingRow == 0 && kingColumn == 0);
        boolean coinHautDroite = (kingRow == 0 && kingColumn == 8);
        boolean coinBasGauche = (kingRow == 8 && kingColumn == 0);
        boolean coinBasDroite = (kingRow == 8 && kingColumn == 8);

        if (coinHautGauche == true || coinHautDroite == true || coinBasGauche == true || coinBasDroite == true) {
            return 100000;
        }

        int score = 0;

        for (int ligne = 0; ligne < 9; ligne++) {
            for (int colonne = 0; colonne < 9; colonne++) {

                if (board[ligne][colonne] == 1) {
                    score = score - 1000;

                    int distanceLigne = Math.abs(ligne - kingRow);
                    int distanceColonne = Math.abs(colonne - kingColumn);
                    int distanceTotale = distanceLigne + distanceColonne;

                    score = score + distanceTotale;
                }

                if (board[ligne][colonne] == 2) {
                    score = score + 1000;

                    if (ligne == kingRow || colonne == kingColumn) {
                        score = score - 2;
                    }
                }

            }
        }

        int distanceHautGaucheSortie = kingRow + kingColumn;
        int distanceHautDroiteSortie = kingRow + (8 - kingColumn);
        int distanceBasGaucheSortie = (8 - kingRow) + kingColumn;
        int distanceBasDroiteSortie = (8 - kingRow) + (8 - kingColumn);

        int distanceMinimaleHaut = Math.min(distanceHautGaucheSortie, distanceHautDroiteSortie);
        int distanceMinimaleBas = Math.min(distanceBasGaucheSortie, distanceBasDroiteSortie);
        int distanceFinaleSortie = Math.min(distanceMinimaleHaut, distanceMinimaleBas);

        score = score - (distanceFinaleSortie * 10);

        return score;
    }

    /**
     * Creates and returns a deep copy of the given 9×9 virtual board.
     *
     * @param board the board to copy
     * @return a new 9×9 array with the same values
     */
    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                newBoard[r][c] = board[r][c];
            }
        }
        return newBoard;
    }
}
