package tablut.view;

import boardifier.view.ElementLook;
import boardifier.view.ConsoleColor;
import tablut.model.ModelPawn;

public class PawnLook extends ElementLook {

    public PawnLook(ModelPawn element) {
        super(element);
        setSize(1, 1);
    }

    @Override
    protected void render() {
        ModelPawn pawn = (ModelPawn) getElement();

        if (pawn.getKing()) {
            shape[0][0] = ConsoleColor.YELLOW_BOLD + "K" + ConsoleColor.RESET; // Roi
        } else if (pawn.getColor() == ModelPawn.PAWN_BLACK) {
            shape[0][0] = ConsoleColor.RED_BOLD + "B" + ConsoleColor.RESET; // Atk
        } else {
            shape[0][0] = ConsoleColor.BLUE_BOLD + "W" + ConsoleColor.RESET; // Def
        }
    }
}