package xyz.mkukri.SwingGraph.geometry;

import xyz.mkukri.SwingGraph.Pair;

public class Line {
    public int x1;
    public int y1;
    public int x2;
    public int y2;

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Line(Point p1, Point p2) {
        this.x1 = p1.x;
        this.y1 = p1.y;
        this.x2 = p2.x;
        this.y2 = p2.y;
    }

    /**
     * Calculate a point d distance from (x2,y2) of the line
     * @param d distance
     * @return point
     */
    public Point pointFrom2(int d) {
        double ratio = d / getLength();
        return new Point((int) (ratio * (x1 - x2)) + x2, (int) (ratio * (y1 - y2)) + y2);
    }

    /**
     * Calculate the length of the line
     * @return length
     */
    public double getLength() {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * Calculate the midpoint of the line
     * @return midpoint
     */
    public Point getMidpoint() {
        return new Point((x1 + x2) / 2, (y1 + y2) / 2);
    }
}
