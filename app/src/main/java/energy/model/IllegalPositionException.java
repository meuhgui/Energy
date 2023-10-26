package energy.model;

/**
 * To be thrown when the position in a matrix is not valid.
 */
public class IllegalPositionException extends RuntimeException {

    /**
     * Creates a new IllegalPositionException that displays the given message.
     *
     * @param message the message to display when this is thrown.
     */
    public IllegalPositionException(String message) {
        super(message);
    }
}