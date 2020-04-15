package xyz.mkukri.SwingGraph.edge;

import xyz.mkukri.SwingGraph.geometry.Line;
import xyz.mkukri.SwingGraph.geometry.Point;
import xyz.mkukri.SwingGraph.geometry.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Find the sides facing each other of two rectangles and
 * draw orthogonally staggered edges between their midpoints
 */
public class EdgeRendererOrthogonal implements EdgeRenderer {
    private List<Line> getHorizontalEdge(Line l) {
        List<Line> lines = new ArrayList<>();
        Point m1 = new Point(l.x1, l.y1);
        Point m2 = new Point(l.x2, l.y2);
        double halfDistance = (m2.y - m1.y) / 2.0;
        Point h1 = new Point(m1.x, (int) (m1.y + halfDistance));
        Point h2 = new Point(m2.x, (int) (m2.y - halfDistance));
        lines.add(new Line(m1, h1));
        lines.add(new Line(m2, h2));
        lines.add(new Line(h1, h2));
        return lines;
    }

    private List<Line> getVerticalEdge(Line l) {
        List<Line> lines = new ArrayList<>();
        Point m1 = new Point(l.x1, l.y1);
        Point m2 = new Point(l.x2, l.y2);
        double halfDistance = (m2.x - m1.x) / 2.0;
        Point h1 = new Point((int) (m1.x + halfDistance), m1.y);
        Point h2 = new Point((int) (m2.x - halfDistance), m2.y);
        lines.add(new Line(m1, h1));
        lines.add(new Line(m2, h2));
        lines.add(new Line(h1, h2));
        return lines;
    }

    @Override
    public void renderEdge(Graphics2D graphics2D, Rectangle v1, Rectangle v2) {
        Line h = EdgeRendererUtil.connectClosestMidpoints(v1.getHorizontalSides(), v2.getHorizontalSides());
        Line v = EdgeRendererUtil.connectClosestMidpoints(v1.getVerticalSides(), v2.getVerticalSides());
        for (Line l : h.getLength() < v.getLength() ? getHorizontalEdge(h) : getVerticalEdge(v)) {
            graphics2D.drawLine(l.x1, l.y1, l.x2, l.y2);
        }
    }
}
