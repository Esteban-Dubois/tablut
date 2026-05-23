package tablut.view;

import boardifier.view.ClassicBoardLook;
import boardifier.view.ConsoleColor;
import tablut.model.ModelBoard;

public class BoardLook extends ClassicBoardLook {

    public BoardLook(int rowHeight, int colWidth, ModelBoard element) {
        super(rowHeight, colWidth, element, -1, 1, true);
    }

    @Override
    protected void render() {
        super.render();

        ModelBoard board = (ModelBoard) getElement();

        for (int i = 0; i < ModelBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < ModelBoard.BOARD_SIZE; j++) {
                if (board.isThrone(i, j)) {
                    colorCellBackground(i, j, ConsoleColor.PURPLE_BACKGROUND); // Trone en violet
                } else if (board.isCorner(i, j)) {
                    colorCellBackground(i, j, ConsoleColor.CYAN_BACKGROUND);   // 4 coins en cyan
                }
            }
        }
    }

    private void colorCellBackground(int row, int col, String colorBackground) {
        int xStart = getCellLeft(row, col);
        int xEnd = getCellRight(row, col);
        int yStart = getCellTop(row, col);
        int yEnd = getCellBottom(row, col);

        if (xStart == -1 || yStart == -1) return;

        for (int y = yStart; y <= yEnd; y++) {
            for (int x = xStart; x <= xEnd; x++) {
                String currentCharacter = shape[innersTop + y][innersLeft + x];
                if (currentCharacter == null || currentCharacter.equals(" ")) {
                    shape[innersTop + y][innersLeft + x] = colorBackground + " " + ConsoleColor.RESET;
                } else {
                    shape[innersTop + y][innersLeft + x] = colorBackground + currentCharacter + ConsoleColor.RESET;
                }
            }
        }
    }
}
