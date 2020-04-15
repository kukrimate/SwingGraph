package xyz.mkukri.SwingGraph.edge;

import xyz.mkukri.SwingGraph.Pair;
import xyz.mkukri.SwingGraph.geometry.Line;
import xyz.mkukri.SwingGraph.geometry.Point;
import xyz.mkukri.SwingGraph.geometry.Rectangle;

import java.util.List;

public class EdgeRendererUtil {
    public static Line connectClosestMidpoints(List<Line> lines1, List<Line> lines2) {
        Line closestLine = null;
        for (Line line1 : lines1) {
            Point mid1 = line1.getMidpoint();
            for (Line line2 : lines2) {
                Point mid2 = line2.getMidpoint();
                Line currentLine = new Line(mid1, mid2);
                if (closestLine == null || currentLine.getLength() < closestLine.getLength()) {
                    closestLine = currentLine;
                }
            }
        }
        return closestLine;
    }
}
