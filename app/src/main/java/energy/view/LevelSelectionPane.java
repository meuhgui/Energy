package energy.view;

import javax.swing.*;

import energy.App;
import energy.model.LevelConfig;

import java.awt.*;
import java.awt.event.ActionListener;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LevelSelectionPane extends JPanel {
    private final ScreenSwitch switcher;
    private JPanel levelButtonsPanel;
    private List<String> playableLevelNames;
    private List<String> editableLevelNames;
    private boolean displayFlag; // true if playable displayed false if editable

    private static final int LEVEL_SELECTION_PANE_WIDTH = 1000;
    private static final int LEVEL_SELECTION_PANE_HEIGHT = 800;

    public LevelSelectionPane(ScreenSwitch switcher) {
        this.switcher = switcher;
        ActionListener al1 = createLoadButtonActionListener(true, this);
        ActionListener al2 = createLoadButtonActionListener(false, this);
        JButton playableLevelsButton = createNamedButton("Playable", al1);
        JButton editableLevelsButton = createNamedButton("Editable", al2);
        this.editableLevelNames = new ArrayList<>();
        this.playableLevelNames = new ArrayList<>();

        this.setBackground(Color.YELLOW);
        this.setPreferredSize(
          new Dimension(LEVEL_SELECTION_PANE_WIDTH, LEVEL_SELECTION_PANE_HEIGHT)
        );

        // Load buttons panel
        JPanel loadButtonsPanel = new JPanel();
        loadButtonsPanel.setBackground(Color.GREEN);
        Dimension loadButtonsPanelDim = new Dimension(
          LEVEL_SELECTION_PANE_WIDTH,
          (int)(0.1 * LEVEL_SELECTION_PANE_HEIGHT)
        );
        loadButtonsPanel.setPreferredSize(loadButtonsPanelDim);
        loadButtonsPanel.add(playableLevelsButton);
        loadButtonsPanel.add(editableLevelsButton);

        // Level buttons panel, show playable first
        this.loadPlayableLevelNames();
        this.loadEditableLevelNames();

        // Display playable levels
        this.displayFlag = true;
        this.levelButtonsPanel = displayLevelButtonsPanel();

        this.add(loadButtonsPanel, BorderLayout.PAGE_START);
        this.add(levelButtonsPanel, BorderLayout.CENTER);
    }

    // Creates a new JButton of given name and action listener
    private JButton createNamedButton(String name, ActionListener al) {
        JButton b = new JButton(name);
        b.addActionListener(al);
        return b;
    }

    // Creates the action listener corresponding to load button action,
    // depending on flag value: true for playable level names display, editable
    // otherwise
    private ActionListener createLoadButtonActionListener(
      boolean flag,
      JPanel lvlPane
    ) {
        return e -> {
            lvlPane.remove(levelButtonsPanel);
            displayFlag = flag;
            levelButtonsPanel = displayLevelButtonsPanel();
            lvlPane.add(levelButtonsPanel);
            lvlPane.revalidate();
            lvlPane.repaint();
        };
    }

    // If displayFlag is true, returns a panel with the playable level buttons,
    // editable level buttons otherwise.
    private JPanel displayLevelButtonsPanel() {
        JPanel levelButtonsPanel = new JPanel();
        Dimension levelButtonsPanelDim = new Dimension(
          LevelSelectionPane.LEVEL_SELECTION_PANE_WIDTH,
          (int)(0.9 * LevelSelectionPane.LEVEL_SELECTION_PANE_HEIGHT)
        );
        levelButtonsPanel.setPreferredSize(levelButtonsPanelDim);
        levelButtonsPanel.setBackground(Color.BLUE);
        List<String> names;
        if (displayFlag) names = this.playableLevelNames;
        else names = this.editableLevelNames;
        for (String name: names) {
            levelButtonsPanel.add(createLevelButton(name));
        }

        // add new level button if editable levels are displayed
        if (!displayFlag) {
            int newID = this.playableLevelNames.size() + 1;
            JButton newLevelButton = new JButton("New level [" + newID + "]");
            newLevelButton.addActionListener(e -> switcher.next(newID, false));
            levelButtonsPanel.add(newLevelButton);
        }
        return levelButtonsPanel;
    }

    // name must be of the form "levelINTEGER.nrg" where INTEGER is an integer
    private JButton createLevelButton(String name) {
        JButton b = new JButton(name);
        String idAsString = name.substring(5, name.length() - 4);
        int id = Integer.parseInt(idAsString);
        b.addActionListener(
          e -> switcher.next(id, displayFlag)
        );
        return b;
    }

    // Loads the names of the entries in the given directory name
    private List<String> loadFileNamesInDirectory(String dirName) {
        List<String> names = new ArrayList<>();
        Path dir;
        try {
            dir = Paths.get(App.INSTALL_DIR + "/" + dirName);
        } catch (Exception e) {
            return new ArrayList<>();
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file: stream) {
                names.add(file.getFileName().toString());
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return names;
    }

    private void loadPlayableLevelNames() {
        this.playableLevelNames = this.loadFileNamesInDirectory(
            LevelConfig.PLAYABLE_LEVEL_PATH_PREFIX + "/"
        );
    }

    private void loadEditableLevelNames() {
        this.editableLevelNames = this.loadFileNamesInDirectory(
            LevelConfig.EDITABLE_LEVEL_PATH_PREFIX + "/"
        );
    }
}
