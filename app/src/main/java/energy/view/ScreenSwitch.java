package energy.view;

// Interface for screen switch
public interface ScreenSwitch {

    /**
     * Switches to the level representation of the given id. If flag is true,
     * displays the playable view, the editor view otherwise.
     * @param id the id of the level to display
     * @param flag true for playable view, editor view otherwise
     */
    void next(int id, boolean flag);
    
    /**
     * Switches back to the main screen.
     */
    void back();
}