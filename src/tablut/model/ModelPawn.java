package tablut.model;

import boardifier.model.GameElement;
import boardifier.model.GameStageModel;

public class ModelPawn extends GameElement {

    public static final int PAWN_BLACK = 1; // moscuvites
    public static final int PAWN_WHITE = 2; // swedes and the king
    
    private int color;
    private boolean king;


    public ModelPawn (GameStageModel gsm) { super(gsm); };

    public ModelPawn (GameStageModel gsm, int color, boolean king) {
        super(gsm);
        this.color = color;
        this.king = king;
    }

    public void setColor (int color) {
        this.color = color;
    }

    public void setKing (boolean king) {
        this.king = king;
    }

    public int getColor () {
        return this.color;
    }

    public boolean getKing () {
        return this.king;
    }

    public boolean isAlly(ModelPawn pawn) {
        return pawn.getColor() == this.getColor();
    }

}
