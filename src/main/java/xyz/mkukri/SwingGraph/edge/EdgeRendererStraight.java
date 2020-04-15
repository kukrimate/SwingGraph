package xyz.mkukri.SwingGraph.edge;

import xyz.mkukri.SwingGraph.geometry.Point;
import xyz.mkukri.SwingGraph.geometry.Rectangle;
import xyz.mkukri.SwingGraph.geometry.Line;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * Find the sides facing each other of two rectangles and
 * draw straight edges between their midpoints
 */
public class EdgeRendererStraight implements EdgeRenderer {
    private final Color edgeColor;
    private final int edgeWidth;
    private final int arrowHeadSize;
    private final Polygon arrowHead;

    public EdgeRendererStraight() {
        this(Color.black, 1, 5);
    }

    public EdgeRendererStraight(Color edgeColor, int edgeWidth, int arrowHeadSize) {
        this.edgeColor = edgeColor;
        this.edgeWidth = edgeWidth;
        this.arrowHeadSize = arrowHeadSize;

        // Create arrow head polygon
        arrowHead = new Polygon();
        arrowHead.addPoint(0, arrowHeadSize);
        arrowHead.addPoint(-arrowHeadSize, -arrowHeadSize);
        arrowHead.addPoint(arrowHeadSize, -arrowHeadSize);
    }

    private Line getEdge(Rectangle r1, Rectangle r2) {
        Line h = EdgeRendererUtil.connectClosestMidpoints(r1.getHorizontalSides(), r2.getHorizontalSides());
        Line v = EdgeRendererUtil.connectClosestMidpoints(r1.getVerticalSides(), r2.getVerticalSides());
        return h.getLength() < v.getLength() ? h : v;
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
        Line line = getEdge(v1, v2);
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
