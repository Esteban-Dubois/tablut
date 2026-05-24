package tablut.control;

import java.util.Scanner;
import tablut.model.ModelBoard;
import tablut.model.ModelPawn;

/**
 * Handles all input from a human player during a Tablut game.
 * <p>
 * This class reads moves from the keyboard, validates the format,
 * checks that the selected pawn belongs to the current player,
 * and verifies that the move is legal according to the game rules.
 * </p>
 */
public class HumanPlayer {

    /**
     * Asks the human player to enter a move and returns it once it is valid.
     * <p>
     * The player must type a source cell (e.g. {@code A1}) and a destination cell
     * (e.g. {@code D1}). The method keeps asking until a fully legal move is entered.
     * </p>
     *
     * @param scanner  the {@link Scanner} used to read keyboard input
     * @param board    the current game board
     * @param playerId the identifier of the current player (0 for black, 1 for white)
     * @return an array of four integers: {@code [sourceRow, sourceColumn, destinationRow, destinationColumn]}
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
