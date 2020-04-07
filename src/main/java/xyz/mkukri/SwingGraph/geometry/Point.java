package xyz.mkukri.SwingGraph.geometry;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(java.awt.Point awtPoint) {
        this.x = awtPoint.x;
        this.y = awtPoint.y;
    }
}
