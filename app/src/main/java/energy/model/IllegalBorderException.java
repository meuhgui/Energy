package energy.model;

/**
 * Thrown when the border given for Tile creation is incompatible with its
 * shape.
 */
public class IllegalBorderException extends RuntimeException {

    /**
     * Creates a new IllegalBorderException that displays given message
     *
     * @param message the message to display when this exception is thrown
     */
    public IllegalBorderException(String message) {
        super(message);
    }
}