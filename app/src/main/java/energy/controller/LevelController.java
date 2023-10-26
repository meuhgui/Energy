package energy.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import energy.model.Position;
import energy.view.CircuitView;

public abstract class LevelController extends MouseInputAdapter {

    static Position getTileAtClick(CircuitView cv, MouseEvent e) {
        Point click = e.getPoint();
        Point[][] points = cv.getPoints();
        
        // determine position of the nearest shape center to the click
        int minDist = Integer.MAX_VALUE;
        int l = Integer.MAX_VALUE;
        int c = Integer.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point center = points[i][j];
                if (center != null) {
                    int dist = (int) center.distance(click);
                    if (dist < minDist) {
                        minDist = dist;
                        l = i;
                        c = j;
                    }
                }
            }
        }

        // do nothing if click is outside Tile
        if (minDist > cv.getShapeSideLength())
            return null;

        // the position of the tile that has been clicked on
        return Position.at(l, c);
    }
}
