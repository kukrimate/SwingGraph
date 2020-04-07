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
     * Convert a rectangle to a list of vertices
     * @return list of vertices
     */
    public List<Point> getVertices() {
        List<Point> vertices = new ArrayList<>();
        vertices.add(new Point(x, y));
        vertices.add(new Point(x + width, y));
        vertices.add(new Point(x, y + height));
        vertices.add(new Point(x + width, y + height));
        return vertices;
    }

    /**
     * Convert a rectangle to a list of sides
     * @return list of sides
     */
    public List<Line> getSides() {
        List<Line> sides = new ArrayList<>();
        sides.add(new Line(x, y,x + width, y));
        sides.add(new Line(x, y, x, y + height));
        sides.add(new Line(x + width, y, x + width, y + height));
        sides.add(new Line(x, y + height, x + width, y + height));
        return sides;
    }
}
