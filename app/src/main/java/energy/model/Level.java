package energy.model;

import energy.view.Observer;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class Level implements PlayableLevel, EditableLevel {
	
	// Level id
	private final int id;

	// Circuit associated to this Level
	private final Circuit circuit;

    // Observers of this Level
    private final List<Observer> observers = new ArrayList<>();
	
	// Initializes Level from given id and circuit
	private Level(int id, Circuit circuit) {
		this.id = id;
		this.circuit = circuit;
	}
	
	/**
     * {@return a new Level from given parameters}
     */
	public static Level from(int id, Circuit circuit) {
        if (id < 0)
            throw new IllegalArgumentException("Id must be positive");
        else if (circuit == null)
            throw new IllegalArgumentException("Circuit can not be null");
        else
			return new Level(id, circuit);
	}
	
	/**
     * {@return a new Level from a levelConfig object}
     */
	public static Level fromLevelConfig(LevelConfig lc) {
		Circuit c = Circuit.empty();
		for (Tile t : lc.tiles()) {
			c.addTile(t);
		}
		if (c.areAllHexagonalTiles()) {
			Dimension dim = c.dimension();
			int w = dim.width;
			int h = dim.height;
			for (int j = 1; j < w; j += 2) {
				c.removeTile(Position.at(h - 1, j));
			}
		}
		return new Level(lc.id(), c);
    }
	
	public int getId() {
		return id;
	}

	public Circuit getCircuit() {
		return circuit;
	}

    // Notifies all observers of an update
    public void notifyObservers() {
        for (Observer observer: this.observers) {
            observer.update(this);
        }
    }

    // Adds the given observer as a new observer of this Level
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    // Returns true if the Tile at the given Position rotated.
    @Override public boolean rotateTileAt(Position position) {
        return this.circuit.rotateTileAt(position);
    }

	// Returns true if all the lamps of this Level's Circuit are powered on
    public boolean isFinished() {
        return this.circuit.allLampsArePoweredOn();
    }

    // Randomly rotates every tile
    public void randomRotations() {
        this.circuit.randomRotations();
    }

    public void blackout() {
        this.circuit.blackout();
    }

    public void propagateElectricity() {
        this.circuit.propagateElectricity();
    }

	@Override
	public boolean connectSideOfTileAt(Position p, int index) {
		return circuit.connectSideOfTileAt(p, index);
	}

	@Override
	public boolean disconnectSideOfTileAt(Position p, int index) {
		return circuit.disconnectSideOfTileAt(p, index);
	}


	@Override
	public boolean setTileComponentTo(Position p, Component c) {
		return this.circuit.setTileComponentTo(p, c);
	}

	@Override
	public boolean save() {
		if(circuit.allLampsArePoweredOn()) {
			LevelConfig lc = LevelConfig.fromLevel(this);
			lc.save();
			return true;
		}
		return false;
	}

	@Override
	public void clearCircuit() {
		this.circuit.clear();
	}

	// Clears the Tile at the given Position in the Circuit of this Level, if 
	// any
	public boolean clearTileAt(Position position) {
		return this.circuit.clearTileAt(position);
	}

	// Adds a column to the Circuit of this Level
	public void removeColumn() {
		this.circuit.removeColumn();
	}
	
	// Removes a column from the Circuit of this Level
	public void addColumn() {
		this.circuit.addColumn();
	}

	// Adds a line to the Circuit of this Level
	public void addLine() {
		this.circuit.addLine();
	}
	
	// Removes the last line of the Circuit of this Level
	public void removeLine() {
		this.circuit.removeLine();
	}

	@Override
	public boolean circuitContainsLamp() {
		return this.circuit.containsLamp();
	}
}