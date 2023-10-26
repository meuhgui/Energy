package energy;

import energy.controller.EditorController;
import energy.controller.GameController;
import energy.model.Component;
import energy.model.*;
import energy.view.LevelSelectionPane;
import energy.view.LevelView;
import energy.view.ScreenSwitch;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.*;

/**
 * Entry class of the program.
 */
public class App extends JFrame implements ScreenSwitch {
    private JPanel currentScreen = new LevelSelectionPane(this);
    public static final String INSTALL_DIR = 
        System.getProperty("user.home") + "/.energy";
    public static final String editableLevelsPath =
        App.INSTALL_DIR + "/" + LevelConfig.EDITABLE_LEVEL_PATH_PREFIX;
    public static final String playableLevelsPath = 
        App.INSTALL_DIR + "/" + LevelConfig.PLAYABLE_LEVEL_PATH_PREFIX;

    public App() {
        super("Energy");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.back();
        this.pack();
        this.setVisible(true);
    }

    // If displayGame is true, displays game screen, editor screen otherwise
    @Override public void next(int id, boolean displayGame) {
        String path;
        if (displayGame) {
            path = App.playableLevelsPath
                + "/"
                + LevelConfig.LEVEL_FILE_BASE_NAME
                + id
                + LevelConfig.FILE_FORMAT;
        } else {
            path = App.editableLevelsPath
                + "/"
                + LevelConfig.LEVEL_FILE_BASE_NAME
                + id
                + LevelConfig.FILE_FORMAT;
        }
        LevelConfig lc = LevelConfig.fromFile(path);
        if (lc == null) { // Level not found
            if (!displayGame) { // editor mode + level not found => new level
                TileShape shape = this.askTileShape();
                if (shape == null) {
                    this.back();
                    return;
                } else {
                    Circuit circuit = Circuit.empty();
                    Tile tile = Tile.of(
                        shape,
                        Position.origin(),
                        Component.EMPTY
                    );
                    circuit.addTile(tile);
                    lc = LevelConfig.fromLevel(Level.from(id, circuit));
                }
            } else { // go back to level selection pane otherwise
                this.back();
                return;
            }
        }

        LevelView levelview = new LevelView(id, this, displayGame);
        if (displayGame) {
            PlayableLevel model = Level.fromLevelConfig(lc);
            model.addObserver(levelview);
            GameController gc = new GameController(model);
            levelview.getCircuitView().addMouseListener(gc);
        } else {
            EditableLevel model = Level.fromLevelConfig(lc);
            model.addObserver(levelview);
            EditorController ec = new EditorController(model);
            levelview.getCircuitView().addMouseListener(ec);
            levelview.setEditorController(ec);
            levelview.getCircuitView().addMouseMotionListener(ec);
        }
        this.changeScreen(levelview);
    }

    // Displays a dialog where the user is asked to choose a tile shape
    // for the new circuit to create
    private TileShape askTileShape() {
        Object[] possibilities = TileShape.values();
        return (TileShape)JOptionPane.showInputDialog(
                this,
            "Select tile shape",
            "Choose Tile shape",
            JOptionPane.PLAIN_MESSAGE,
            null,
            possibilities,
            TileShape.SQUARE
        );
    }

    // Displays level selection screen
    @Override public void back() {
        this.changeScreen(new LevelSelectionPane(this));
    }

    // Removes the currentScreen and puts the given pane as new current screen
    private void changeScreen(JPanel newPane) {
        Container contentPane = this.getContentPane();
        contentPane.remove(currentScreen);
        currentScreen = newPane;
        contentPane.add(currentScreen);
        this.revalidate();
        this.repaint();
    }

    // Copies the content of the src directory to the dst directory
    private static void copyContent(Path src, Path dst) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(src)) {
            for (Path entry : stream) {
                Path entryCopy = Paths.get(
                        dst.toString() + "/" + entry.getFileName().toString()
                );
                Files.copy(entry,
                           entryCopy,
                           StandardCopyOption.COPY_ATTRIBUTES
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Entry point.
     * @param args the arguments of the program
     */
    public static void main(String[] args) {
        /* energy install repository is `~/.energy` */
        Path nrgPath = Paths.get(App.INSTALL_DIR);

        /* Load level files in installation folder */
        if (Files.notExists(nrgPath, LinkOption.NOFOLLOW_LINKS)) {
            Path editPath = Paths.get(App.editableLevelsPath);
            Path playPath = Paths.get(App.playableLevelsPath);
            try {
                Files.createDirectories(editPath); /* also creates .energy */
                Files.createDirectories(playPath);

                ClassLoader loader = App.class.getClassLoader();
                
                /* copy editable files from resources */
                Path editSrc = new File(loader
                    .getResource(LevelConfig.EDITABLE_LEVEL_PATH_PREFIX + "/")
                    .toURI()
                ).toPath();
                App.copyContent(editSrc, editPath);

                /* copy playable files from resources */
                Path playSrc = new File(loader
                    .getResource(LevelConfig.PLAYABLE_LEVEL_PATH_PREFIX + "/")
                    .toURI()
                ).toPath();
                App.copyContent(playSrc, playPath);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        SwingUtilities.invokeLater(App::new);
    }
}
