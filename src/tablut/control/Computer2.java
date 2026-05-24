package tablut.control;

import tablut.model.ModelBoard;
import tablut.model.ModelPawn;

/**
 * Computer2 calculates moves for the AI using the Minimax algorithm.
 */
public class Computer2 {

    /**
     * Calculates the best move for the AI.
     *
     * @param board The current game board.
     * @param myColor The color of the AI pieces.
     * @param lastSourceRow The row of the last piece moved by the computer.
     * @param lastSourceColumn The column of the last piece moved by the computer.
     * @param lastDestinationRow The destination row of the last move.
     * @param lastDestinationColumn The destination column of the last move.
     * @return An array containing the best move coordinates.
     */
    public int[] getBestMove(ModelBoard board, int myColor, int lastSourceRow, int lastSourceColumn, int lastDestinationRow, int lastDestinationColumn) {
        int[][] virtualBoard = new int[9][9];
        for (int ligne = 0; ligne < 9; ligne++) {
            for (int colonne = 0; colonne < 9; colonne++) {
                if (board.isElementAt(ligne, colonne) == true) {
                    ModelPawn pawn = (ModelPawn) board.getElement(ligne, colonne);
                    if (pawn.getKing() == true) {
                        virtualBoard[ligne][colonne] = 3;
                    } else if (pawn.getColor() == ModelPawn.PAWN_BLACK) {
                        virtualBoard[ligne][colonne] = 1;
                    } else {
                        virtualBoard[ligne][colonne] = 2;
                    }
                } else {
                    virtualBoard[ligne][colonne] = 0;
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
                                    
                                    boolean memeLigneDepart = (sourceRow == lastDestinationRow);
                                    boolean memeColonneDepart = (sourceColumn == lastDestinationColumn);
                                    boolean memeLigneArrivee = (destinationRow == lastSourceRow);
                                    boolean memeColonneArrivee = (destinationColumn == lastSourceColumn);

                                    if (memeLigneDepart == true && memeColonneDepart == true && memeLigneArrivee == true && memeColonneArrivee == true) {
                                        moveValue = moveValue - 50000;
                                    }

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
                                    
                                    boolean memeLigneDepart = (sourceRow == lastDestinationRow);
                                    boolean memeColonneDepart = (sourceColumn == lastDestinationColumn);
                                    boolean memeLigneArrivee = (destinationRow == lastSourceRow);
                                    boolean memeColonneArrivee = (destinationColumn == lastSourceColumn);

                                    if (memeLigneDepart == true && memeColonneDepart == true && memeLigneArrivee == true && memeColonneArrivee == true) {
                                        moveValue = moveValue + 50000;
                                    }

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
     * Explores future game states to evaluate a move.
     *
     * @param board The current state of the virtual board.
     * @param depth The remaining depth to explore.
     * @param isMaximizingPlayer True if it is the turn of the maximizing player.
     * @return The evaluation score of the board.
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
     * Checks if a move is valid on the virtual board.
     *
     * @param board The virtual board state.
     * @param sourceRow The starting row.
     * @param sourceColumn The starting column.
     * @param destinationRow The destination row.
     * @param destinationColumn The destination column.
     * @param playerColor The color of the player.
     * @return True if the move is valid, false otherwise.
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
                for (int index = sourceRow - 1; index >= destinationRow; index--) {
                    if (board[index][destinationColumn] != 0) {
                        return false;
                    }
                    if (index == 4 && destinationColumn == 4 && isKing == false) {
                        return false;
                    }
                    if (((index == 0 && destinationColumn == 0) || (index == 0 && destinationColumn == 8) || (index == 8 && destinationColumn == 0) || (index == 8 && destinationColumn == 8)) && isKing == false) {
                        return false;
                    }
                }
            } else if (sourceRow < destinationRow) {
                for (int index = sourceRow + 1; index <= destinationRow; index++) {
                    if (board[index][destinationColumn] != 0) {
                        return false;
                    }
                    if (index == 4 && destinationColumn == 4 && isKing == false) {
                        return false;
                    }
                    if (((index == 0 && destinationColumn == 0) || (index == 0 && destinationColumn == 8) || (index == 8 && destinationColumn == 0) || (index == 8 && destinationColumn == 8)) && isKing == false) {
                        return false;
                    }
                }
            }
        } else {
            if (sourceColumn > destinationColumn) {
                for (int index = sourceColumn - 1; index >= destinationColumn; index--) {
                    if (board[destinationRow][index] != 0) {
                        return false;
                    }
                    if (destinationRow == 4 && index == 4 && isKing == false) {
                        return false;
                    }
                    if (((destinationRow == 0 && index == 0) || (destinationRow == 0 && index == 8) || (destinationRow == 8 && index == 0) || (destinationRow == 8 && index == 8)) && isKing == false) {
                        return false;
                    }
                }
            } else if (sourceColumn < destinationColumn) {
                for (int index = sourceColumn + 1; index <= destinationColumn; index++) {
                    if (board[destinationRow][index] != 0) {
                        return false;
                    }
                    if (destinationRow == 4 && index == 4 && isKing == false) {
                        return false;
                    }
                    if (((destinationRow == 0 && index == 0) || (destinationRow == 0 && index == 8) || (destinationRow == 8 && index == 0) || (destinationRow == 8 && index == 8)) && isKing == false) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Moves a piece and captures enemies on the virtual board.
     *
     * @param board The virtual board to update.
     * @param sourceRow The starting row.
     * @param sourceColumn The starting column.
     * @param destinationRow The destination row.
     * @param destinationColumn The destination column.
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
     * Identifies if a piece is an enemy.
     *
     * @param pawnType The type of the piece.
     * @param myColor The color of the current player.
     * @return True if the piece is an enemy, false otherwise.
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
     * Checks if a cell contains an ally or a trap to help capture an enemy.
     *
     * @param board The virtual board.
     * @param row The row to check.
     * @param column The column to check.
     * @param myColor The color of the current player.
     * @return True if an ally or trap is present, false otherwise.
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
     * Verifies if the king is surrounded and eliminated on the virtual board.
     *
     * @param board The virtual board.
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
     * Calculates a score for the board to decide the best move.
     *
     * @param board The virtual board to evaluate.
     * @return The final score of the board.
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
     * Creates an exact duplicate of the virtual board.
     *
     * @param board The board to copy.
     * @return The new duplicated board.
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
