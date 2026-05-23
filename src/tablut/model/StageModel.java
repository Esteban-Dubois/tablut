package tablut.model;

import boardifier.model.*;

public class StageModel extends GameStageModel {

    private ModelBoard board;
    private ModelPawn[] blackPawns;
    private ModelPawn[] whitePawns;
    private TextElement playerName;

    public static final int MODE_PLAYER_VS_PLAYER = 1;
    public static final int MODE_PLAYER_VS_AI_1 = 2;
    public static final int MODE_PLAYER_VS_AI_2 = 3;

    private int gameMode;

    public StageModel(String name, Model model) {
        super(name, model);
        this.gameMode = MODE_PLAYER_VS_PLAYER;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public int getGameMode() {
        return this.gameMode;
    }

    public void initializePlayers(String playerOneName, String playerTwoName) {
        this.model.getPlayers().clear();
        
        this.model.addHumanPlayer(playerOneName);

        if (this.gameMode == MODE_PLAYER_VS_PLAYER) {
            this.model.addHumanPlayer(playerTwoName);
        } else if (this.gameMode == MODE_PLAYER_VS_AI_1) {
            this.model.addComputerPlayer("AI 1");
        } else if (this.gameMode == MODE_PLAYER_VS_AI_2) {
            this.model.addComputerPlayer("AI 2");
        }
    }

    public ModelBoard getBoard() {
        return board;
    }
    
    public void setBoard(ModelBoard board) {
        this.board = board;
        addContainer(board);
    }

    public ModelPawn[] getBlackPawns() {
        return blackPawns;
    }
    
    public void setBlackPawns(ModelPawn[] blackPawns) {
        this.blackPawns = blackPawns;
        for(int i = 0; i < blackPawns.length; i++) {
            addElement(blackPawns[i]);
        }
    }

    public ModelPawn[] getWhitePawns() {
        return whitePawns;
    }
    
    public void setWhitePawns(ModelPawn[] whitePawns) {
        this.whitePawns = whitePawns;
        for(int i = 0; i < whitePawns.length; i++) {
            addElement(whitePawns[i]);
        }
    }
    
    public TextElement getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(TextElement playerName) {
        this.playerName = playerName;
        addElement(playerName);
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new StageFactory(this);
    }
}