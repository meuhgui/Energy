package energy.model;

import java.awt.Dimension;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;

/**
 * Electrical parts linked together via cable or Wi-Fi.
 */
public class Circuit {

	// Tiles of this Circuit.
	private final ArrayList<Tile> tiles;
	
	// Initializes empty tiles and links lists
	private Circuit() {
		this.tiles = new ArrayList<>();
	}
	
	/**
     * {@return a new empty Circuit}.
     */
	public static Circuit empty() {
		return new Circuit();
	}
	
	/**
     * {@return true if the connexion was made}.
     * @param tile The {@code Tile} we want the connexion to change.
     */
	public boolean connectSideOfTileAt(Position p, int index) {
		Tile t = getTileAt(p);
        if (!this.tiles.
        contains(t)) return false;
		return t.connect(index);
	}

	/**
     * {@return true if the deconnexion was made}.
     * @param tile The {@code Tile} we want the connexion to change.
     */
	public boolean disconnectSideOfTileAt(Position p, int index) {
		Tile t = getTileAt(p);
        if (!this.tiles.contains(t)) return false;
		return t.disconnect(index);
	}
	
	/**
     * {@return true if tile component was changed}.
     * @param tile The {@code Tile} we want the component to change.
     */
	public boolean setTileComponentTo(Position p, Component c) {
		Tile t = getTileAt(p);
        if (!this.tiles.contains(t)) return false;
		t.setComponent(c);
		return true;
	}
	
	/**
     * {@return true if tile was added}.
     * @param tile The {@code Tile} to add.
     */
	public boolean addTile(Tile t) {
		if (this.tiles.contains(this.getTileAt(t.position()))){
            return false;
        }
		return tiles.add(t);
	}
	
	/**
     * {@return true if tile was removed}.
     * @param tile The {@code Tile} to remove.
     */
	public boolean removeTile(Position p) {
		Tile t = getTileAt(p);
        if (!this.tiles.contains(t) || p.equals(Position.origin())) {
            return false;            
        }
		return tiles.remove(t);
	}
	
	/**
     * {@return the list of all tiles}
     */
	public ArrayList<Tile> getTiles(){
		return this.tiles;
	}

    // Computes the appearances of each TileShape in this Circuit's
    // tiles' shapes
    private int[] getProportionOfTileShapes() {
        int[] shapes = new int[TileShape.values().length];
        for (Tile tile: this.tiles) {
            shapes[tile.shape().ordinal()]++;
        }
        return shapes;
    }

    // Returns true if all Tiles are of same shape or this Circuit is empty.
    private boolean areAllTilesOfSameShape() {
        int[] shapes = getProportionOfTileShapes();
        boolean found = false;
        for (int shape : shapes) {
            if (shape != 0) {
                if (found)
                    return false;
                else
                    found = true;
            }
        }
        return true;
    }

    /**
     * {@return true if all the tiles of this Circuit are hexagonal.}
     * @throws IllegalStateException if there are hexagonal and squared tiles in
     *         this Circuit.
     */
    public boolean areAllHexagonalTiles() {
        if (this.tiles.isEmpty()) return false;
        if (!areAllTilesOfSameShape()) {
            throw new IllegalStateException(
              "Circuit with squared and hexagonal tiles"
            );
        }
        int[] shapes = getProportionOfTileShapes();
        return shapes[TileShape.HEXAGON.ordinal()] != 0;
    }

    /**
     * {@return true if all the tiles of this Circuit are squared.}
     * @throws IllegalStateException if there are hexagonal and squared tiles in
     *         this Circuit.
     */
    public boolean areAllSquaredTiles() {
        if (this.tiles.isEmpty()) return false;
        if (!areAllTilesOfSameShape()) {
            throw new IllegalStateException(
              "Circuit with squared and hexagonal tiles"
            );
        }
        int[] shapes = getProportionOfTileShapes();
        return shapes[TileShape.SQUARE.ordinal()] != 0;
    }

    /**
     * {@return the dimension of this Circuit}
     */
    public Dimension dimension() {
        if (this.tiles.isEmpty())
            return new Dimension();
        int maxRow = 0;
        int maxCol = 0;
        for (Tile tile: this.tiles) {
            if (tile.getLine() > maxRow)
                maxRow = tile.getLine();
            if (tile.getColumn() > maxCol)
                maxCol = tile.getColumn();
        }
        return new Dimension(maxCol + 1, maxRow + 1);
    }

    // Eteint toutes les tuiles, sauf les sources, peu importe la configuration.
    public void blackout() {
        for (Tile tile: this.tiles) {
            tile.setIsPowered(false);
        }
    }

    // Propage l'électricité à partir des sources de ce circuit
    public void propagateElectricity() {
        Deque<Tile> toExplore = new ArrayDeque<>(this.getSources());
        while (!toExplore.isEmpty()) {
            Tile cur = toExplore.pop();
            cur.setIsPowered(true);
            List<Tile> neighs = this.getNeighbors(cur);
            for (Tile tile: neighs) {
                if (!tile.isPowered() && !toExplore.contains(tile)) {
                    toExplore.push(tile);
                }
            }
        }
    }

    // Returns true if the Tile at the given Position rotated.
    public boolean rotateTileAt(Position position) {
        for (Tile tile: this.tiles) {
            if (tile.isAt(position) && tile.canRotate()) {
                tile.rotate();
                return true;
            }
        }
        return false;
    }

    // Returns the linked neighbors of the given tile
    public List<Tile> getNeighbors(Tile tile) {
        Objects.requireNonNull(tile);
        if (!this.tiles.contains(tile))
            throw new NoSuchElementException("Tile is not in Circuit");
        List<Tile> res = new ArrayList<>();
        List<Position> neighPos = Position.neighborPositions(tile.position(),
                                                             tile.shape());
        List<Tile> neighbors = new ArrayList<>();
        for (Position position: neighPos) {
            Tile t = this.getTileAt(position);
            if (t != null && !neighbors.contains(t)) {
                neighbors.add(t);
            }
        }
        // keep linked neighbors
        for (Tile t: neighbors) {
            if (tile.isLinkedTo(t)) {
                res.add(t);
            }
        }
        // add other hotspots if not already linked
        if (tile.component() == Component.HOTSPOT) {
            List<Tile> hotspots = this.getHotspots();
            for (Tile hotspot: hotspots) {
                if (!res.contains(hotspot)) {
                    res.add(hotspot);
                }
            }
            res.remove(tile); // remove itself
        }
        return res;
    }

    // Returns the tile at the given position, null if none
    public Tile getTileAt(Position position) {
        Objects.requireNonNull(position);
        for (Tile tile: this.tiles) {
            if (tile.position().equals(position)) {
                return tile;
            }
        }
        return null;
    }

    // Returns the sources of this Circuit
    public List<Tile> getSources() {
        List<Tile> res = new ArrayList<>();
        for (Tile tile: this.tiles) {
            if (tile.component() == Component.SOURCE) {
                res.add(tile);
            }
        }
        return res;
    }

    // Returns the hotspots, if any, of this Circuit
    public List<Tile> getHotspots() {
        List<Tile> res = new ArrayList<>();
        for (Tile tile: this.tiles) {
            if (tile.component() == Component.HOTSPOT) {
                res.add(tile);
            }
        }
        return res;
    }

    // Returns true if all the lamps in this Circuit are powered on and this
    // Circuit is not empty
    public boolean allLampsArePoweredOn() {
        for (Tile tile: this.tiles) {
            if (tile.component() == Component.LAMP && !tile.isPowered()) {
                return false;
            }
        }
        return !this.isEmpty();
    }

    // Returns true if this Circuit has no tiles
    public boolean isEmpty() {
        return this.tiles.isEmpty();
    }

    // Randomly rotates the tile of this Circuit, leaving in a state different
    // from the initial state and finished state
    public void randomRotations() {
        Random random = new Random();
        for (Tile tile: this.tiles) {
            if (tile.canRotate()) {
                int m = tile.length();
                int nbRotations = 0;
                while (nbRotations == 0)
                    nbRotations = random.nextInt(m);
                for (int i = 0; i < nbRotations; i++) {
                    tile.rotate();
                }
            }
        }
    }

    // Clears all the Tiles of this Circuit
    public void clear() {
        for (Tile tile: this.tiles) {
            tile.clear();
        }
    }

    // Clears the Tile at the given Position if any
    public boolean clearTileAt(Position position) {
        for (Tile tile: this.tiles) {
            if (tile.position().equals(position)) {
                tile.clear();
                return true;
            }
        }
        return false;
    }

    private void addOrRemoveLine(boolean add) {
        Dimension dim = this.dimension();
        int h = dim.height;
        int w = dim.width;
        
        TileShape shape = 
            (this.areAllSquaredTiles()) ? TileShape.SQUARE : TileShape.HEXAGON;
        
        if (!add && h == 1) // can't remove last line
            return;
            
        // can't remove line as would create separated hexagons
        if (!add && w > h && h == 2 && shape == TileShape.HEXAGON)
            return;
        
        for (int j = 0; j < w; j++) {
            if (shape == TileShape.HEXAGON && (j % 2 == 1)) {
                if (add) {
                    this.addTile(Tile.empty(shape, Position.at(h - 1, j)));
                } else {
                    this.removeTile(Position.at(h - 2, j));
                }
            } else {
                if (add) {
                    this.addTile(Tile.empty(shape, Position.at(h, j)));
                } else {
                    this.removeTile(Position.at(h - 1, j));
                }
            }
        }
    }

    public void addLine() {
        this.addOrRemoveLine(true);
    }

    public void removeLine() {
        this.addOrRemoveLine(false);
    }
    
    // Add or remove a column from the circuit according to flag
    // true -> add and false -> remove
    public void addOrRemoveColumn(boolean add) {
    	TileShape shape = 
            this.areAllHexagonalTiles() ? TileShape.HEXAGON: TileShape.SQUARE;
    	
        Dimension dim = this.dimension();
        int h = dim.height;
        int w = dim.width;
            
        if (!add && w == 1) // can't remove last column
            return;
        
        if (add && h == 1 && shape == TileShape.HEXAGON) // can't add
            return;
        
        System.out.println("h " + h + " w " + w);
    	for (int i = 0; i < h; i++) {
    		if (add) {
                if (shape == TileShape.HEXAGON && i == h - 1 && w % 2 == 1) {
                    continue;
                }
                this.addTile(Tile.empty(shape, Position.at(i, w)));
            } else {
                System.out.println("Remove tile at " + i + " " + (w - 1));
                this.removeTile(Position.at(i, w - 1));
            }
    	}
    }
    
    // Add column to circuit
    public void addColumn() {
    	this.addOrRemoveColumn(true);
    }
    
    // Remove column from circuit
    public void removeColumn() {
    	this.addOrRemoveColumn(false);
    }

    // Returns true if this Circuit contains a Tile with a Lamp component
    public boolean containsLamp() {
        for (Tile tile: this.tiles)
            if (tile.component() == Component.LAMP) return true;
        return false;
    }
}