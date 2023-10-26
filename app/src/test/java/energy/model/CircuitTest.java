package energy.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.awt.*;

public class CircuitTest {

    @Test void dimensionOfEmptyCircuitIs0_0() {
        Circuit sut = Circuit.empty();
        assertEquals(new Dimension(), sut.dimension());
    }

    @Test void dimensionOf2by3CircuitOfSquaredTilesIs2By3() {
        Circuit sut = Circuit.empty();
        Position p1 = Position.at(0, 0);
        Position p2 = Position.at(0, 1);
        Position p3 = Position.at(1, 0);
        Position p4 = Position.at(1, 1);
        Position p5 = Position.at(2, 0);
        Position p6 = Position.at(2, 1);
        sut.addTile(Tile.square(p1, Component.SOURCE));
        sut.addTile(Tile.square(p3, Component.SOURCE));
        sut.addTile(Tile.square(p4, Component.SOURCE));
        sut.addTile(Tile.square(p5, Component.SOURCE));
        sut.addTile(Tile.square(p6, Component.SOURCE));
        sut.addTile(Tile.square(p2, Component.SOURCE));
        assertEquals(new Dimension(2, 3), sut.dimension());
    }

    @Test void dimensionOf3By4CircuitOfHexagonTilesIs3By4() {
        Circuit sut = Circuit.empty();
        Position p1 = Position.at(0, 0);
        Position p2 = Position.at(0, 1);
        Position p3 = Position.at(1, 2);
        Position p4 = Position.at(1, 0);
        Position p5 = Position.at(1, 1);
        Position p6 = Position.at(2, 2);
        Position p7 = Position.at(2, 0);
        Position p8 = Position.at(2, 1);
        Position p9 = Position.at(2, 2);
        Position p10 = Position.at(3, 0);
        Position p11 = Position.at(3, 2);
        sut.addTile(Tile.hexagon(p1, Component.LAMP));
        sut.addTile(Tile.hexagon(p2, Component.SOURCE));
        sut.addTile(Tile.hexagon(p3, Component.HOTSPOT));
        sut.addTile(Tile.hexagon(p4, Component.LAMP));
        sut.addTile(Tile.hexagon(p5, Component.LAMP));
        sut.addTile(Tile.hexagon(p6, Component.HOTSPOT));
        sut.addTile(Tile.hexagon(p7, Component.LAMP));
        sut.addTile(Tile.hexagon(p8, Component.SOURCE));
        sut.addTile(Tile.hexagon(p9, Component.LAMP));
        sut.addTile(Tile.hexagon(p10, Component.HOTSPOT));
        sut.addTile(Tile.hexagon(p11, Component.LAMP));
        assertEquals(new Dimension(3, 4), sut.dimension());
    }

    
}
