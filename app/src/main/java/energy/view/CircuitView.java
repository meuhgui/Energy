package energy.view;

import energy.model.ReadOnlyCircuit;
import energy.model.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

// The view of a Circuit
public class CircuitView extends JPanel {

    // A read only model
	ReadOnlyCircuit model;

    // The center of the tiles to draw
    Point[][] cPoints;

    // Stores the previous dimension of the Circuit to display 
    Dimension prevCircuitDimension;

	public CircuitView() {
		this.model = null;
        this.cPoints = null;
        this.prevCircuitDimension = null;
        this.setLayout(null);
        this.setBackground(Color.BLACK);
	}

    void setModel(ReadOnlyCircuit model) {
        this.model = model;
    }

    ReadOnlyCircuit getModel() {
        return this.model;
    }

    // Computes the length of the side of the shapes to draw on the screen
    public double getShapeSideLength() {
        // Model dimension
        Dimension cirDim = this.model.dimension();
        double cirW = cirDim.width;
        double cirH = cirDim.height;
        if (this.model.areAllSquaredTiles()) {
            return Math.min(this.getSize().width / cirW,
                    this.getSize().height / cirH);
        } else if (this.model.areAllHexagonalTiles()) {
            if (this.getSize().width/cirW < this.getSize().height/cirH) {
                double half = cirW / 2.0;
                double w = this.getSize().width;
                return w / (Math.floor(half) + 2.0 * Math.ceil(half));
            } else {
                return this.getSize().height / cirH / Math.sqrt(3.0);
            }
        } else {
            return 0.0;
        }
    }

    // Computes the center of the tiles based on screen and circuit dimensions
    private void loadShapeCenters() {
        // Model dimension
        Dimension cirDim = this.model.dimension();
        int cirW = cirDim.width;
        int cirH = cirDim.height;

        // initialize points
        this.cPoints = new Point[cirH][cirW];

        double shapeSideLength = this.getShapeSideLength();
        int len = (int)shapeSideLength;
        int x1;
        int y1;
        for (int i = 0; i < this.cPoints.length; i++) {
            for (int j = 0; j < this.cPoints[i].length; j++) {
                if (this.model.areAllSquaredTiles()) {
                    x1 = len / 2;
                    y1 = len / 2;
                    this.cPoints[i][j] = 
                        new Point(x1 + (j * len), y1 + (i * len));
                } else {
                    int hS = (int)(3.0 / 2.0 * shapeSideLength);
                    int vS = (int)(Math.sqrt(3.0) * shapeSideLength);
                    int oddos = vS / 2;

                    x1 = len;
                    y1 = vS / 2;
                    if (j % 2 == 0) {
                        this.cPoints[i][j] = 
                            new Point(x1 + (j * hS), y1 + (i * vS));
                    } else {
                        if (i == this.cPoints.length - 1)
                            continue;
                        this.cPoints[i][j] = 
                            new Point(x1 + (j * hS), y1 + oddos + (i * vS));
                    }
                }
            }
        }
    }

    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!this.model.dimension().equals(this.prevCircuitDimension)) {
            loadShapeCenters();
            this.prevCircuitDimension = this.model.dimension();
        }

 		Graphics2D g2d = (Graphics2D)g;
	 	double sideLength = this.getShapeSideLength();
	 	int tileH = 0, tileW = 0, posX = 0, posY = 0;
	 	for(Tile t : this.model.tiles()) {
	 		int line = t.position().getLine();
			int column = t.position().getColumn();
			Point point = cPoints[line][column];
			TileView tv = new TileView(t);
	 		if(point != null) {

		 		// Display hexagons
		 		if(this.model.areAllHexagonalTiles()) {
		 			tileW = (int) (2*sideLength);
			 		tileH = (int) (2*sideLength); // this is not hexagon height
                                                  // but BufferedImage height
		 			posX = (int) (point.getX() - tileW / 2);
	 				posY = (int) (point.getY()
                                  - (TileView.HEXAGON_IMAGE_SHAPE_HEIGHT * tileH
                                     / TileView.HEXAGON_IMAGE_SHAPE_WIDTH) / 2);
		 		}
		 		
		 		// Display squares
		 		if(this.model.areAllSquaredTiles()) {
		 			tileW = (int) sideLength;
		 			tileH = (int) sideLength;
		 			posX = (int) (point.getX() - tileW / 2);
	 				posY = (int) (point.getY() - tileH / 2);   
		 		}
		 		
		 		//Draw tile's border
	 			Image tmp = tv
                    .getBorderImage()
                    .getScaledInstance(tileW, tileH, Image.SCALE_SMOOTH);
 			    g2d.drawImage(tmp, posX, posY, null);
		 		
 			    //Draw cables
 			    for(int i = 0; i < tv.getCableImages().size(); i++) {
 			    	tmp = tv.getCableImages()
                        .get(i)
                        .getScaledInstance(tileW, tileH, Image.SCALE_SMOOTH);
 			    	BufferedImage dimg =
                        new BufferedImage(tileW,
                                          tileH,
                                          BufferedImage.TYPE_INT_ARGB);
 			    	Graphics2D gBF = dimg.createGraphics();
 			    	gBF.drawImage(tmp, 0, 0, null);
 			    	gBF.dispose();
 			    	
		 			// Rotate
 			    	BufferedImage rotatedImage =
                        new BufferedImage(tileW, tileH, dimg.getType());
 			    		
 			    	double angle = Math.toRadians(tv.getAngles().get(i));
 			    	AffineTransform at = new AffineTransform();
 			    	
 			    	if(model.areAllHexagonalTiles()) {
                        // Compute proportional height
 			    		int h = TileView.HEXAGON_IMAGE_SHAPE_HEIGHT * tileH
                            / TileView.HEXAGON_IMAGE_SHAPE_WIDTH;
                        at.rotate(angle, tileW / 2.0, h / 2.0);
 			    	} else {
 			    		at.rotate(angle,
                                tileW / 2.0,
                                tileH / 2.0);
 			    	}
 			    	
 			    	Graphics2D gIm = rotatedImage.createGraphics();
 			    	gIm.setTransform(at);
 			    	gIm.drawImage(dimg, 0, 0, null);
 			    	gIm.dispose();
 			    	
 			    	g2d.drawImage(rotatedImage, posX, posY, null);
 		        }
 			    
 			    //Draw tile's component
 			    if(tv.getComponentImage() != null) {
 			    	tmp = tv
                        .getComponentImage()
                        .getScaledInstance(tileW, tileH, Image.SCALE_SMOOTH);
 	 			    g2d.drawImage(tmp, posX, posY, null);
 			    }
	 		}
	 	}
    }
    
    // Returns a copy of the center points
    public Point[][] getPoints() {
        Dimension cirDim = this.model.dimension();
        int h = cirDim.height;
        int w = cirDim.width;
        Point[][] cpy = new Point[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (this.cPoints[i][j] != null)
                    cpy[i][j] = this.cPoints[i][j].getLocation();
            }
        }
        return cpy;
    }
}