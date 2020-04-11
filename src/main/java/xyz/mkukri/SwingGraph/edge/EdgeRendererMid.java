package xyz.mkukri.SwingGraph.edge;

import xyz.mkukri.SwingGraph.geometry.Point;
import xyz.mkukri.SwingGraph.geometry.Rectangle;
import xyz.mkukri.SwingGraph.geometry.Line;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

/**
 * Draws straight edges between the midpoint of the closest side of two vertices
 */
public class EdgeRendererMid implements EdgeRenderer {
    private final Color edgeColor;
    private final int edgeWidth;
    private final int arrowHeadSize;
    private final Polygon arrowHead;

    public EdgeRendererMid() {
        this(Color.black, 1, 5);
    }

    public EdgeRendererMid(Color edgeColor, int edgeWidth, int arrowHeadSize) {
        this.edgeColor = edgeColor;
        this.edgeWidth = edgeWidth;
        this.arrowHeadSize = arrowHeadSize;

        // Create arrow head polygon
        arrowHead = new Polygon();
        arrowHead.addPoint(0, arrowHeadSize);
        arrowHead.addPoint(-arrowHeadSize, -arrowHeadSize);
        arrowHead.addPoint(arrowHeadSize, -arrowHeadSize);
    }

    /**
     * Calculate the midpoint line between the closest sides of two rectangles
     * @param r1 rectangle 1
     * @param r2 rectangle 2
     * @return line
     */
    private Line midpointLine(Rectangle r1, Rectangle r2) {
        java.util.List<Line> sides1 = r1.getSides();
        List<Line> sides2 = r2.getSides();

        Line shortestLine = null;

        for (Line s1 : sides1) {
            xyz.mkukri.SwingGraph.geometry.Point m1 = s1.getMidpoint();
            for (Line s2 : sides2) {
                xyz.mkukri.SwingGraph.geometry.Point m2 = s2.getMidpoint();
                Line curLine = new Line(m1, m2);
                if (shortestLine == null || curLine.getLength() < shortestLine.getLength()) {
                    shortestLine = curLine;
                }
            }
        }

        return shortestLine;
    }

    @Override
    public void renderEdge(Graphics2D graphics2D, Rectangle v1, Rectangle v2) {
        Color oldColor = graphics2D.getColor();
        graphics2D.setColor(edgeColor);
        Stroke oldStroke = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(edgeWidth));
        AffineTransform oldTransform = graphics2D.getTransform();
        RenderingHints oldRenderingHints = graphics2D.getRenderingHints();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Render line
        Line line = midpointLine(v1, v2);
        graphics2D.drawLine(line.x1, line.y1, line.x2, line.y2);

        // Render arrow head
        AffineTransform transform = new AffineTransform();
        Point pos = line.pointFrom2(arrowHeadSize);
        transform.translate(pos.x, pos.y);
        transform.rotate(Math.atan2(line.y2 - line.y1, line.x2 - line.x1) - Math.PI / 2);
        graphics2D.setTransform(transform);
        graphics2D.fillPolygon(arrowHead);

        // Restore old settings
        graphics2D.setColor(oldColor);
        graphics2D.setStroke(oldStroke);
        graphics2D.setTransform(oldTransform);
        graphics2D.setRenderingHints(oldRenderingHints);
    }
}
