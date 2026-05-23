package tablut.control;

import java.util.Scanner;

import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import boardifier.model.GameElement;
import boardifier.model.Player;
import boardifier.model.action.ActionList;
import boardifier.model.Model;
import tablut.model.ModelBoard;
import tablut.model.ModelPawn;
import tablut.view.View;

/**
 * This class controls the Tablut game.
 * It manages the game loop, the player turns, the computer actions, and the rules of the game.
 */
public class Controller extends boardifier.control.Controller {

    /**
     * The row of the pawn moved by the computer during the last turn.
     */
    private int lastComputerSourceRow = -1;

    /**
     * The column of the pawn moved by the computer during the last turn.
     */
    private int lastComputerSourceColumn = -1;

    /**
     * The row where the computer moved its pawn during the last turn.
     */
    private int lastComputerDestinationRow = -1;

    /**
     * The column where the computer moved its pawn during the last turn.
     */
    private int lastComputerDestinationColumn = -1;

    /**
     * Creates a new controller for the game.
     *
     * @param model The game model that stores the data.
     * @param view  The game view that displays the graphics.
     */
    public Controller(Model model, View view) {
        super(model, view);
        this.setFirstStageName("tablut");
    }

    /**
     * Starts the main loop of the game.
     * It gives the turn to the current player until the game is finished.
     */
    public void stageLoop() {
        Scanner scanner = new Scanner(System.in);

        while (model.isEndStage() == false) {
            update();

            Player currentPlayer = model.getCurrentPlayer();
            System.out.println("Turn: " + currentPlayer.getName());

            if (currentPlayer.getType() == Player.HUMAN) {
                playHumanTurn(scanner);
            } else {
                playComputerTurn();
            }

            model.setNextPlayer();
        }
    }

    /**
     * Asks the human player to type a move.
     * It checks if the move is allowed before playing it on the board.
     *
     * @param scanner The tool used to read the text written by the player on the keyboard.
     */
    private void playHumanTurn(Scanner scanner) {
        boolean validMove = false;

        String lettresValides = "ABCDEFGHI";
        String chiffresValides = "123456789";

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

            if (checkMoveValidity(sourceRow, sourceColumn, destinationRow, destinationColumn) == true) {
                executeMove(sourceRow, sourceColumn, destinationRow, destinationColumn);
                checkCaptures(destinationRow, destinationColumn);

                int playerId = model.getIdPlayer();
                
                if (win() == true) {
                    model.setIdWinner(playerId);
                    
                    update();
                    
                    if (playerId == 0) {
                        System.out.println(model.getCurrentPlayer().getName() + " wins the game (Black)");
                    } else {
                        System.out.println(model.getCurrentPlayer().getName() + " wins the game (White)");
                    }
                    
                    model.stopStage();
                }

                validMove = true;
            } else {
                System.out.println("Invalid move.");
            }
        }
        
    }

    /**
     * Calculates and plays the best move for the computer player.
     * It gives points to every possible move and chooses the move with the highest score.
     */
    private void playComputerTurn() {
        int bestSourceRow = -1;
        int bestSourceColumn = -1;
        int bestDestinationRow = -1;
        int bestDestinationColumn = -1;
        int bestScore = -1;

        int playerId = model.getIdPlayer();
        int myColor = 0;
        
        if (playerId == 0) {
            myColor = ModelPawn.PAWN_BLACK;
        } else {
            myColor = ModelPawn.PAWN_WHITE;
        }

        ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");

        int kingRow = -1;
        int kingColumn = -1;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.isElementAt(row, col) == true) {
                    ModelPawn p = (ModelPawn) board.getElement(row, col);
                    if (p.getKing() == true) {
                        kingRow = row;
                        kingColumn = col;
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
                                
                                if (checkMoveValidity(sourceRowIndex, sourceColumnIndex, destinationRowIndex, destinationColumnIndex) == true) {
                                    
                                    int currentScore = 1;

                                    if (myColor == ModelPawn.PAWN_WHITE && pawn.getKing() == true) {
                                        if (board.isCorner(destinationRowIndex, destinationColumnIndex) == true) {
                                            currentScore = 10000;
                                        } else {
                                            int beforeDistanceToCorner = Math.min(Math.min(sourceRowIndex + sourceColumnIndex, sourceRowIndex + (8 - sourceColumnIndex)), Math.min((8 - sourceRowIndex) + sourceColumnIndex, (8 - sourceRowIndex) + (8 - sourceColumnIndex)));
                                            int afterDistanceToCorner = Math.min(Math.min(destinationRowIndex + destinationColumnIndex, destinationRowIndex + (8 - destinationColumnIndex)), Math.min((8 - destinationRowIndex) + destinationColumnIndex, (8 - destinationRowIndex) + (8 - destinationColumnIndex)));
                                            
                                            if (afterDistanceToCorner < beforeDistanceToCorner) {
                                                currentScore = 50;
                                            }
                                        }
                                    }

                                    if (myColor == ModelPawn.PAWN_WHITE && pawn.getKing() == false) {

                                        if (sourceRowIndex == kingRow || sourceColumnIndex == kingColumn) {

                                            if (destinationRowIndex != kingRow && destinationColumnIndex != kingColumn) {
                                                
                                                int enemyCount = 0;
                                                if (destinationColumnIndex > kingColumn) {
                                                    for (int c = kingColumn + 1; c < 9; c++) {
                                                        if (isEnemyAt(destinationRowIndex, c, myColor) == true) enemyCount++;
                                                    }
                                                } else {
                                                    for (int c = kingColumn - 1; c >= 0; c--) {
                                                        if (isEnemyAt(destinationRowIndex, c, myColor) == true) enemyCount++;
                                                    }
                                                }

                                                currentScore = 40 - (enemyCount * 5); 
                                            }
                                        }
                                    }

                                    if (myColor == ModelPawn.PAWN_BLACK) {
                                        if (isEnemyKingAt(destinationRowIndex - 1, destinationColumnIndex) == true || 
                                            isEnemyKingAt(destinationRowIndex + 1, destinationColumnIndex) == true || 
                                            isEnemyKingAt(destinationRowIndex, destinationColumnIndex - 1) == true || 
                                            isEnemyKingAt(destinationRowIndex, destinationColumnIndex + 1) == true) {
                                            currentScore = 1000;
                                        }
                                    }

                                    if (destinationRowIndex >= 2 && isEnemyAt(destinationRowIndex - 1, destinationColumnIndex, myColor) == true && isAllyAt(destinationRowIndex - 2, destinationColumnIndex, myColor) == true) {
                                        currentScore = 100;
                                    }
                                    if (destinationRowIndex <= 6 && isEnemyAt(destinationRowIndex + 1, destinationColumnIndex, myColor) == true && isAllyAt(destinationRowIndex + 2, destinationColumnIndex, myColor) == true) {
                                        currentScore = 100;
                                    }
                                    if (destinationColumnIndex >= 2 && isEnemyAt(destinationRowIndex, destinationColumnIndex - 1, myColor) == true && isAllyAt(destinationRowIndex, destinationColumnIndex - 2, myColor) == true) {
                                        currentScore = 100;
                                    }
                                    if (destinationColumnIndex <= 6 && isEnemyAt(destinationRowIndex, destinationColumnIndex + 1, myColor) == true && isAllyAt(destinationRowIndex, destinationColumnIndex + 2, myColor) == true) {
                                        currentScore = 100;
                                    }

                                    if (wouldBeCaptured(destinationRowIndex, destinationColumnIndex, myColor, pawn.getKing()) == true) {
                                        currentScore = 0;
                                    }

                                    if (currentScore == 1) {
                                        if (sourceRowIndex == lastComputerDestinationRow && 
                                            sourceColumnIndex == lastComputerDestinationColumn && 
                                            destinationRowIndex == lastComputerSourceRow && 
                                            destinationColumnIndex == lastComputerSourceColumn) {
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

        if (bestScore != -1) {
            lastComputerSourceRow = bestSourceRow;
            lastComputerSourceColumn = bestSourceColumn;
            lastComputerDestinationRow = bestDestinationRow;
            lastComputerDestinationColumn = bestDestinationColumn;

            String lettresValides = "ABCDEFGHI";
            String chiffresValides = "123456789";
            char sourceLetter = lettresValides.charAt(bestSourceColumn);
            char sourceNumber = chiffresValides.charAt(bestSourceRow);
            char destinationLetter = lettresValides.charAt(bestDestinationColumn);
            char destinationNumber = chiffresValides.charAt(bestDestinationRow);
            
            System.out.println(model.getCurrentPlayer().getName() + " moved: " + sourceLetter + sourceNumber + " -> " + destinationLetter + destinationNumber);

            executeMove(bestSourceRow, bestSourceColumn, bestDestinationRow, bestDestinationColumn);
            checkCaptures(bestDestinationRow, bestDestinationColumn);

            if (win() == true) {
                model.setIdWinner(playerId);
                update();
                if (playerId == 0) {
                    System.out.println(model.getCurrentPlayer().getName() + " wins the game (Black)");
                } else {
                    System.out.println(model.getCurrentPlayer().getName() + " wins the game (White)");
                }
                model.stopStage();
            }
        }
    }

    /**
     * Checks if a pawn would be captured if it moves to a specific square.
     * This is used by the computer to avoid losing pawns.
     *
     * @param row     The destination row to check.
     * @param column  The destination column to check.
     * @param myColor The color of the current player.
     * @param isKing  True if the pawn is the king, false otherwise.
     * @return True if the pawn would be captured, false if the pawn is safe.
     */
    private boolean wouldBeCaptured(int row, int column, int myColor, boolean isKing) {
        if (isKing == true) {
            return false;
        }
        if (row >= 1 && row <= 7) {
            if (isEnemyAt(row - 1, column, myColor) == true && isEnemyAt(row + 1, column, myColor) == true) {
                return true;
            }
        }
        if (column >= 1 && column <= 7) {
            if (isEnemyAt(row, column - 1, myColor) == true && isEnemyAt(row, column + 1, myColor) == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the enemy king is located at a specific square.
     *
     * @param row    The row to check.
     * @param column The column to check.
     * @return True if the enemy king is on this square, false otherwise.
     */
    private boolean isEnemyKingAt(int row, int column) {
        if (row >= 0 && row <= 8 && column >= 0 && column <= 8) {
            ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");
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
     * Checks if a move follows all the rules of the game.
     * It verifies that the path is clear and that the destination is correct.
     *
     * @param sourceRow         The starting row of the pawn.
     * @param sourceColumn      The starting column of the pawn.
     * @param destinationRow    The target row for the pawn.
     * @param destinationColumn The target column for the pawn.
     * @return True if the move is allowed, false if the move is forbidden.
     */
    private boolean checkMoveValidity(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn) {

        ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");

        if (board.isEmptyAt(sourceRow, sourceColumn) == true) {
            return false;
        }

        int playerId = model.getIdPlayer();

        ModelPawn pawn = (ModelPawn) board.getElement(sourceRow, sourceColumn);

        if ((playerId == 0) && (pawn.getColor() != ModelPawn.PAWN_BLACK)) {
            return false;
        }

        if ((playerId == 1) && (pawn.getColor() != ModelPawn.PAWN_WHITE)) {
            return false;
        }

        if ((sourceRow != destinationRow) && (sourceColumn != destinationColumn)) {
            return false;
        }

        if (sourceColumn == destinationColumn) {
            if (sourceRow > destinationRow) {
                for (int i = (sourceRow - 1); i >= destinationRow; i--) {
                    if (board.isElementAt(i, destinationColumn)) {
                        return false;
                    } else if ((board.isThrone(i, destinationColumn) == true) && (pawn.getKing() == false)) {
                        return false;
                    } else if ((board.isCorner(i, destinationColumn) == true) && (pawn.getKing() == false)) {
                        return false;
                    }
                }
            } else if (sourceRow < destinationRow) {
                for (int i = (sourceRow + 1); i <= destinationRow; i++) {
                    if (board.isElementAt(i, destinationColumn)) {
                        return false;
                    } else if ((board.isThrone(i, destinationColumn) == true) && (pawn.getKing() == false)) {
                        return false;
                    } else if ((board.isCorner(i, destinationColumn) == true) && (pawn.getKing() == false)) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            if (sourceColumn > destinationColumn) {
                for (int i = (sourceColumn - 1); i >= destinationColumn; i--) {
                    if (board.isElementAt(destinationRow, i)) {
                        return false;
                    } else if ((board.isThrone(destinationRow, i) == true) && (pawn.getKing() == false)) {
                        return false;
                    } else if ((board.isCorner(destinationRow, i) == true) && (pawn.getKing() == false)) {
                        return false;
                    }
                }
            } else if (sourceColumn < destinationColumn) {
                for (int i = (sourceColumn + 1); i <= destinationColumn; i++) {
                    if (board.isElementAt(destinationRow, i)) {
                        return false;
                    } else if ((board.isThrone(destinationRow, i) == true) && (pawn.getKing() == false)) {
                        return false;
                    } else if ((board.isCorner(destinationRow, i) == true) && (pawn.getKing() == false)) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a friendly pawn is located at a specific square.
     *
     * @param destinationRow    The row to check.
     * @param destinationColumn The column to check.
     * @param myColor           The color of the friendly pawns.
     * @return True if an ally is on this square, false otherwise.
     */
    private boolean isAllyAt(int destinationRow, int destinationColumn, int myColor) {

        ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");
        if (board.isElementAt(destinationRow, destinationColumn)) {
            ModelPawn pawn = (ModelPawn) board.getElement(destinationRow, destinationColumn);
            if (pawn.getColor() == myColor) {
                return true;
            }
        }
        return false;

    }

    /**
     * Checks if an enemy pawn is located at a specific square.
     *
     * @param destinationRow    The row to check.
     * @param destinationColumn The column to check.
     * @param myColor           The color of the friendly pawns.
     * @return True if an enemy is on this square, false otherwise.
     */
    private boolean isEnemyAt(int destinationRow, int destinationColumn, int myColor) {

        ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");
        if (board.isElementAt(destinationRow, destinationColumn)) {
            ModelPawn pawn = (ModelPawn) board.getElement(destinationRow, destinationColumn);
            if (pawn.getColor() != myColor) {
                return true;
            }
        }
        return false;

    }

    /**
     * Looks around the new position of a pawn to find and remove trapped enemies.
     * It uses the rules of the game (corners and throne can help to capture).
     *
     * @param destinationRow    The row where the pawn just arrived.
     * @param destinationColumn The column where the pawn just arrived.
     */
    private void checkCaptures(int destinationRow, int destinationColumn) {
        ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");
        ModelPawn ourPawn = (ModelPawn) board.getElement(destinationRow, destinationColumn);

        if (destinationRow >= 2) {
            if (this.isEnemyAt(destinationRow - 1, destinationColumn, ourPawn.getColor()) == true) {
                ModelPawn pawn1 = (ModelPawn) board.getElement(destinationRow - 1, destinationColumn);
                if (this.isAllyAt(destinationRow - 2, destinationColumn, ourPawn.getColor()) == true) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                } else if (board.isCorner(destinationRow - 2, destinationColumn) == true) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                } else if ((board.isThrone(destinationRow - 2, destinationColumn) == true) && (board.isEmptyAt(destinationRow - 2, destinationColumn) == true)) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                }
            }
        }

        if (destinationRow <= 6) {
            if (this.isEnemyAt(destinationRow + 1, destinationColumn, ourPawn.getColor()) == true) {
                ModelPawn pawn1 = (ModelPawn) board.getElement(destinationRow + 1, destinationColumn);
                if (this.isAllyAt(destinationRow + 2, destinationColumn, ourPawn.getColor()) == true) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                } else if (board.isCorner(destinationRow + 2, destinationColumn) == true) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                } else if ((board.isThrone(destinationRow + 2, destinationColumn) == true) && (board.isEmptyAt(destinationRow + 2, destinationColumn) == true)) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                }
            }
        }

        if (destinationColumn >= 2) {
            if (this.isEnemyAt(destinationRow, destinationColumn - 1, ourPawn.getColor()) == true) {
                ModelPawn pawn1 = (ModelPawn) board.getElement(destinationRow, destinationColumn - 1);
                if (this.isAllyAt(destinationRow, destinationColumn - 2, ourPawn.getColor()) == true) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                } else if (board.isCorner(destinationRow, destinationColumn - 2) == true) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                } else if ((board.isThrone(destinationRow, destinationColumn - 2) == true) && (board.isEmptyAt(destinationRow, destinationColumn - 2) == true)) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                }
            }
        }

        if (destinationColumn <= 6) {
            if (this.isEnemyAt(destinationRow, destinationColumn + 1, ourPawn.getColor()) == true) {
                ModelPawn pawn1 = (ModelPawn) board.getElement(destinationRow, destinationColumn + 1);
                if (this.isAllyAt(destinationRow, destinationColumn + 2, ourPawn.getColor()) == true) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                } else if (board.isCorner(destinationRow, destinationColumn + 2) == true) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                } else if ((board.isThrone(destinationRow, destinationColumn + 2) == true) && (board.isEmptyAt(destinationRow, destinationColumn + 2) == true)) {
                    if (pawn1.getKing() == false) {
                        pawn1.removeFromStage();
                    }
                }
            }
        }

        checkKingCapture();
    }

    /**
     * Checks if the king is surrounded and captured.
     * The king must be blocked on all four sides by enemies, the edges of the board, or the throne.
     */
    private void checkKingCapture() {
        ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");
        int kingRow = -1;
        int kingColumn = -1;
        ModelPawn kingPawn = null;

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board.isElementAt(r, c) == true) {
                    ModelPawn pawn = (ModelPawn) board.getElement(r, c);
                    if (pawn.getKing() == true) {
                        kingRow = r;
                        kingColumn = c;
                        kingPawn = pawn;
                    }
                }
            }
        }

        if (kingPawn == null) {
            return;
        }

        boolean blockedUp = false;
        boolean blockedDown = false;
        boolean blockedLeft = false;
        boolean blockedRight = false;

        if (kingRow == 0 || isEnemyAt(kingRow - 1, kingColumn, ModelPawn.PAWN_WHITE) == true || board.isThrone(kingRow - 1, kingColumn) == true) {
            blockedUp = true;
        }
        if (kingRow == 8 || isEnemyAt(kingRow + 1, kingColumn, ModelPawn.PAWN_WHITE) == true || board.isThrone(kingRow + 1, kingColumn) == true) {
            blockedDown = true;
        }
        if (kingColumn == 0 || isEnemyAt(kingRow, kingColumn - 1, ModelPawn.PAWN_WHITE) == true || board.isThrone(kingRow, kingColumn - 1) == true) {
            blockedLeft = true;
        }
        if (kingColumn == 8 || isEnemyAt(kingRow, kingColumn + 1, ModelPawn.PAWN_WHITE) == true || board.isThrone(kingRow, kingColumn + 1) == true) {
            blockedRight = true;
        }

        if (blockedUp == true && blockedDown == true && blockedLeft == true && blockedRight == true) {
            kingPawn.removeFromStage();
        }
    }

    /**
     * Checks if a player has won the game.
     * The black pieces win if the king is captured. The white pieces win if the king reaches a corner.
     *
     * @return True if someone has won the game, false if the game continues.
     */
    private boolean win () {

        int playerId = model.getIdPlayer();

        ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");

        boolean kingAlive = false;

        for (int index1 = 0; index1 < ModelBoard.BOARD_SIZE; index1++) {

            for (int index2 = 0; index2 < ModelBoard.BOARD_SIZE; index2++) {

                if (board.isElementAt(index1, index2) == true) {

                    ModelPawn pawn = (ModelPawn) board.getElement(index1, index2);

                    if (pawn.getKing() == true) {

                        kingAlive = true;

                        if ((board.isCorner(index1, index2) == true) && (playerId == 1) ) {

                            return true;

                        }
                    }
                }

            }

        }

        if ((kingAlive == false) && (playerId == 0)) {
            return true;
        }
        
        return false;

    }

    /**
     * Moves a pawn on the board.
     *
     * @param sourceRow         The starting row of the pawn.
     * @param sourceColumn      The starting column of the pawn.
     * @param destinationRow    The row where the pawn will arrive.
     * @param destinationColumn The column where the pawn will arrive.
     */
    private void executeMove(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn) {
        GameElement pawn = model.getGameStage().getContainer("tablutBoard").getElement(sourceRow, sourceColumn);

        ActionList actions = ActionFactory.generateMoveWithinContainer(model, pawn, destinationRow, destinationColumn);

        ActionPlayer player = new ActionPlayer(model, this, actions);
        player.start();
    }
}
