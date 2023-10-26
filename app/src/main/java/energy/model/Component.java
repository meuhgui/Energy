package energy.model;

/**
 * A Component is a part of an electrical circuit.
 */
public enum Component {

    /**
     * The Source component.
     */
    SOURCE(false, "S"),

    /**
     * The Hotspot component.
     */
    HOTSPOT(true, "W"),

    /**
     * The Lamp component.
     */
    LAMP(true, "L"),

    /**
     * The empty component.
     */
    EMPTY(true, ".");
  
    // Indicates whether this Component can rotate
    private final boolean canRotate;

    // The diminutive of this Component
    private final String diminutive;

    Component(boolean canRotate, String diminutive) {
        this.canRotate = canRotate;
        this.diminutive = diminutive;
    }

    /**
     * {@return true if this Component can rotate}.
     */
    public boolean canRotate() {
        return this.canRotate;
    }


    /**
     * {@return the diminutive of this Component}.
     */
    public String getDiminutive() {
        return this.diminutive;
    }
    
    /**
     * {@return Component associated to String}.
     * @throws IllegalArgumentException if the given string doesn't correspond 
     * to any Component diminutive
     */
    public static Component componentFromDiminutive(String s) {
        if (s.equals("S")) return Component.SOURCE;
        if (s.equals("W")) return Component.HOTSPOT;
        if (s.equals("L")) return Component.LAMP;
        if (s.equals(".")) return Component.EMPTY;
        else throw new IllegalArgumentException("Unknown diminutive");
    }
}