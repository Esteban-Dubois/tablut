package tablut.control;

import java.util.Scanner;
import tablut.model.ModelBoard;
import tablut.model.ModelPawn;

/**
 * HumanPlayer handles keyboard inputs for a human player.
 */
public class HumanPlayer {

    /**
     * Asks the human player for a valid move via the console.
     *
     * @param scanner The scanner used to read user input.
     * @param board The current game board.
     * @param playerId The ID of the current player.
     * @return An array containing the move coordinates.
     */
    public int[] getHumanMove(Scanner scanner, ModelBoard board, int playerId) {
        int[] resultat = new int[4];
        boolean validMove = false;

        String lettresValides = "ABCDEFGHI";
        String chiffresValides = "123456789";

        int myColor = 0;
        if (playerId == 0) {
            myColor = ModelPawn.PAWN_BLACK;
        } else {
            myColor = ModelPawn.PAWN_WHITE;
        }

        while (validMove == false) {

            int sourceRow = -1;
            int sourceColumn = -1;
            int destinationRow = -1;
            int destinationColumn = -1;

            while (sourceColumn < 0 || sourceColumn > 8 || sourceRow < 0 || sourceRow > 8) {
                System.out.print("Select pawn to move (example: A1): ");
                String sourceInput = scanner.next().toUpperCase();

                if (sourceInput.length() >= 2) {
                    sourceColumn = lettresValides.indexOf(sourceInput.charAt(0));
                    sourceRow = chiffresValides.indexOf(sourceInput.charAt(1));
                } else {
                    System.out.println("Invalid format. Please use a letter followed by a number.");
                }
            }

            while (destinationColumn < 0 || destinationColumn > 8 || destinationRow < 0 || destinationRow > 8) {
                System.out.print("Select destination (example: D1): ");
                String destinationInput = scanner.next().toUpperCase();

                if (destinationInput.length() >= 2) {
                    destinationColumn = lettresValides.indexOf(destinationInput.charAt(0));
                    destinationRow = chiffresValides.indexOf(destinationInput.charAt(1));
                } else {
                    System.out.println("Invalid format. Please use a letter followed by a number.");
                }
            }

            if (board.isEmptyAt(sourceRow, sourceColumn) == true) {
                System.out.println("Invalid move: There is no pawn here.");
            } else {
                ModelPawn pawn = (ModelPawn) board.getElement(sourceRow, sourceColumn);
                
                if (pawn.getColor() != myColor) {
                    System.out.println("Invalid move: This is not your pawn.");
                } else {
                    if (Rules.checkMoveValidity(board, sourceRow, sourceColumn, destinationRow, destinationColumn, pawn.getKing()) == true) {
                        resultat[0] = sourceRow;
                        resultat[1] = sourceColumn;
                        resultat[2] = destinationRow;
                        resultat[3] = destinationColumn;
                        validMove = true;
                    } else {
                        System.out.println("Invalid move.");
                    }
                }
            }
        }

        return resultat;
    }
}
