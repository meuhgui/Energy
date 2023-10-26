package energy.model;

/**
 * The shape of a Tile.
 */
public enum TileShape {

    /**
     * The square shape with 4 sides.
     */
    SQUARE(4, "S"),

    /**
     * The hexagon shape with 6 sides.
     */
    HEXAGON(6, "H");

    // Number of sides associated to each TileShape
    private final int sides;
    private final String fileIdentifier;

    TileShape(int sides, String fileIdentifier) {
        this.sides = sides;
        this.fileIdentifier = fileIdentifier;
    }

    // Returns the number of sides of this TileShape
    public int sides() {
        return this.sides;
    }

    public String fileIdentifier() {
        return this.fileIdentifier;
    }
}