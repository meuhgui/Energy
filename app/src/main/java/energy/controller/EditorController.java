package energy.controller;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import energy.model.Component;
import energy.model.EditableLevel;
import energy.model.Position;
import energy.model.Tile;
import energy.view.CircuitView;
import energy.model.Level;

public class EditorController extends LevelController {
    private final EditableLevel model;
    private boolean clearTileMode = false;
    private boolean componentEditionMode = false;
    private Component componentEdit = Component.EMPTY;
    private Position pointingTilePosition = null;
    private boolean hasChanged = false;

    public EditorController(EditableLevel model) {
        this.model = model;
        updateModel();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object src = e.getSource();
        if (!(src instanceof CircuitView))
            return;

        Position tilePos = LevelController.getTileAtClick((CircuitView) src, e);

        // User did not click on a Tile
        if (tilePos == null)
            return;

        if (componentEditionMode) {
            if (this.model.setTileComponentTo(tilePos, componentEdit)) {
                updateModel();
                // this.clearTileMode = false;
                this.hasChanged = true;
            }
        } else if (clearTileMode) {
            if (this.model.clearTileAt(tilePos)) {
                updateModel();
                // this.clearTileMode = false;
                this.hasChanged = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.pointingTilePosition = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Object src = e.getSource();
        if (!(src instanceof CircuitView))
            return;

        Position newMousePointingPosition = LevelController
            .getTileAtClick((CircuitView) src, e);

        if (pointingTilePosition == null) {
            pointingTilePosition = newMousePointingPosition;
        } else {
            // Pointing tile changed, a border need to be connected
            if (!pointingTilePosition.equals(newMousePointingPosition)) {
                Level l = (Level) model;

                // Find tiles associated to the two positions we have
                Tile initialTile = l
                        .getCircuit()
                        .getTileAt(pointingTilePosition);
                Tile newPointingTile = l
                        .getCircuit()
                        .getTileAt(newMousePointingPosition);

                if (initialTile != null && newPointingTile != null) {
                    // Compute relative positions to know at which side there is
                    // contact
                    int contact = initialTile.touchingSide(newPointingTile);
                    if (contact != -1) {
                        int neighBorder = (contact + initialTile.shape().sides() / 2)
                                % initialTile.shape().sides();

                        int modifiers = e.getModifiersEx();

                        // Connect these borders
                        if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0) {
                            initialTile.connect(contact);
                            newPointingTile.connect(neighBorder);
                        }
                        // Disconnect these borders
                        if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) != 0) {
                            initialTile.disconnect(contact);
                            newPointingTile.disconnect(neighBorder);
                        }
                        this.updateModel();
                        this.hasChanged = true;
                    }

                    // Change the pointingTilePosition
                    this.pointingTilePosition = newMousePointingPosition;
                }
            }
        }
    }

    // Clears the Circuit of the model
    public void clearCircuit() {
        this.model.clearCircuit();
        this.updateModel();
        this.hasChanged = true;
    }

    // Remove a column from circuit
    public void removeColumn() {
        this.model.removeColumn();
        this.updateModel();
        this.hasChanged = true;
    }

    // Add a column to circuit
    public void addColumn() {
        this.model.addColumn();
        this.updateModel();
        this.hasChanged = true;
    }

    // Adds a line to the Circuit of this EditableLevel
    public void addLine() {
        this.model.addLine();
        updateModel();
        this.hasChanged = true;
    }

    // Removes the last line of the Circuit of this EditableLevel
    public void removeLine() {
        this.model.removeLine();
        updateModel();
        this.hasChanged = true;
    }

    private void updateModel() {
        this.model.blackout();
        this.model.propagateElectricity();
        this.model.notifyObservers();
    }

    public void setComponentEditionMode(boolean on, Component newComponent) {
        this.componentEditionMode = on;
        this.componentEdit = newComponent;
    }

    public void setTileClearMode(boolean b) {
        this.clearTileMode = b;
    }

    public boolean isFinished() {
        return this.model.isFinished();
    }

    public boolean hasChanged() {
        return this.hasChanged;
    }

    public boolean circuitContainsLamp() {
        return this.model.circuitContainsLamp();
    }

    public boolean getTileClearMode() {
        return this.clearTileMode;
    }

    public boolean getComponentMode() {
        return this.componentEditionMode;
    }
}