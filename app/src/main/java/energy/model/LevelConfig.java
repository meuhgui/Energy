package energy.model;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import energy.App;

public class LevelConfig {
	public static final String PLAYABLE_LEVEL_PATH_PREFIX = "playable";
	public static final String EDITABLE_LEVEL_PATH_PREFIX = "editable";
	public static final String LEVEL_FILE_BASE_NAME = "level";
	public static final String FILE_FORMAT = ".nrg";	

    // height of circuit
	private int height;

    // width of circuit
	private int width;
	
	// ID of the Level (which should correspond to a file number)
	private int id;
	
	// Type of shape in this level
	private TileShape shape;
	
	// List of tiles contained in this LevelConfig
	private final ArrayList<Tile> tiles;
	
	// Initializes empty LevelConfig
	private LevelConfig() {	
		this.tiles = new ArrayList<>();
	}
	
	// Initializes LevelConfig with given parameters
	private LevelConfig(int height,
    					int width,
    					int id,
    					TileShape shape,
    					ArrayList<Tile> tiles) {
		this.height = height;
		this.width = width;
		this.id = id;
		this.shape = shape;
		this.tiles = tiles;
	}

	public static LevelConfig fromLevel(Level l) {
		// Get id
		int id = l.getId();
		
		// Get width and height
		Circuit circuit = l.getCircuit();
		Dimension dim = circuit.dimension();
		int height = dim.height;
		int width = dim.width;
		
		// Get tile shape
		TileShape ts = TileShape.SQUARE;
		if(circuit.getTiles().size() > 0)
			ts = circuit.getTiles().get(0).shape();
		
		// Return associated LevelConfig
		return new LevelConfig(height, width, id, ts, circuit.getTiles());
	}

	public static LevelConfig fromFile(String filepath) {
		LevelConfig res = new LevelConfig();
		// Step 1 : Convert file to list of string lines
		ArrayList<String> lines = new ArrayList<>();
		try {
            File file = new File(filepath);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine())
                lines.add(sc.nextLine());
            sc.close();
        } catch (Exception e) {
            return null;
        }
		
		try {
			// Step 2 : Initialize id from filename
			res.id = Integer.parseInt(filepath.replaceAll("[^0-9]", ""));
			
			// Step 3 : initialize attributes iterating on lines 
			for(int num_line = 0; num_line < lines.size(); num_line ++) {
				String currentString = lines.get(num_line);
				String[] tokens = currentString.split(" ");
				
				// Width / Height / Shape
				if(num_line == 0) {
					res.height = Integer.parseInt(tokens[0]);
					res.width = Integer.parseInt(tokens[1]);
					if(tokens[2].equals("H"))
						res.shape = TileShape.HEXAGON;
					if(tokens[2].equals("S"))
						res.shape = TileShape.SQUARE;
				} else { // Tiles construction
					int column = 0;
					Component currentComponent = null;
					ArrayList<Integer> currentConnectedSides =
							new ArrayList<>();
					for(String token : tokens) {
						if(token.equals(".") || token.equals("L") || token.equals("S") || token.equals("W")) {
							if(currentComponent!=null) {
								Tile t = Tile.of(res.shape, Position.at(num_line - 1, column), currentComponent);
								for(int i : currentConnectedSides) {
									t.connect(i);
								}								
								res.tiles.add(t);
								column++;
								currentComponent = Component.componentFromDiminutive(token);
								currentConnectedSides = new ArrayList<>();
							}
							else {
								currentComponent = Component.componentFromDiminutive(token);
							}
						}
						else {
							currentConnectedSides.add(Integer.valueOf(token));
						}
					}
					
					Tile t = Tile.of(res.shape, Position.at(num_line - 1, column), currentComponent);
					for(int i : currentConnectedSides) {
						t.connect(i);
					}					
					res.tiles.add(t);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return res;
	}
	
	private String toFileContent() {
		StringBuilder content = new StringBuilder();

		// Write first line => height, width and shape
		Circuit c = Circuit.empty();
		for(Tile t : tiles) {
			c.addTile(t);
		}
		Dimension dim = c.dimension();
		this.height = dim.height;
		this.width = dim.width;

		content
				.append(height)
				.append(" ")
				.append(width)
				.append(" ")
				.append(shape.fileIdentifier())
				.append("\n");
		
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				Tile cur = c.getTileAt(Position.at(i, j));
				String rep = (cur == null) ? "." : cur.levelRep();
				content.append(rep);
				if (j == this.width - 1) content.append("\n");
				else content.append(" ");
			}
		}
		return content.toString();
	}
	
	public void save() {
		try {
			String suffix = "/"
				+ LevelConfig.LEVEL_FILE_BASE_NAME
				+ id
				+ LevelConfig.FILE_FORMAT;
			String prefix = App.INSTALL_DIR + "/";
			File playable = new File(prefix
								   + LevelConfig.PLAYABLE_LEVEL_PATH_PREFIX
								   + suffix);
			File editable = new File(prefix
								   + LevelConfig.EDITABLE_LEVEL_PATH_PREFIX
								   + suffix);
			String content = this.toFileContent();								   	
			this.saveFile(playable, content);
			this.saveFile(editable, content);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void saveFile(File file, String content) throws IOException {
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.close();
	}
	
	// Getters / setters
	public int id() {
		return id;
	}

	public TileShape shape() {
		return shape;
	}

	public ArrayList<Tile> tiles() {
		return tiles;
	}
}