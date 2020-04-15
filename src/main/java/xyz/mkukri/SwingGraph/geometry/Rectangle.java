package xyz.mkukri.SwingGraph.geometry;

import java.util.ArrayList;
import java.util.List;

public class Rectangle {
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle(java.awt.Rectangle awtRectangle) {
        this.x = awtRectangle.x;
        this.y = awtRectangle.y;
        this.width = awtRectangle.width;
        this.height = awtRectangle.height;
    }

    /**
     * Check if the rectangle contains the point
     * @param p point
     * @return does the rectangle contain the point?
     */
    public boolean containsPoint(Point p) {
        return x <= p.x && p.x <= x + width && y <= p.y && p.y <= y + height;
    }

    /**
     * Get the horizontal sides of the rectangle
     * @return horizontal sides
     */
    public List<Line> getHorizontalSides() {
        List<Line> sides = new ArrayList<>();
        sides.add(new Line(x, y, x + width, y));
        sides.add(new Line(x, y + height, x + width, y + height));
        return sides;
    }

    /**
     * Get the vertical sides of this rectangle
     * @return vertical sides
     */
    public List<Line> getVerticalSides() {
        List<Line> sides = new ArrayList<>();
        sides.add(new Line(x, y, x, y + height));
        sides.add(new Line(x + width, y, x + width, y + height));
        return sides;
    }
}
