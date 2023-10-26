package energy.view;

import energy.model.Component;
import energy.model.Position;
import energy.model.Tile;
import energy.model.TileShape;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileView{
	// Tile's resources load in a dictionary
	private static Map<String, BufferedImage> loadedRes = null;
	
    private final Tile model;

	// Tiles information
	private final String isPoweredStr;

	private String componentFilename = null;
	private final ArrayList<String> cableFilenames = new ArrayList<>();
	private final ArrayList<Integer> angles = new ArrayList<>();
	
	// As bufferedImages
	private BufferedImage borderImage;
	private BufferedImage componentImage;
	private final ArrayList<BufferedImage> cableImages = new ArrayList<>();
	
	// Info about shape dimension on initial image (120 * 120 for square and
    // 120*104 for hexagons)
	static final int SQUARE_IMAGE_SHAPE_WIDTH = 120;
	static final int SQUARE_IMAGE_SHAPE_HEIGHT = 120;
	static final int HEXAGON_IMAGE_SHAPE_WIDTH = 120;
	static final int HEXAGON_IMAGE_SHAPE_HEIGHT = 104;
	
	private static final String TILE_IMAGES_FOLDER = "tuiles_png/";
	private static final String SQUARE_IMAGES_FOLDER = "square/";
	private static final String HEXAGON_IMAGES_FOLDER = "hexagon/";

	public TileView(Tile t){
        this.model = t;

		// Load resources if it hasn't been done yet
		if(loadedRes == null) {
			try {
				TileView.loadResources();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Get powered as string
		if(model.isPowered()) {
			isPoweredStr = "on";
		} else {
			isPoweredStr = "off";
		}
		
		// Find all file needed to display this tile
		this.initFilenamesAttributes();
	}
	
	/**
     * Function to initialize all file_names attributes needed to display this
     * tile
     */  
	public void initFilenamesAttributes() {
        cableFilenames.clear();
        angles.clear();
        
        Component component = model.component();
        String modelShapeStr = model.shape().toString().toLowerCase();
        String modelComponentStr = component.toString().toLowerCase();
        String shapeImageFolder;
        if(model.shape() == TileShape.HEXAGON) {
        	shapeImageFolder = HEXAGON_IMAGES_FOLDER;
        } else {
        	shapeImageFolder = SQUARE_IMAGES_FOLDER;
        }

		// --- 1 : Find borders image
		// FileNames of element to display
		String borderFilename = shapeImageFolder
				+ modelShapeStr
				+ "_"
				+ isPoweredStr
				+ ".png";
		
 		// --- 2 : Find component image
		if (component != Component.EMPTY && component != Component.SOURCE) {
			componentFilename = shapeImageFolder
                + modelShapeStr
                + "_"
                + modelComponentStr
                + "_"
                + isPoweredStr
                + ".png";
		}

		if (component == Component.SOURCE) {
			componentFilename = shapeImageFolder
                + modelShapeStr
                + "_"
                + modelComponentStr
                + ".png";
		}
		
		// --- 3 : Find list of cable images
		// Easy case : Tile has a component
		// Or if it is empty and with only one connected side
		if (component != Component.EMPTY ||
			(component == Component.EMPTY && model.connectedSides() == 1)) {
            int len = model.length();
			for (int i = 0; i < len; i++) {
				if (model.side(i)) {
					if (model.shape() == TileShape.HEXAGON) {
						cableFilenames.add(shapeImageFolder
                                           + "hexagon_link1_"
                                           + isPoweredStr
                                           + ".png"
                        );
						angles.add(i * (360 / 6));
					}
					if (model.shape() == TileShape.SQUARE) {
						cableFilenames.add(shapeImageFolder
                                           + "square_link1_"
                                           + isPoweredStr
                                           + ".png"
                        );
						angles.add(i * (360 / 4));
					}
				}
			}
		} else { // More complicated case : When tile doesn't have any component
            ArrayList<Integer> connected = new ArrayList<>();
			// Iterate on each border and on distance between links
            int len = model.length();
			for(int dist = 1; dist < len; dist++) {
                // Connected in this for_loop
				ArrayList<Integer> newConnected = new ArrayList<>();
				for(int i = 0; i < len; i++) {
					if(model.side(i)
                       && model.side((i + dist) % len)
                       && (!connected.contains((i+dist)%(len))
                           || !connected.contains(i))) {
						newConnected.add(i);
						newConnected.add((i+dist)%(len));

						if(model.shape() == TileShape.SQUARE) {
							// Computation of angle
							this.angles.add(i * (360 / 4));
							// Find correct image
							if (dist == 1 || dist == 3) {
								cableFilenames.add(shapeImageFolder
                                                   + "square_link2_"
                                                   + isPoweredStr
                                                   + ".png"
                                );
							}
							if (dist == 2) {
								cableFilenames.add(shapeImageFolder
                                                   + "square_link3_"
                                                   + isPoweredStr
                                                   + ".png"
                                );
							}
						}

						if(model.shape() == TileShape.HEXAGON) {
							// Computation of angle
							this.angles.add(i * (360 / 6));
							// Find correct image
							if (dist==1 || dist==5) {
								cableFilenames.add(shapeImageFolder
                                                   + "hexagon_link2_"
                                                   + isPoweredStr
                                                   + ".png"
                                );
							}
							if (dist==2 || dist==4) {
								cableFilenames.add(shapeImageFolder
                                                   + "hexagon_link3_"
                                                   + isPoweredStr
                                                   + ".png"
                                );
							}
							if (dist==3) {
								cableFilenames.add(shapeImageFolder
                                                   + "hexagon_link4_"
                                                   + isPoweredStr
                                                   + ".png");
							}
						}
					}
				}
				connected.addAll(newConnected);
			}
			
			
			// Particular case for hexagon, hardcoded for now
			// Case : shape hexagon, connected borders : 0, 1, 3, 4 -> without
            // this case it would create two non-connected cable roads
			if(model.shape() == TileShape.HEXAGON){
				List<Integer> disconnected =
                    model.getDisconnectedSidesAsIndices();
				if (model.length() - model.connectedSides() == 2
                   	&& disconnected.get(0) == (disconnected.get(1)+3)%6) {
					cableFilenames.clear();
					angles.clear();
								
					cableFilenames.add(shapeImageFolder
                                       + "hexagon_link2_"
                                       + isPoweredStr
                                       + ".png"
                    );
					angles.add((disconnected.get(0)+1)%6 * 360 / 6);
					cableFilenames.add(shapeImageFolder
                                       + "hexagon_link2_"
                                       + isPoweredStr
                                       + ".png"
                    );
					angles.add((disconnected.get(1)+1)%6 * 360 / 6);
					cableFilenames.add(shapeImageFolder
                                       + "hexagon_link3_"
                                       + isPoweredStr
                                       + ".png"
                    );
					angles.add((disconnected.get(0)-1)%6 * 360 / 6);
				}
			}
			
		}
		
		this.borderImage = TileView.loadedRes.get(borderFilename);
		this.componentImage = TileView.loadedRes.get(this.componentFilename);
		for (String cableFilename : cableFilenames) {
			BufferedImage cableImage = TileView
					.loadedRes
					.get(cableFilename);
			cableImages.add(cableImage);
		}
	}	
	
	/**
     * Initialize resources loaded as images in a hashmap
     */ 
	public static void loadResources() throws URISyntaxException, IOException {
		loadedRes = new HashMap<>();
		
		// Get list of square images files
		URL folderSquareURL = TileView
            .class
            .getClassLoader()
            .getResource(TILE_IMAGES_FOLDER + SQUARE_IMAGES_FOLDER);
		File folderSquareFiles = new File(folderSquareURL.toURI());
		String[] squareFileNames = folderSquareFiles.list();
		
		// Get list of hexagon images files
		URL folderHexagonURL = TileView
            .class
            .getClassLoader()
            .getResource(TILE_IMAGES_FOLDER + HEXAGON_IMAGES_FOLDER);
		File folderHexagonFiles = new File(folderHexagonURL.toURI());
		String[] hexagonFileNames = folderHexagonFiles.list();
		
		// Load resources as images from files names
		for(String s: squareFileNames) {
            TileView.loadResource(SQUARE_IMAGES_FOLDER + s);
        }
		for(String s: hexagonFileNames) {
            TileView.loadResource(HEXAGON_IMAGES_FOLDER + s);
        }
	}
	
	/**
     * load the given file name resource (file_name must be
     * square|hexagon/file_name)
     */
	public static void loadResource(String fileName) throws
        URISyntaxException, IOException {
		// Files containing images
		URL resource = TileView
            .class
            .getClassLoader()
            .getResource(TILE_IMAGES_FOLDER + fileName);
		File f = new File(resource.toURI());
		
		// Create associated BufferedImage
		BufferedImage bi = ImageIO.read(f);
		TileView.loadedRes.put(fileName, bi);
	}

    public Position getPosition() {
        return this.model.position();
    }

	public ArrayList<Integer> getAngles(){
		return this.angles;
	}
	
	public BufferedImage getBorderImage() {
		return this.borderImage;
	}
	
	public BufferedImage getComponentImage() {
		return this.componentImage;
	}
	
	public ArrayList<BufferedImage> getCableImages(){
		return this.cableImages;
	}
}