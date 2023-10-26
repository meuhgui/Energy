package energy.model;

import java.awt.Dimension;
import java.util.List;

public class ReadOnlyCircuit {
    private final Circuit circuit;

    public ReadOnlyCircuit(Circuit circuit) {
        this.circuit = circuit;
    }

    public Dimension dimension() {
        return this.circuit.dimension();
    }

    public boolean areAllHexagonalTiles() {
        return this.circuit.areAllHexagonalTiles();
    }

    public boolean areAllSquaredTiles() {
        return this.circuit.areAllSquaredTiles();
    }

    public List<Tile> tiles() {
        return this.circuit.getTiles();
    }
}