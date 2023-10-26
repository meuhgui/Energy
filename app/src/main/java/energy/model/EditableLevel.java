package energy.model;

import energy.view.Observable;

public interface EditableLevel extends Observable {

    // Add link between two tiles
    boolean connectSideOfTileAt(Position p, int index);
    
    // Remove connected border from tile
    boolean disconnectSideOfTileAt(Position p, int index);

    // Change Component of a tile (eventually Empty component)
    boolean setTileComponentTo(Position p, Component c);
    
    // Save level
    boolean save();

    // Powers off every Tile of the Circuit of this EditableLevel
    void blackout();
    
    // Propagates the electricity through the Circuit of this EditableLevel
    void propagateElectricity();

    // Clears the Circuit of this EditableLevel
    void clearCircuit();

    // Returns true if the Tile at the given Position in the Circuit of this 
    // EditableLevel was cleared
    boolean clearTileAt(Position position);
    
    // remove a column from the circuit
    void removeColumn();
    
    // add a column from the circuit
    void addColumn();

    // Adds a line to the Circuit of this EditableLevel
    void addLine();
    
    // Removes the last line of the Circuit of this EditableLevel
    void removeLine();

    // Returns true if the game is finished (all lamps in cricuit are turned on)
    boolean isFinished();

    // Returns true if the circuit contains at least one tile with lamp
    boolean circuitContainsLamp();
}