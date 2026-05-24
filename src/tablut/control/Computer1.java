package tablut.control;

import tablut.model.ModelBoard;
import tablut.model.ModelPawn;

/**
 * Greedy AI player for the Tablut game.
 * <p>
 * This AI scores every legal move and picks the one with the highest score.
 * It does not look ahead; it only evaluates the immediate result of each move.
 * Score values used:
 * </p>
 * <ul>
 *   <li>10 000 – King reaches a corner (white wins immediately).</li>
 *   <li>1 000 – Black pawn lands adjacent to the King.</li>
 *   <li>100 – A custodian capture of an enemy pawn is possible.</li>
 *   <li>50 – King moves closer to a corner.</li>
 *   <li>40 (minus 5 per nearby enemy) – White guard clears a path for the King.</li>
 *   <li>1 – Any other legal move.</li>
 *   <li>0 – Move is suicidal or exactly reverses the previous move.</li>
 * </ul>
 */
public class Computer1 {

    /**
     * Finds and returns the best move for the given color using a greedy strategy.
     * <p>
     * The method scans every piece that belongs to {@code myColor}, evaluates all
     * legal destinations for each piece, and keeps the move with the highest score.
     * The last move coordinates are used to prevent the AI from immediately
     * reversing its previous move.
     * </p>
     *
     * @param board                the current game board
     * @param myColor              the color of this AI ({@code ModelPawn.PAWN_BLACK} or {@code ModelPawn.PAWN_WHITE})
     * @param lastSourceRow        the source row of the previous move (used to avoid back-and-forth)
     * @param lastSourceColumn     the source column of the previous move
     * @param lastDestinationRow   the destination row of the previous move
     * @param lastDestinationColumn the destination column of the previous move
     * @return an array of four integers: {@code [sourceRow, sourceColumn, destinationRow, destinationColumn]}
     */
    public int[] getBestMove(ModelBoard board, int myColor, int lastSourceRow, int lastSourceColumn, int lastDestinationRow, int lastDestinationColumn) {

        int bestSourceRow = -1;
        int bestSourceColumn = -1;
        int bestDestinationRow = -1;
        int bestDestinationColumn = -1;
        int bestScore = -1;

        int kingRow = -1;
        int kingColumn = -1;

        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (board.isElementAt(row, column) == true) {
                    ModelPawn pawn = (ModelPawn) board.getElement(row, column);
                    if (pawn.getKing() == true) {
                        kingRow = row;
                        kingColumn = column;
                    }
                }
            }
        }

        for (int sourceRowIndex = 0; sourceRowIndex < 9; sourceRowIndex++) {
            for (int sourceColumnIndex = 0; sourceColumnIndex < 9; sourceColumnIndex++) {

                if (board.isElementAt(sourceRowIndex, sourceColumnIndex) == true) {
                    ModelPawn pawn = (ModelPawn) board.getElement(sourceRowIndex, sourceColumnIndex);

                    if (pawn.getColor() == myColor) {

                        for (int destinationRowIndex = 0; destinationRowIndex < 9; destinationRowIndex++) {
                            for (int destinationColumnIndex = 0; destinationColumnIndex < 9; destinationColumnIndex++) {

                                if (Rules.checkMoveValidity(board, sourceRowIndex, sourceColumnIndex, destinationRowIndex, destinationColumnIndex, pawn.getKing()) == true) {

                                    int currentScore = 1;

                                    // White King move scoring
                                    if (myColor == ModelPawn.PAWN_WHITE) {
                                        if (pawn.getKing() == true) {
                                            if (board.isCorner(destinationRowIndex, destinationColumnIndex) == true) {
                                                currentScore = 10000;
                                            } else {
                                                int distanceLigneAvant = sourceRowIndex;
                                                int distanceColonneAvant = sourceColumnIndex;
                                                int distanceLigneApres = destinationRowIndex;
                                                int distanceColonneApres = destinationColumnIndex;

                                                int distanceAvant = Math.min(Math.min(distanceLigneAvant + distanceColonneAvant, distanceLigneAvant + (8 - distanceColonneAvant)), Math.min((8 - distanceLigneAvant) + distanceColonneAvant, (8 - distanceLigneAvant) + (8 - distanceColonneAvant)));
                                                int distanceApres = Math.min(Math.min(distanceLigneApres + distanceColonneApres, distanceLigneApres + (8 - distanceColonneApres)), Math.min((8 - distanceLigneApres) + distanceColonneApres, (8 - distanceLigneApres) + (8 - distanceColonneApres)));

                                                if (distanceApres < distanceAvant) {
                                                    currentScore = 50;
                                                }
                                            }
                                        }
                                    }

                                    // White guard clearing a path for the King
                                    if (myColor == ModelPawn.PAWN_WHITE) {
                                        if (pawn.getKing() == false) {
                                            if (sourceRowIndex == kingRow || sourceColumnIndex == kingColumn) {
                                                if (destinationRowIndex != kingRow && destinationColumnIndex != kingColumn) {

                                                    int enemyCount = 0;
                                                    if (destinationColumnIndex > kingColumn) {
                                                        for (int compteur = kingColumn + 1; compteur < 9; compteur++) {
                                                            if (isEnemyAt(board, destinationRowIndex, compteur, myColor) == true) {
                                                                enemyCount = enemyCount + 1;
                                                            }
                                                        }
                                                    } else {
                                                        for (int compteur = kingColumn - 1; compteur >= 0; compteur--) {
                                                            if (isEnemyAt(board, destinationRowIndex, compteur, myColor) == true) {
                                                                enemyCount = enemyCount + 1;
                                                            }
                                                        }
                                                    }

                                                    currentScore = 40 - (enemyCount * 5);
                                                }
                                            }
                                        }
                                    }

                                    // Black pawn threatening the King
                                    if (myColor == ModelPawn.PAWN_BLACK) {
                                        boolean roiEnHaut = isEnemyKingAt(board, destinationRowIndex - 1, destinationColumnIndex);
                                        boolean roiEnBas = isEnemyKingAt(board, destinationRowIndex + 1, destinationColumnIndex);
                                        boolean roiAGauche = isEnemyKingAt(board, destinationRowIndex, destinationColumnIndex - 1);
                                        boolean roiADroite = isEnemyKingAt(board, destinationRowIndex, destinationColumnIndex + 1);

                                        if (roiEnHaut == true || roiEnBas == true || roiAGauche == true || roiADroite == true) {
                                            currentScore = 1000;
                                        }
                                    }

                                    // Custodian capture detection (all four directions)
                                    boolean ennemiEnHaut = isEnemyAt(board, destinationRowIndex - 1, destinationColumnIndex, myColor);
                                    boolean allieAuDessusEnnemi = isAllyAt(board, destinationRowIndex - 2, destinationColumnIndex, myColor);
                                    if (destinationRowIndex >= 2 && ennemiEnHaut == true && allieAuDessusEnnemi == true) {
                                        currentScore = 100;
                                    }

                                    boolean ennemiEnBas = isEnemyAt(board, destinationRowIndex + 1, destinationColumnIndex, myColor);
                                    boolean allieEnDessousEnnemi = isAllyAt(board, destinationRowIndex + 2, destinationColumnIndex, myColor);
                                    if (destinationRowIndex <= 6 && ennemiEnBas == true && allieEnDessousEnnemi == true) {
                                        currentScore = 100;
                                    }

                                    boolean ennemiAGauche = isEnemyAt(board, destinationRowIndex, destinationColumnIndex - 1, myColor);
                                    boolean allieAGaucheEnnemi = isAllyAt(board, destinationRowIndex, destinationColumnIndex - 2, myColor);
                                    if (destinationColumnIndex >= 2 && ennemiAGauche == true && allieAGaucheEnnemi == true) {
                                        currentScore = 100;
                                    }

                                    boolean ennemiADroite = isEnemyAt(board, destinationRowIndex, destinationColumnIndex + 1, myColor);
                                    boolean allieADroiteEnnemi = isAllyAt(board, destinationRowIndex, destinationColumnIndex + 2, myColor);
                                    if (destinationColumnIndex <= 6 && ennemiADroite == true && allieADroiteEnnemi == true) {
                                        currentScore = 100;
                                    }

                                    // Suicide prevention
                                    boolean dangerSuicide = wouldBeCaptured(board, destinationRowIndex, destinationColumnIndex, myColor, pawn.getKing());
                                    if (dangerSuicide == true) {
                                        currentScore = 0;
                                    }

                                    // Back-and-forth prevention
                                    if (currentScore == 1) {
                                        boolean memeLigneDepart = (sourceRowIndex == lastDestinationRow);
                                        boolean memeColonneDepart = (sourceColumnIndex == lastDestinationColumn);
                                        boolean memeLigneArrivee = (destinationRowIndex == lastSourceRow);
                                        boolean memeColonneArrivee = (destinationColumnIndex == lastSourceColumn);

                                        if (memeLigneDepart == true && memeColonneDepart == true && memeLigneArrivee == true && memeColonneArrivee == true) {
                                            currentScore = 0;
                                        }
                                    }

                                    if (currentScore > bestScore) {
                                        bestScore = currentScore;
                                        bestSourceRow = sourceRowIndex;
                                        bestSourceColumn = sourceColumnIndex;
                                        bestDestinationRow = destinationRowIndex;
                                        bestDestinationColumn = destinationColumnIndex;
                                    }

                                }
                            }
                        }

                    }
                }
            }
        }

        int[] resultat = new int[4];
        resultat[0] = bestSourceRow;
        resultat[1] = bestSourceColumn;
        resultat[2] = bestDestinationRow;
        resultat[3] = bestDestinationColumn;
        return resultat;
    }

    /**
     * Returns {@code true} if a friendly pawn is on the given cell.
     *
     * @param board   the current game board
     * @param row     the row to check
     * @param column  the column to check
     * @param myColor the color of the current player
     * @return {@code true} if there is an ally on that cell, {@code false} otherwise
     */
    private boolean isAllyAt(ModelBoard board, int row, int column, int myColor) {
        if (row < 0 || row > 8 || column < 0 || column > 8) {
            return false;
        }
        if (board.isElementAt(row, column) == true) {
            ModelPawn pawn = (ModelPawn) board.getElement(row, column);
            if (pawn.getColor() == myColor) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if an enemy pawn is on the given cell.
     *
     * @param board   the current game board
     * @param row     the row to check
     * @param column  the column to check
     * @param myColor the color of the current player
     * @return {@code true} if there is an enemy on that cell, {@code false} otherwise
     */
    private boolean isEnemyAt(ModelBoard board, int row, int column, int myColor) {
        if (row < 0 || row > 8 || column < 0 || column > 8) {
            return false;
        }
        if (board.isElementAt(row, column) == true) {
            ModelPawn pawn = (ModelPawn) board.getElement(row, column);
            if (pawn.getColor() != myColor) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the King is on the given cell.
     *
     * @param board  the current game board
     * @param row    the row to check
     * @param column the column to check
     * @return {@code true} if the King is on that cell, {@code false} otherwise
     */
    private boolean isEnemyKingAt(ModelBoard board, int row, int column) {
        if (row >= 0 && row <= 8 && column >= 0 && column <= 8) {
            if (board.isElementAt(row, column) == true) {
                ModelPawn pawn = (ModelPawn) board.getElement(row, column);
                if (pawn.getKing() == true) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if a pawn would be immediately captured on the given cell.
     * <p>
     * A pawn is considered in danger if it has an enemy on both sides vertically
     * or on both sides horizontally. This check is skipped for the King because
     * his capture rules are more complex.
     * </p>
     *
     * @param board   the current game board
     * @param row     the row of the cell to check
     * @param column  the column of the cell to check
     * @param myColor the color of the pawn being evaluated
     * @param isKing  {@code true} if the pawn is the King (check is skipped)
     * @return {@code true} if the cell is dangerous, {@code false} otherwise
     */
    private boolean wouldBeCaptured(ModelBoard board, int row, int column, int myColor, boolean isKing) {
        if (isKing == true) {
            return false;
        }
        if (row >= 1 && row <= 7) {
            boolean ennemiAuDessus = isEnemyAt(board, row - 1, column, myColor);
            boolean ennemiEnDessous = isEnemyAt(board, row + 1, column, myColor);
            if (ennemiAuDessus == true && ennemiEnDessous == true) {
                return true;
            }
        }
        if (column >= 1 && column <= 7) {
            boolean ennemiAGauche = isEnemyAt(board, row, column - 1, myColor);
            boolean ennemiADroite = isEnemyAt(board, row, column + 1, myColor);
            if (ennemiAGauche == true && ennemiADroite == true) {
                return true;
            }
        }
        return false;
    }

}
