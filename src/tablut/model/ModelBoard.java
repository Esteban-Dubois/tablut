package tablut.model;

import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class ModelBoard extends ContainerElement {

    public static final int BOARD_SIZE = 9;
    public static final int THRONE_ROW = 4;
    public static final int THRONE_COLUMN = 4;

    public ModelBoard(String name, int x, int y, GameStageModel gameStageModel) {
        super(name, x, y, BOARD_SIZE, BOARD_SIZE, gameStageModel);
    }

    public boolean isThrone(int row, int column) {
        return row == THRONE_ROW && column == THRONE_COLUMN;
    }

    public boolean isCorner(int row, int column) {
        if (row == 0 && column == 0) {
            return true;
        }
        if (row == 0 && column == BOARD_SIZE - 1) {
            return true;
        }
        if (row == BOARD_SIZE - 1 && column == 0) {
            return true;
        }
        if (row == BOARD_SIZE - 1 && column == BOARD_SIZE - 1) {
            return true;
        }
        return false;
    }

    public void setValidCells(ModelPawn pawn) {

        resetReachableCells(false);

        java.util.List<Point> valid = computeValidCells(pawn);

        if (valid != null) {

            for (Point p : valid) {

                reachableCells[p.y][p.x] = true;

            }

        }

    }

    public java.util.List<Point> computeValidCells(ModelPawn pawn) {

        List<Point> lst = new ArrayList<>();

        int[] pos = getElementCell(pawn);
        int x = pos[1];
        int y = pos[0];

        // valid cells on the north

        if (y != 0 && isEmptyAt(x, y - 1)) {

            int north = y - 1;

            while (north >= 0 && isEmptyAt(x, north)) {

                if (pawn.getKing() || (x != 4 || north != 4)) {

                    lst.add(new Point(x, north));

                }

                north--;

            }

        }

        // valid cells on the east

        if (x != 8 && isEmptyAt(x + 1, y)) {

            int east = x + 1;

            while (east < 9 && isEmptyAt(east, y)) {

                if (pawn.getKing() || (east != 4 || y != 4)) {

                    lst.add(new Point(east, y));

                }

                east++;

            }

        }

        // valid cells on the south

        if (y != 8 && isEmptyAt(x, y + 1)) {

            int south = y + 1;

            while (south < 9 && isEmptyAt(x, south)) {

                if (pawn.getKing() || (x != 4 || south != 4)) {

                    lst.add(new Point(x, south));

                }

                south++;

            }

        }

        // valid cells on the west

        if (x != 0 && isEmptyAt(x - 1, y)) {

            int west = x - 1;

            while (west > 0 && isEmptyAt(west, y)) {

                if (pawn.getKing() || (west != 4 || y != 4)) {

                    lst.add(new Point(west, y));

                }

                west--;

            }

        }

        return lst;

    }
}