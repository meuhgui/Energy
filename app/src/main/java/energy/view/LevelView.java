package energy.view;

import javax.swing.*;
import java.awt.*;

import energy.controller.EditorController;
import energy.model.*;
import energy.model.Component;

public class LevelView extends JPanel implements Observer {
    private Level model;
    private final ScreenSwitch switcher;
    private final CircuitView circuitView;
    private final boolean editorMode;
    private EditorController editorController;

    // Flag is true when the level is loaded to play, false to edit
    public LevelView(int id, ScreenSwitch switcher, boolean flag) {
        this.model = null;
        this.editorController = null;
        this.circuitView = new CircuitView();
        this.switcher = switcher;
        this.editorMode = !flag;

        this.setBackground(Color.RED);
        this.setLayout(new BorderLayout());

        JPanel p = new JPanel();
        JLabel levelIDLabel = new JLabel("#" + id);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            if (editorMode
                    && editorController.isFinished()
                    && editorController.hasChanged()
                    && editorController.circuitContainsLamp()) {
                if (askSaveConfirmation()) {
                    this.model.save();
                }
            }
            this.switcher.back();
        });
        p.setBackground(Color.YELLOW);
        p.add(levelIDLabel);
        p.add(backButton);
        this.add(p, BorderLayout.PAGE_END);
        this.add(this.circuitView, BorderLayout.CENTER);

        if (this.editorMode)
            this.addEditionButtonsPanel();
    }

    public void update(Observable observed) {
        if (observed instanceof Level) {
            this.model = (Level)observed;
            this.circuitView.setModel(
              new ReadOnlyCircuit(this.model.getCircuit())
            );
            this.circuitView.repaint();

            if (!this.editorMode) {
                this.displayEndGameDialog();
            }
        }
    }

    // Displays a dialog asking the user if they want to play another game. This
    // Dialog is displayed only if model.isFinished() returns true.
    // Depending on the result, switches back to the level selection panel or
    // to the next game.
    public void displayEndGameDialog() {
        if (this.model.isFinished()) {
            if (askNextGameConfirmation())
                this.switcher.next(this.model.getId() + 1, true);
            else
                this.switcher.back();
        }
    }

    public CircuitView getCircuitView() {
        return this.circuitView;
    }

    public void addEditionButtonsPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        JButton addLineButton = new JButton("Add line");
        addLineButton.addActionListener(e -> editorController.addLine());

        JButton addColumnButton = new JButton("Add column");
        addColumnButton.addActionListener(e -> editorController.addColumn());

        JButton removeLineButton = new JButton("Remove line");
        removeLineButton.addActionListener(e -> editorController.removeLine());

        JButton removeColumnButton = new JButton("Remove column");
        removeColumnButton.addActionListener(e -> 
            editorController.removeColumn());
        
        JButton clearCircuitButton = new JButton("Clear circuit");
        clearCircuitButton.addActionListener(e -> {
            if (askCircuitClearConfirmation())
                editorController.clearCircuit();
        });

        JButton clearTileButton = new JButton("Clear tile");
        clearTileButton.addActionListener(e -> {
            boolean cur = editorController.getTileClearMode();
            editorController.setTileClearMode(!cur);
            if (!cur) clearTileButton.setBackground(Color.RED);
            else clearTileButton.setBackground(new JButton().getBackground());
        });

        JButton setComponentButton = new JButton("Change component");
        setComponentButton.addActionListener(e -> {
            boolean cur = editorController.getComponentMode();
            if (cur) {
                editorController.setComponentEditionMode(false,
                                                         Component.EMPTY);
                setComponentButton.setBackground(new JButton().getBackground());
            } else {
                Component newComponent = askTileComponent();
                if (newComponent == null)
                    return;
                editorController.setComponentEditionMode(true, newComponent);
                setComponentButton.setBackground(Color.RED);
            }
        });
        
        p.add(addLineButton);
        p.add(removeLineButton);
        p.add(addColumnButton);
        p.add(removeColumnButton);
        p.add(clearCircuitButton);
        p.add(clearTileButton);
        p.add(setComponentButton);

        this.add(p, BorderLayout.LINE_END);
    }

    // Displays a dialog asking the user to select some Component
    private Component askTileComponent() {
        Object[] possibilities = Component.values();
        return (Component)JOptionPane.showInputDialog(
            this,
            "Select tile component",
            "Choose Tile Component",
            JOptionPane.PLAIN_MESSAGE,
            null,
            possibilities,
            Component.EMPTY
        );
    }

    private boolean askCircuitClearConfirmation() {
        return this.askConfirmation("Are you sure ?", 
                                    "Circuit clear confirmation");
    }

    private boolean askSaveConfirmation() {
        return this.askConfirmation("Do you want to save modifications ?", 
                                    "Save confirmation");
    }

    private boolean askNextGameConfirmation() {
        return this.askConfirmation("Play next ?", "You won !");
    }

    // Returns true if the user clicked on the "Yes" button of the 
    // confirm dialog of given title and message
    private boolean askConfirmation(String msg, String title) {
        int res = JOptionPane.showConfirmDialog(
            null,
            msg,
            title,
            JOptionPane.YES_NO_OPTION
        );
        return res == JOptionPane.YES_OPTION;
    }    

    public void setEditorController(EditorController ec) {
        this.editorController = ec;
    }
}