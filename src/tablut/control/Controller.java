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

public class Controller extends boardifier.control.Controller {

    public Controller(Model model, View view) {
        super(model, view);
        this.setFirstStageName("tablut");
    }

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

    private void playHumanTurn(Scanner scanner) {
        boolean validMove = false;

        String lettresValides = "ABCDEFGHI";

        while (validMove == false) {

            int sourceRow = -1;
            int sourceColumn = -1;
            int destinationRow = -1;
            int destinationColumn = -1;

            while (sourceColumn < 0 || sourceColumn > 8) {
                System.out.print("Select pawn column (A-I): ");
                String colInput = scanner.next().toUpperCase();

                sourceColumn = lettresValides.indexOf(colInput);
            }

            while (sourceRow < 0 || sourceRow > 8) {
                System.out.print("Select pawn row (1-9): ");
                sourceRow = scanner.nextInt() - 1;
            }

            while (destinationColumn < 0 || destinationColumn > 8) {
                System.out.print("Select destination column (A-I): ");
                String destColInput = scanner.next().toUpperCase();
                destinationColumn = lettresValides.indexOf(destColInput);
            }

            while (destinationRow < 0 || destinationRow > 8) {
                System.out.print("Select destination row (1-9): ");
                destinationRow = scanner.nextInt() - 1;
            }

            if (checkMoveValidity(sourceRow, sourceColumn, destinationRow, destinationColumn) == true) {
                executeMove(sourceRow, sourceColumn, destinationRow, destinationColumn);
                checkCaptures(destinationRow, destinationColumn);

                int playerId = model.getIdPlayer();
                
                if (win() == true) {
                    model.setIdWinner(playerId);
                    model.stopStage();
                }

                validMove = true;
            } else {
                System.out.println("Invalid move.");
            }
        }
        
    }

    private void playComputerTurn() {
    }

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

    private void checkCaptures(int destinationRow, int destinationColumn) {

        ModelBoard board = (ModelBoard) model.getGameStage().getContainer("tablutBoard");

        ModelPawn ourPawn = (ModelPawn) board.getElement(destinationRow, destinationColumn);

        if (destinationRow >= 2) {

            if (this.isEnemyAt(destinationRow - 1, destinationColumn, ourPawn.getColor()) == true) {

                ModelPawn pawn1 = (ModelPawn) board.getElement(destinationRow - 1, destinationColumn);

                if (this.isAllyAt(destinationRow - 2, destinationColumn, ourPawn.getColor()) == true) {

                    if (pawn1.getKing() == true) {

                        if ((destinationColumn >= 1) && (destinationColumn <= 7)) {

                            if (board.isThrone(destinationRow - 1, destinationColumn) == true) {

                                if (this.isAllyAt(destinationRow - 1, destinationColumn - 1, ourPawn.getColor()) == true) {

                                    if (this.isAllyAt(destinationRow - 1, destinationColumn + 1, ourPawn.getColor()) == true) {

                                        board.removeElement(pawn1);

                                    }
                                }
                            } else if (board.isThrone(destinationRow - 1, destinationColumn - 1) == true) {

                                if (this.isAllyAt(destinationRow - 1, destinationColumn + 1, ourPawn.getColor())) {

                                    board.removeElement(pawn1);

                                }
                            } else if (board.isThrone(destinationRow - 1, destinationColumn + 1) == true) {

                                if (this.isAllyAt(destinationRow - 1, destinationColumn - 1, ourPawn.getColor())) {

                                    board.removeElement(pawn1);

                                }
                            }
                        }
                    } else {

                        board.removeElement(pawn1);

                    }
                } else if (board.isCorner(destinationRow - 2, destinationColumn) == true) {

                    board.removeElement(pawn1);

                } else if ((board.isThrone(destinationRow - 2, destinationColumn) == true) && (board.isElementAt(destinationRow - 2, destinationColumn) == false)) {

                    if (pawn1.getKing() == true) {

                        if ((destinationColumn >= 1) && (destinationColumn <= 7)) {

                            if (isAllyAt(destinationRow - 1, destinationColumn - 1, ourPawn.getColor())) {

                                if (isAllyAt(destinationRow - 1, destinationColumn + 1, ourPawn.getColor())) {

                                    board.removeElement(pawn1);

                                }
                            }
                        }
                    } else {

                        board.removeElement(pawn1);

                    }
                }
            }
        } 
        
        if (destinationRow <= 6) {

            if (this.isEnemyAt(destinationRow + 1, destinationColumn, ourPawn.getColor()) == true) {

                ModelPawn pawn1 = (ModelPawn) board.getElement(destinationRow + 1, destinationColumn);

                if (this.isAllyAt(destinationRow + 2, destinationColumn, ourPawn.getColor()) == true) {

                    if (pawn1.getKing() == true) {

                        if ((destinationColumn >= 1) && (destinationColumn <= 7)) {

                            if (board.isThrone(destinationRow + 1, destinationColumn) == true) {

                                if (this.isAllyAt(destinationRow + 1, destinationColumn - 1, ourPawn.getColor()) == true) {

                                    if (this.isAllyAt(destinationRow + 1, destinationColumn + 1, ourPawn.getColor()) == true) {

                                        board.removeElement(pawn1);

                                    }
                                }
                            } else if (board.isThrone(destinationRow + 1, destinationColumn - 1) == true) {

                                if (this.isAllyAt(destinationRow + 1, destinationColumn + 1, ourPawn.getColor())) {

                                    board.removeElement(pawn1);

                                }
                            } else if (board.isThrone(destinationRow + 1, destinationColumn + 1) == true) {

                                if (this.isAllyAt(destinationRow + 1, destinationColumn - 1, ourPawn.getColor())) {

                                    board.removeElement(pawn1);

                                }
                            }
                        }
                    } else {

                        board.removeElement(pawn1);

                    }
                } else if (board.isCorner(destinationRow + 2, destinationColumn) == true) {

                    board.removeElement(pawn1);

                } else if ((board.isThrone(destinationRow + 2, destinationColumn) == true) && (board.isElementAt(destinationRow + 2, destinationColumn) == false)) {

                    if (pawn1.getKing() == true) {

                        if ((destinationColumn >= 1) && (destinationColumn <= 7)) {

                            if (isAllyAt(destinationRow + 1, destinationColumn - 1, ourPawn.getColor())) {

                                if (isAllyAt(destinationRow + 1, destinationColumn + 1, ourPawn.getColor())) {

                                    board.removeElement(pawn1);

                                }
                            }
                        }
                    } else {

                        board.removeElement(pawn1);

                    }
                }
            }
        }
        
        if (destinationColumn >= 2) {

            if (this.isEnemyAt(destinationRow, destinationColumn - 1, ourPawn.getColor()) == true) {

                ModelPawn pawn1 = (ModelPawn) board.getElement(destinationRow, destinationColumn - 1);

                if (this.isAllyAt(destinationRow, destinationColumn - 2, ourPawn.getColor()) == true) {

                    if (pawn1.getKing() == true) {

                        if ((destinationRow >= 1) && (destinationRow <= 7)) {

                            if (board.isThrone(destinationRow, destinationColumn - 1) == true) {

                                if (this.isAllyAt(destinationRow - 1, destinationColumn - 1, ourPawn.getColor()) == true) {

                                    if (this.isAllyAt(destinationRow + 1, destinationColumn - 1, ourPawn.getColor()) == true) {

                                        board.removeElement(pawn1);

                                    }
                                }
                            } else if (board.isThrone(destinationRow - 1, destinationColumn - 1) == true) {

                                if (this.isAllyAt(destinationRow + 1, destinationColumn - 1, ourPawn.getColor()) == true) {

                                    board.removeElement(pawn1);

                                }
                            } else if (board.isThrone(destinationRow + 1, destinationColumn - 1) == true) {

                                if (this.isAllyAt(destinationRow - 1, destinationColumn - 1, ourPawn.getColor()) == true) {

                                    board.removeElement(pawn1);

                                }
                            }
                        }
                    } else {

                        board.removeElement(pawn1);

                    }
                } else if (board.isCorner(destinationRow, destinationColumn - 2) == true) {

                    board.removeElement(pawn1);

                } else if ((board.isThrone(destinationRow, destinationColumn - 2) == true) && (board.isEmptyAt(destinationRow, destinationColumn - 2) == true)) {

                    if (pawn1.getKing() == true) {

                        if ((destinationRow >= 1) && (destinationRow <= 7)) {

                            if (this.isAllyAt(destinationRow - 1, destinationColumn - 1, ourPawn.getColor()) == true) {

                                if (this.isAllyAt(destinationRow + 1, destinationColumn - 1, ourPawn.getColor()) == true) {

                                    board.removeElement(pawn1);

                                }
                            }
                        }
                    } else {

                        board.removeElement(pawn1);

                    }
                }
            }
        }
        
        if (destinationColumn <= 6) {

            if (this.isEnemyAt(destinationRow, destinationColumn + 1, ourPawn.getColor()) == true) {

                ModelPawn pawn1 = (ModelPawn) board.getElement(destinationRow, destinationColumn + 1);

                if (this.isAllyAt(destinationRow, destinationColumn + 2, ourPawn.getColor()) == true) {

                    if (pawn1.getKing() == true) {

                        if ((destinationRow >= 1) && (destinationRow <= 7)) {

                            if (board.isThrone(destinationRow, destinationColumn + 1) == true) {

                                if (this.isAllyAt(destinationRow - 1, destinationColumn + 1, ourPawn.getColor()) == true) {

                                    if (this.isAllyAt(destinationRow + 1, destinationColumn + 1, ourPawn.getColor()) == true) {

                                        board.removeElement(pawn1);

                                    }
                                }
                            } else if (board.isThrone(destinationRow - 1, destinationColumn + 1) == true) {

                                if (this.isAllyAt(destinationRow + 1, destinationColumn + 1, ourPawn.getColor()) == true) {

                                    board.removeElement(pawn1);

                                }
                            } else if (board.isThrone(destinationRow + 1, destinationColumn + 1) == true) {

                                if (this.isAllyAt(destinationRow - 1, destinationColumn + 1, ourPawn.getColor()) == true) {

                                    board.removeElement(pawn1);

                                }
                            }
                        }
                    } else {

                        board.removeElement(pawn1);

                    }

                } else if (board.isCorner(destinationRow, destinationColumn + 2) == true) {

                    board.removeElement(pawn1);

                } else if ((board.isThrone(destinationRow, destinationColumn + 2) == true) && (board.isEmptyAt(destinationRow, destinationColumn + 2) == true)) {

                    if (pawn1.getKing() == true) {

                        if ((destinationRow >= 1) && (destinationRow <= 7)) {

                            if (this.isAllyAt(destinationRow - 1, destinationColumn + 1, ourPawn.getColor()) == true) {

                                if (this.isAllyAt(destinationRow + 1, destinationColumn + 1, ourPawn.getColor()) == true) {

                                    board.removeElement(pawn1);

                                }
                            }
                        }
                    } else {

                        board.removeElement(pawn1);

                    }
                }
            }
        }
    }

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

    private void executeMove(int sourceRow, int sourceColumn, int destinationRow, int destinationColumn) {
        GameElement pawn = model.getGameStage().getContainer("tablutBoard").getElement(sourceRow, sourceColumn);

        ActionList actions = ActionFactory.generateMoveWithinContainer(model, pawn, destinationRow, destinationColumn);

        ActionPlayer player = new ActionPlayer(model, this, actions);
        player.start();
    }
}
