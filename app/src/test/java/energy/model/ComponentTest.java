package energy.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

public class ComponentTest {

    @Test void sourceComponentCanNotRotate() {
        assertFalse(Component.SOURCE.canRotate());
    }

    @Test void hotspotComponentCanRotate() {
        assert(Component.HOTSPOT.canRotate());
    }

    @Test void lampComponentCanRotate() {
        assert(Component.LAMP.canRotate());
    }

    @Test void emptyComponentCanNotRotate() {
        assert(Component.EMPTY.canRotate());
    }

    @Test void sourceDiminutiveIsS() {
        assertEquals("S", Component.SOURCE.getDiminutive());
    }

    @Test void hotspotDiminutiveIsW(){
        assertEquals("W", Component.HOTSPOT.getDiminutive());
    }

    @Test void lampDiminutiveIsL() {
        assertEquals("L", Component.LAMP.getDiminutive());
    }

    @Test void emptyDiminutiveIsDot() {
        assertEquals(".", Component.EMPTY.getDiminutive());
    }
}
