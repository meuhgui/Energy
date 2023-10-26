package energy.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

public class PositionTest {

    @Test void getLineOfOriginReturnsZero() {
        Position origin = Position.origin();
        assertEquals(0, origin.getLine());
    }

    @Test void getLineOfPositionReturnsLine() {
        Position pos = Position.at(5, 3);
        assertEquals(5, pos.getLine());
    }

    @Test void getColumnOfOriginReturnsZero() {
        Position origin = Position.origin();
        assertEquals(0, origin.getColumn());
    }

    @Test void getColumnOfPositionReturnsColumn() {
        Position pos = Position.at(3, 5);
        assertEquals(5, pos.getColumn());
    }

    @Test void negativeLineCreationThrowsIllegalPositionException() {
        IllegalPositionException thrown = assertThrows(
            IllegalPositionException.class,
            () -> Position.at(-4, 4)
        );
        assertEquals("Negative position", thrown.getMessage());
    }

    @Test void negativeColumnCreationThrowsIllegalPositionException() {
        IllegalPositionException thrown = assertThrows(
            IllegalPositionException.class,
            () -> Position.at(4, -4)
        );
        assertEquals("Negative position", thrown.getMessage());
    }

    @Test void samePositionInstancesAreEqual() {
        Position origin = Position.origin();
        assertEquals(origin, origin);
    }

    @Test void positionAndNullAreDifferent() {
        Position pos = Position.at(4, 5);
        assertNotEquals(null, pos);
    }

    @Test void positionAndIntegerAreNotEqual() {
        Position pos = Position.origin();
        assertNotEquals(1, pos);
    }

    @Test void samePositionsAreEqual() {
        Position p1 = Position.at(4, 5);
        Position p2 = Position.at(4, 5);
        assertEquals(p1, p2);
    }

    @Test void originsAreEqual() {
        Position o1 = Position.origin();
        Position o2 = Position.origin();
        assertEquals(o1, o2);
    }
}
