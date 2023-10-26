package energy.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

public class TileTest {

    @Test void shapeOfSquaredTileIsSquare() {
        Tile sut = Tile.of(
          TileShape.SQUARE,
          Position.origin(),
          Component.SOURCE
        );
        sut.connect(0);
        assertEquals(TileShape.SQUARE, sut.shape());
    }

    @Test void shapeOfHexagonalTileIsHexagon() {
        Tile sut = Tile.of(
          TileShape.HEXAGON,
          Position.origin(),
          Component.SOURCE
        );
        sut.connect(0);
        assertEquals(TileShape.HEXAGON, sut. shape());
    }

    @Test void tileAtOriginIsAtOrigin() {
        Tile sut = Tile.of(
          TileShape.HEXAGON,
          Position.origin(),
          Component.SOURCE
        );
        sut.connect(0);
        assertEquals(Position.origin(), sut.position());
    }

    @Test void tileIsAtGivenPosition() {
        Position pos = Position.at(3, 5);
        Tile sut = Tile.of(
          TileShape.HEXAGON,
          pos,
          Component.SOURCE
        );
        sut.connect(0);
        assertEquals(pos, sut.position());
    }

    @Test void tileHasGivenComponent() {
        Component source = Component.SOURCE;
        Tile sut = Tile.of(
          TileShape.HEXAGON,
          Position.origin(),
          source
        );
        sut.connect(0);
        assertEquals(source, sut.component());
    }

    @Test void shapeOfSquaredTileWithFrabricIsSquare() {
        Tile sut = Tile.square(
          Position.origin(),
          Component.LAMP
        );
        assertEquals(TileShape.SQUARE, sut. shape());
    }

    @Test void shapeOfHexagonalTileWithFrabricIsHexagon() {
        Tile sut = Tile.hexagon(
          Position.origin(),
          Component.LAMP
        );
        assertEquals(TileShape.HEXAGON, sut. shape());
    }

    @Test void tileWithLampCanRotate(){
        Tile sut = Tile.square(
          Position.origin(),
          Component.LAMP
        );
        assert(sut.canRotate());
    }

    @Test void tileWithHotspotCanRotate() {
        Tile sut = Tile.hexagon(
          Position.origin(),
          Component.HOTSPOT
        );
        assert(sut.canRotate());
    }

    @Test void tileWithSourceCanNotRotate() {
        Tile sut = Tile.hexagon(
          Position.origin(),
          Component.SOURCE
        );
        assertFalse(sut.canRotate());
    }

    @Test void tileIsPoweredWhenHoldsSourceComponent() {
        Tile sut = Tile.square(
          Position.origin(),
          Component.SOURCE
        );
        assert(sut.isPowered());
    }

    @Test void tilesAtSamePositionAndSameComponentAreEqual() {
        Tile sut = Tile.square(
          Position.origin(),
          Component.SOURCE
        );
        Tile t2 = Tile.square(
          Position.origin(),
          Component.SOURCE
        );
        assert(sut.equals(t2));
    }

    @Test void isEmtpyOnEmptyTileReturnsTrue() {
        Tile sut = Tile.empty(TileShape.HEXAGON, Position.origin());
        assert(sut.isEmpty());
    }

    @Test void anEmptyTileHasNoBorder() {
        Tile sut = Tile.empty(TileShape.HEXAGON, Position.origin());
        assert(sut.isDisconnected());
    }

    @Test void tileWithOneConnectedBorderIsNotEmpty() {
        Tile sut = Tile.square(
          Position.origin(),
          Component.EMPTY
        );
        sut.connect(3);
        assertFalse(sut.isEmpty());
    }

    @Test void tileWithComponentIsNotEmpty() {
        Tile sut = Tile.hexagon(
          Position.origin(),
          Component.SOURCE
        );
        assertFalse(sut.isEmpty());
    }

    @Test void emptyTileLevelFileRepIsADot() {
        Tile sut = Tile.empty(TileShape.HEXAGON, Position.origin());
        assertEquals(".", sut.levelRep());
    }

    @Test void tileLevelFileRepHasSameBorder() {
        Tile sut = Tile.square(
          Position.at(4, 2),
          Component.EMPTY
        );
        sut.connect(1);
        assertEquals(". 1", sut.levelRep());
    }

    @Test void tileWithComponentHasCorrectLevelFileRep() {
        Tile sut = Tile.hexagon(
          Position.origin(),
          Component.HOTSPOT
        );
        sut.connect(2, 4, 5);
        System.out.println(sut.levelRep());
        assertEquals("W 2 4 5", sut.levelRep());
    }

    @Test void neighborsAreNotLinked() {
        Tile t1 = Tile.hexagon(Position.at(2, 3), Component.HOTSPOT);
        t1.connect(0, 1, 2, 4, 5);
        Tile t2 = Tile.hexagon(Position.at(3, 3), Component.LAMP);
        t2.connect(0);
        assertFalse(t1.isLinkedTo(t2));
    }

    @Test void tilesAreLinked() {
        Tile t1 = Tile.square(Position.at(0, 1), Component.SOURCE);
        t1.connect(1, 3);
        Tile t2 = Tile.square(Position.at(0, 0), Component.LAMP);
        t2.connect(0, 1);
        assertTrue(t1.isLinkedTo(t2));
    }

    @Test void level7TilesNotLinked() {
        Tile t1 = Tile.hexagon(Position.at(1, 0), Component.EMPTY);
        t1.connect(0, 2);
        Tile t2 = Tile.hexagon(Position.at(1, 1), Component.EMPTY);
        t2.connect(0, 3, 5);
        assertTrue(t1.isLinkedTo(t2));
    }

    @Test void level7TilesNotLinked2() {
        Tile t1 = Tile.hexagon(Position.at(1,0), Component.EMPTY);
        t1.connect(1, 5);
        Tile t2 = Tile.hexagon(Position.at(0, 1), Component.EMPTY);
        t2.connect(0, 4);
        assertTrue(t1.isLinkedTo(t2));
    }
}
