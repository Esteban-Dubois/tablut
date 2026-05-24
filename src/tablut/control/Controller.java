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
 * Controller manages the main game loop and coordinates players.
 */
public class Controller extends boardifier.control.Controller {

    private int lastComputerSourceRow = -1;
    private int lastComputerSourceColumn = -1;
    private int lastComputerDestinationRow = -1;
    private int lastComputerDestinationColumn = -1;

    /**
     * Constructs the controller.
     *
     * @param model The game model.
     * @param view The graphical view of the game.
     */
    public Controller(Model model, View view) {
        super(model, view);
        this.setFirstStageName("tablut");
    }

    /**
     * Runs the main game loop until the game ends.
     */
    public void stageLoop() {
        Scanner scanner = new Scanner(System.in);

        while (model.isEndStage() == false) {
            update();

            Player currentPlayer = model.getCurrentPlayer();
            System.out.println("Turn: " + currentPlayer.getName());

            ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");
            int playerId = model.getIdPlayer();
            
            int myColor = 0;
            if (playerId == 0) {
                myColor = ModelPawn.PAWN_BLACK;
            } else {
                myColor = ModelPawn.PAWN_WHITE;
            }

            int[] bestMove = new int[4];
            bestMove[0] = -1;

            if (currentPlayer.getType() == Player.HUMAN) {
                HumanPlayer human = new HumanPlayer();
                bestMove = human.getHumanMove(scanner, board, playerId);
            } else if (currentPlayer.getName().contains("Artificial Intelligence 2") == true) {
                Computer2 computer2 = new Computer2();
                bestMove = computer2.getBestMove(board, myColor, lastComputerSourceRow, lastComputerSourceColumn, lastComputerDestinationRow, lastComputerDestinationColumn);
            } else {
                Computer1 computer1 = new Computer1();
                bestMove = computer1.getBestMove(board, myColor, lastComputerSourceRow, lastComputerSourceColumn, lastComputerDestinationRow, lastComputerDestinationColumn);
            }

            if (bestMove[0] != -1) {
                
                if (currentPlayer.getType() != Player.HUMAN) {
                    lastComputerSourceRow = bestMove[0];
                    lastComputerSourceColumn = bestMove[1];
                    lastComputerDestinationRow = bestMove[2];
                    lastComputerDestinationColumn = bestMove[3];
                    
                    String lettresValides = "ABCDEFGHI";
                    String chiffresValides = "123456789";
                    char sourceLetter = lettresValides.charAt(bestMove[1]);
                    char sourceNumber = chiffresValides.charAt(bestMove[0]);
                    char destinationLetter = lettresValides.charAt(bestMove[3]);
                    char destinationNumber = chiffresValides.charAt(bestMove[2]);
                    
                    System.out.println(currentPlayer.getName() + " moved: " + sourceLetter + sourceNumber + " -> " + destinationLetter + destinationNumber);
                }

                executeMove(bestMove[0], bestMove[1], bestMove[2], bestMove[3]);
                checkCaptures(bestMove[2], bestMove[3]);

                if (win() == true) {
                    model.setIdWinner(playerId);
                    update();
                    if (playerId == 0) {
                        System.out.println(currentPlayer.getName() + " wins the game (Black)");
                    } else {
                        System.out.println(currentPlayer.getName() + " wins the game (White)");
                    }
                    model.stopStage();
                }

            }

            model.setNextPlayer();
        }
    }

    /**
     * Executes the visual movement of a piece.
     *
     * @param sourceRow The starting row.
     * @param sourceColumn The starting column.
     * @param destinationRow The target row.
     * @param destinationColumn The target column.
     */
    private void executeMove(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn) {
        GameElement pawn = model.getGameStage().getContainer("tablutBoard").getElement(sourceRow, sourceColumn);
        ActionList actions = ActionFactory.generateMoveWithinContainer(model, pawn, destinationRow, destinationColumn);
        ActionPlayer player = new ActionPlayer(model, this, actions);
        player.start();
    }

    /**
     * Verifies if an ally is located at the given coordinates.
     *
     * @param destinationRow The row to check.
     * @param destinationColumn The column to check.
     * @param myColor The color of the current player.
     * @return True if an ally is found, false otherwise.
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
     * Verifies if an enemy is located at the given coordinates.
     *
     * @param destinationRow The row to check.
     * @param destinationColumn The column to check.
     * @param myColor The color of the current player.
     * @return True if an enemy is found, false otherwise.
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
     * Checks around the destination for any valid captures and removes the dead pieces.
     *
     * @param destinationRow The row of the last move.
     * @param destinationColumn The column of the last move.
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
     * Checks if the king is surrounded and ends the game if true.
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
     * Determines if a player has won the game.
     *
     * @return True if a player has won, false otherwise.
     */
    private boolean win() {
        int playerId = model.getIdPlayer();
        ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");
        boolean kingAlive = false;

        for (int index1 = 0; index1 < ModelBoard.BOARD_SIZE; index1++) {
            for (int index2 = 0; index2 < ModelBoard.BOARD_SIZE; index2++) {
                if (board.isElementAt(index1, index2) == true) {
                    ModelPawn pawn = (ModelPawn) board.getElement(index1, index2);
                    if (pawn.getKing() == true) {
                        kingAlive = true;
                        if ((board.isCorner(index1, index2) == true) && (playerId == 1)) {
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
}
