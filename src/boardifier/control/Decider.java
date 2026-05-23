package boardifier.control;

import boardifier.model.Model;
import boardifier.model.action.ActionList;

public abstract class Decider {
    protected Model model;
    protected Controller control;

    public Decider(Model model, Controller control) {
        this.model = model;
        this.control = control;
    }

    public abstract ActionList decide();
}
