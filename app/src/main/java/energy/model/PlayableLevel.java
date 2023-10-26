package energy.model;

import energy.view.Observable;

public interface PlayableLevel extends Observable {

    // Rotates the tile at given Position
    boolean rotateTileAt(Position position);

    // Rotates randomly all tiles
    void randomRotations();

    // Turns the lights out
    void blackout();
    
    // Propagates electricity through the circuit
    void propagateElectricity();
}