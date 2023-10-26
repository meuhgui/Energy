package energy.controller;

import java.awt.event.MouseEvent;
import energy.model.*;
import energy.view.*;

public class GameController extends LevelController {
    private final PlayableLevel model;

    public GameController(PlayableLevel model) {
        this.model = model;
        this.model.randomRotations();
        this.updateModel();
    }

    private void updateModel() {
        this.model.blackout();
        this.model.propagateElectricity();
        this.model.notifyObservers();
    }

    @Override public void mouseClicked(MouseEvent e) {
        Object src = e.getSource();
        if (!(src instanceof CircuitView))
            return;

        Position tilePos = LevelController.getTileAtClick((CircuitView)src, e);
        if (model.rotateTileAt(tilePos)) {
            this.updateModel();
        }
    }
}
