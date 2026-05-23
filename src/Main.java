import java.util.Scanner;
import boardifier.control.StageFactory;
import boardifier.model.Model;
import tablut.view.View;
import tablut.control.Controller;

/**
 * This class is the starting point of the Tablut game application.
 * It configures the game settings and starts the program.
 */
public class Main {

    /**
     * The main method that starts the application.
     * It asks the user to choose a game mode and a player color, creates the game components, and starts the game loop.
     *
     * @param arguments The command line arguments passed to the program.
     * @throws Exception If an error happens during the setup of the game.
     */
    public static void main(String[] arguments) throws Exception {
        System.out.println("=== LANCEMENT DU TABLUT (VERSION TEXTE) ===");

        Scanner inputScanner = new Scanner(System.in);
        Model model = new Model();

        int gameModeSelection = 0;
        while (gameModeSelection < 1 || gameModeSelection > 3) {
            System.out.println("Select Game Mode:");
            System.out.println("1 - Player vs Player");
            System.out.println("2 - Player vs Artificial Intelligence 1");
            System.out.println("3 - Player vs Artificial Intelligence 2");
            System.out.print("Your choice (1-3): ");

            if (inputScanner.hasNextInt()) {
                gameModeSelection = inputScanner.nextInt();
            } else {
                inputScanner.next();
            }
        }

        int playerColorSelection = 0;
        while (playerColorSelection < 1 || playerColorSelection > 2) {
            System.out.println("Select your color:");
            System.out.println("1 - Black (Attackers, play first)");
            System.out.println("2 - White (Defenders)");
            System.out.print("Your choice (1-2): ");

            if (inputScanner.hasNextInt()) {
                playerColorSelection = inputScanner.nextInt();
            } else {
                inputScanner.next();
            }
        }

        if (gameModeSelection == 1) {
            if (playerColorSelection == 1) {
                model.addHumanPlayer("Player 1 (Black)");
                model.addHumanPlayer("Player 2 (White)");
            } else {
                model.addHumanPlayer("Player 2 (Black)");
                model.addHumanPlayer("Player 1 (White)");
            }
        } else if (gameModeSelection == 2) {
            if (playerColorSelection == 1) {
                model.addHumanPlayer("Player (Black)");
                model.addComputerPlayer("Artificial Intelligence 1 (White)");
            } else {
                model.addComputerPlayer("Artificial Intelligence 1 (Black)");
                model.addHumanPlayer("Player (White)");
            }
        } else {
            if (playerColorSelection == 1) {
                model.addHumanPlayer("Player (Black)");
                model.addComputerPlayer("Artificial Intelligence 2 (White)");
            } else {
                model.addComputerPlayer("Artificial Intelligence 2 (Black)");
                model.addHumanPlayer("Player (White)");
            }
        }

        View view = new View(model);
        Controller controller = new Controller(model, view);

        StageFactory.registerModelAndView(
                "tablut",
                "tablut.model.StageModel",
                "tablut.view.View$TablutStageView"
        );

        controller.startGame();
        controller.stageLoop();
    }
}
