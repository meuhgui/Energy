package energy.model;

import java.util.*;

/**
 * Represents a tile of Energy.
 */
public class Tile {

    // The shape of this Tile
    private final TileShape shape;

    // The position in a matrix of this Tile
    private final Position position;

    // The border of this Tile
    private final boolean[] border;

    // The electrical part of this Tile
    private Component component;

    // Current rotation angle
    private double angle;

    // Indicates if this Tile is powered
    private boolean isPowered;
    
    // Tile constructor that specifies each element with no verification
    private Tile(TileShape shape,
                 Position position,
                 Component component) {
        this.shape = shape;
        this.border = new boolean[shape.sides()];
        this.position = position;
        this.component = component;
        this.isPowered = this.component == Component.SOURCE;
    }

    /**
     * {@return a new Tile.}
     * @param shape the shape of the new Tile.
     * @param position the position of the new Tile.
     * @param component the electrical part of the new Tile.
     * @throws IllegalBorderException if the border does not correspond to the
     * provided shape
     */
    public static Tile of(TileShape shape,
                          Position position,
                          Component component) {
        return new Tile(shape, position, component);
    }

    /**
     * {@return a square Tile.}
     * @param position the position of the new Tile.
     * @param component the electrical part of the new Tile.
     * @throws IllegalBorderException if the border does not correspond to the
     * provided shape
     */
    public static Tile square(
        Position position,
        Component component
    ) {
        return new Tile(TileShape.SQUARE, position, component);
    }

    /**
     * {@return a hexagonal Tile}
     * @param position the position of the new Tile
     * @param component the electrical part of the new Tile
     * @throws IllegalBorderException if the border does not correspond to the
     * provided shape
     */
    public static Tile hexagon(
        Position position,
        Component component
    ) {
        return new Tile(TileShape.HEXAGON, position, component);
    }

    /**
     * Returns a new empty {@code Tile}.
     *
     * @param shape The shape of the new {@code Tile}.
     * @param position The position of the new {@code Position}.
     * @return a new {@code Tile} without connected borders and an empty
     * Component.
     */
    public static Tile empty(TileShape shape, Position position) {
        return new Tile(shape, position, Component.EMPTY);
    }

    /**
     * {@return the shape of this Tile}
     */
    public TileShape shape() {
        return this.shape;
    }

    /**
     * {@return the position of this Tile}
     */
    public Position position() {
        return this.position;
    }

    /**
     * {@return the component of this Tile}
     */
    public Component component() {
        return this.component;
    }
    
    public void setComponent(Component c) {
    	this.component = c;
    }

    /**
     * {@return true if this Tile's component can rotate}
     */
    public boolean canRotate() {
        return this.component.canRotate();
    }

    /**
     * {@return true if this Tile is powered}
     */
    public boolean isPowered() {
        return this.isPowered;
    }

    /**
     * {@return true if both at same Position and same Component}
     */
    @Override public boolean equals(Object other) {
        if (other == null) return false;
        if (this == other) return true;
        if (this.getClass() != other.getClass()) return false;
        Tile t = (Tile) other;
        return this.position.equals(t.position)
            && this.component.equals(t.component);
    }

    @Override public int hashCode() {
        final int prime1 = 31;
        final int prime2 = 41;
        int result = 1;
        result *= prime1;
        result += this.position.hashCode();
        result *= prime2;
        result += this.component.hashCode();
        return result;
    }

    /**
     * Returns true if this {@code Tile} is empty.
     *
     * @return true if this {@code Tile} has no borders and its component is
     * {@code Component.EMPTY}
     */
    public boolean isEmpty() {
        return this.isDisconnected()
            && this.component == Component.EMPTY;
    }

    /**
     * Returns the representation of this {@code Tile} as if a level file
     * (.nrg). If this {@code Tile} is empty, returns ".". If not, returns a
     * String starting by the diminutive of its component, a blank character
     * followed by the list of the connected borders separated by a blank space
     * in ascending order.
     *
     * @return the level file representation of this {@code Tile}.
     */
    public String levelRep() {
        StringBuilder res = new StringBuilder();
        res.append(this.component.getDiminutive());
        if(this.isEmpty() || this.isDisconnected()) {
        	return res.toString();
        }
        return res.append(this.borderLevelFileRepresentation()).toString();
    }

    private String borderLevelFileRepresentation() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < this.border.length; i++) {
            if (this.border[i]) {
            	res.append(" ").append(i);
            }
        }
        return res.toString();
    }

    /**
     * {@return the row position of this Tile}
     */
    public int getLine() {
        return this.position.getLine();
    }

    /**
     * {@return the column position of this Tile}
     */
    public int getColumn() {
        return this.position.getColumn();
    }

    /**
     * Rotates this Tile counterclockwise.
     */
    public void rotate() {
        if (this.canRotate()) {
            this.rotateBorder();
            if (this.shape == TileShape.SQUARE)
                angle = (angle + 90.0) % 360;
            if (this.shape == TileShape.HEXAGON)
                angle = (angle + 60.0) % 360;
        }
    }

    // Shifts clockwise the connected sides of this Tile
    private void rotateBorder() {
        boolean tmp = border[border.length - 1];
        for (int i = border.length - 1; i > 0; i--) {
            border[i] = border[i - 1];
        }
        border[0] = tmp;        
    }

    // Returns true if this Tile is at given Position
    public boolean isAt(Position position) {
        return this.position.equals(position);
    }

    // Sets the value of isPowered to the given value. If component is source,
    // does nothing
    public void setIsPowered(boolean isPowered) {
        if (this.component != Component.SOURCE) this.isPowered = isPowered;
    }

    // Returns true if the given tile is linked to this Tile, that is, if they
    // are of same shape and at the point they touch there is a connected border
    // for both Tiles
    public boolean isLinkedTo(Tile tile) {
        Objects.requireNonNull(tile);
        int ts = this.position.touchingSide(tile.position, this.shape);
        return this.shape == tile.shape
            && this.position.isNeighbor(tile.position, this.shape)
            && this.isConnectedTo(tile, ts);
    }
    
    // Returns true if this Border and the given Border are connected at the ith
    // side of this Border
    private boolean isConnectedTo(Tile tile, int i) {
        int len = border.length;
        return this.border[i] && tile.border[(i + len / 2) % len];
    }    

    public boolean side(int i) {
        return this.border[i];
    }

    // Sets this Tile's component to EMPTY and disconnects all the sides
    public void clear() {
        this.component = Component.EMPTY;
        Arrays.fill(this.border, false);
    }
    
    // If connect is true, connects the side at i, otherwise disconnects the
    // side at i
    private boolean connectOrDisconnectAt(boolean connect, int i) {
        boolean prev = this.border[i];
        this.border[i] = connect;
        return this.border[i] != prev;
    }
    
    // Connects the given side of the border of this Tile
    public boolean connect(int i) {
    	return connectOrDisconnectAt(true, i);
    }
    
    // Disconnects the given side of the border of this Tile
    public boolean disconnect(int i) {
        return connectOrDisconnectAt(false,i);
    }

    // Returns the number of sides of this Tile
    public int length() {
        return this.border.length;
    }

    // Returns true if this Tile has 0 connected sides
    public boolean isDisconnected() {
        for (boolean side: this.border) {
            if (side) {
                return false;
            }
        }
        return true;
    }

    // Returns the number of connected sides
    public int connectedSides() {
        int res = 0;
        for (boolean side: this.border) {
            if (side) {
                res++;
            }
        }
        return res;
    }

    // Return the indices of disconnected sides
	public List<Integer> getDisconnectedSidesAsIndices() {
		ArrayList<Integer> disc = new ArrayList<>();
		for (int i = 0; i < this.border.length; i++) {
			if (!this.border[i]) {
				disc.add(i);
			}
		}
		return disc;
	}

    // Connects the given sides of this Tile
    public void connect(int ... sides) {
        for (int i: sides)
            this.border[i] = true;
    }

    public int touchingSide(Tile other) {
        return this.position.touchingSide(other.position, this.shape);
    }
}