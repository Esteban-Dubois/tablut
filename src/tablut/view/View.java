package tablut.view;

import boardifier.model.Model;
import boardifier.model.GameStageModel;
import boardifier.model.GameElement;
import boardifier.view.GameStageView;
import tablut.model.ModelBoard;
import tablut.model.ModelPawn;

public class View extends boardifier.view.View {

    public View(Model model) {
        super(model);
    }

    // ASTUCE : On crée la Vue du niveau sous forme de classe interne.
    // Boardifier est content car il a sa GameStageView, et tu respectes
    // ta règle de ne pas créer de fichier supplémentaire !
    public static class TablutStageView extends GameStageView {

        public TablutStageView(String name, GameStageModel gameStageModel) {
            super(name, gameStageModel);
        }

        @Override
        public void createLooks() {
            // 1. On récupère le plateau et on lui crée son Look
            ModelBoard board = (ModelBoard) gameStageModel.getContainer("tablutBoard");
            if (board != null) {
                BoardLook boardLook = new BoardLook(3, 5, board);
                addLook(boardLook);

                // 2. On scanne la grille pour trouver tes pions et leur associer un PawnLook
                for (int row = 0; row < ModelBoard.BOARD_SIZE; row++) {
                    for (int col = 0; col < ModelBoard.BOARD_SIZE; col++) {
                        GameElement element = board.getElement(row, col);

                        if (element instanceof ModelPawn) {
                            addLook(new PawnLook((ModelPawn) element));
                        }
                    }
                }
            }
        }
    }
}