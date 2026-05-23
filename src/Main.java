import boardifier.control.StageFactory;
import boardifier.model.Model;
import tablut.view.View;
import tablut.control.Controller;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("=== LANCEMENT DU TABLUT (VERSION TEXTE) ===");

        Model model = new Model();
        model.addHumanPlayer("Jarod (Noirs)");
        model.addHumanPlayer("MonPote (Blancs)");

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