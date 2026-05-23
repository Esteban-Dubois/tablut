package tablut.model;



import boardifier.model.*;

class StageFactory extends StageElementsFactory {

    private StageModel stageModel;

    public StageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (StageModel) gameStageModel;
    }

    @Override
    public void setup() {
        TextElement text = new TextElement(stageModel.getCurrentPlayerName(), stageModel);
        text.setLocation(0, 0);
        stageModel.setPlayerName(text);

        ModelBoard board = new ModelBoard("tablutBoard", 0, 1, stageModel);
        stageModel.setBoard(board);

        ModelPawn[] blackPawns = new ModelPawn[16];
        for (int i = 0; i < 16; i++) {
            blackPawns[i] = new ModelPawn(stageModel, 1, false);
        }
        stageModel.setBlackPawns(blackPawns);
        board.addElement(blackPawns[0], 0, 3);
        board.addElement(blackPawns[1], 0, 4);
        board.addElement(blackPawns[2], 0, 5);
        board.addElement(blackPawns[3], 1, 4);

        board.addElement(blackPawns[4], 3, 8);
        board.addElement(blackPawns[5], 4, 8);
        board.addElement(blackPawns[6], 5, 8);
        board.addElement(blackPawns[7], 4, 7);

        board.addElement(blackPawns[8], 8, 3);
        board.addElement(blackPawns[9], 8, 4);
        board.addElement(blackPawns[10], 8, 5);
        board.addElement(blackPawns[11], 7, 4);

        board.addElement(blackPawns[12], 3, 0);
        board.addElement(blackPawns[13], 4, 0);
        board.addElement(blackPawns[14], 5, 0);
        board.addElement(blackPawns[15], 4, 1);

        ModelPawn[] whitePawns = new ModelPawn[9];
        for (int i = 0; i < 8; i++) {
            whitePawns[i] = new ModelPawn(stageModel, 2, false);
        }
        whitePawns[8] = new ModelPawn(stageModel, 2, true);
        stageModel.setWhitePawns(whitePawns);

        board.addElement(whitePawns[0], 2, 4);
        board.addElement(whitePawns[1], 3, 4);

        board.addElement(whitePawns[2], 4, 5);
        board.addElement(whitePawns[3], 4, 6);

        board.addElement(whitePawns[4], 5, 4);
        board.addElement(whitePawns[5], 6, 4);

        board.addElement(whitePawns[6], 4, 2);
        board.addElement(whitePawns[7], 4, 3);

        board.addElement(whitePawns[8], 4, 4); // the king

    }

    

}