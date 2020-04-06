package xyz.mkukri.SwingGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Panel for displaying graphs of built from Swing components
 */
public class SwingGraph extends JPanel {
    private static final Color EDGE_COLOR = Color.black;
    private static final int EDGE_WIDTH = 1;
    private static final Color BORDER_COLOR_NORMAL = Color.lightGray;
    private static final Color BORDER_COLOR_MOVE = Color.darkGray;
    private static final Color BORDER_COLOR_RESIZE = Color.green;
    private static final int BORDER_WIDTH = 2;
    private static final int MIN_WIDTH = 10;
    private static final int MIN_HEIGHT = 10;

    /**
     * Edges in the current graph
     */
    private Set<Edge> edges = new HashSet<>();
    /**
     * List of resizable vertices
     */
    private Set<Component> resizableVertices = new HashSet<>();
    /**
     * Vertex targeted by the user
     */
    private Component targetVertex = null;
    /**
     * Pre-calculated polygon for an edge's arrow head
     */
    private final Polygon arrowHead;

    /**
     * Create a new GraphPanel
     */
    public SwingGraph() {
        setLayout(null);

        arrowHead = new Polygon();
        arrowHead.addPoint(0, 5);
        arrowHead.addPoint(-5, -5);
        arrowHead.addPoint(5, -5);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component clickedVertex = findVertex(e.getPoint());
                if (clickedVertex != null) {
                    if (!resizableVertices.contains(clickedVertex)) {
                        resizableVertices.add(clickedVertex);
                        updateCursor();
                        revalidate();
                        repaint();
                    }
                } else {
                    if (resizableVertices.size() > 0) {
                        resizableVertices.clear();
                        revalidate();
                        repaint();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            private Point lastPos = new Point(0, 0);

            @Override
            public void mouseDragged(MouseEvent e) {
                if (targetVertex == null)
                    return;
                if (resizableVertices.contains(targetVertex)) {
                    int newWidth = e.getX() - targetVertex.getX();
                    if (newWidth < MIN_WIDTH) {
                        newWidth = MIN_WIDTH;
                    }
                    int newHeight = e.getY() - targetVertex.getY();
                    if (newHeight < MIN_HEIGHT) {
                        newHeight = MIN_HEIGHT;
                    }
                    targetVertex.setBounds(targetVertex.getX(), targetVertex.getY(), newWidth, newHeight);
                } else {
                    Point offset = new Point((int) (targetVertex.getX() - lastPos.getX()),
                            (int) (targetVertex.getY() - lastPos.getY()));
                    targetVertex.setBounds(e.getX() + offset.x, e.getY() + offset.y,
                            targetVertex.getWidth(), targetVertex.getHeight());
                }
                revalidate();
                repaint();
                lastPos = e.getPoint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Component hoveredVertex = findVertex(e.getPoint());
                if (hoveredVertex != targetVertex) {
                    targetVertex = hoveredVertex;
                    revalidate();
                    repaint();
                }
                updateCursor(); // NOTE: we need to always update the cursor,
                                // because we might be coming from the inside of a vertex
                lastPos = e.getPoint();
            }
        });
    }

    /**
     * Update the cursor type
     */
    private void updateCursor() {
        if (resizableVertices.contains(targetVertex)) {
            setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
        } else if (targetVertex != null) {
            setCursor(new Cursor(Cursor.MOVE_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Calculate the corresponding border rectangle for a vertex
     * @param vertex vertex
     * @param width_offset extra "border" width (e.g. for accounting for the arrow head)
     * @return rectangle
     */
    private Rectangle getBorderRect(Component vertex, int width_offset) {
        Rectangle border = vertex.getBounds();
        border.x -= BORDER_WIDTH + width_offset;
        border.y -= BORDER_WIDTH + width_offset;
        border.width += 2 * (BORDER_WIDTH + width_offset);
        border.height += 2 * (BORDER_WIDTH + width_offset);
        return border;
    }

    /**
     * Get vertex based on position
     * @param p position
     * @return vertex
     */
    private Component findVertex(Point p) {
        for (Component vertex : getComponents()) {
            Rectangle border = getBorderRect(vertex,0);
            if (border.contains(p))
                return vertex;
        }
        return null;
    }

    /**
     * Find lines for each side of a rectangle
     * @param r rectangle
     * @return list of lines
     */
    private List<Line2D> rectSides(Rectangle r) {
        List<Line2D> lines = new ArrayList<>();
        lines.add(new Line2D.Float(r.x, r.y, r.x + r.width, r.y));
        lines.add(new Line2D.Float(r.x, r.y, r.x, r.y + r.height));
        lines.add(new Line2D.Float(r.x + r.width, r.y, r.x + r.width, r.y + r.height));
        lines.add(new Line2D.Float(r.x, r.y + r.height, r.x + r.width, r.y + r.height));
        return lines;
    }

    /**
     * Calculate a line connecting two rectangles
     * @param r1 first rectangle
     * @param r2 second rectangle
     * @return connecting line
     */
    private Line2D connectRects(Rectangle r1, Rectangle r2) {
        List<Line2D> sides1 = rectSides(r1);
        List<Line2D> sides2 = rectSides(r2);

        double shortestDistance = -1;
        Line2D shortestLine = null;

        for (Line2D s1 : sides1) {
            Point m1 = new Point((int) (s1.getX1() + s1.getX2()) / 2, (int) (s1.getY1() + s1.getY2()) / 2);
            for (Line2D s2 : sides2) {
                Point m2 = new Point((int) (s2.getX1() + s2.getX2()) / 2, (int) (s2.getY1() + s2.getY2()) / 2);
                double curDistance = Math.sqrt(Math.pow(m1.x - m2.x, 2) + Math.pow(m1.y - m2.y, 2));
                if (shortestDistance == -1 || curDistance < shortestDistance) {
                    shortestDistance = curDistance;
                    shortestLine = new Line2D.Float(m1.x, m1.y, m2.x, m2.y);
                }
            }
        }

        return shortestLine;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;
        Stroke oldStroke = g2D.getStroke();
        Color oldColor = g2D.getColor();

        // Enable AA, otherwise the edges look rough
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges
        g2D.setStroke(new BasicStroke(EDGE_WIDTH));
        g2D.setColor(EDGE_COLOR);

        for (Edge e : edges) {
            // Draw line for the arrow
            Line2D l = connectRects(getBorderRect(e.getV1(), 0), getBorderRect(e.getV2(), 5));
            g2D.drawLine((int) l.getX1(), (int)  l.getY1(), (int) l.getX2(), (int) l.getY2());
            // Draw the arrow head
            AffineTransform oldTransform = g2D.getTransform();
            AffineTransform transform = new AffineTransform();
            transform.translate(l.getX2(), l.getY2());
            transform.rotate(Math.atan2(l.getY2() - l.getY1(), l.getX2() - l.getX1()) - Math.PI / 2);
            g2D.setTransform(transform);
            g2D.fill(arrowHead);
            g2D.setTransform(oldTransform);
        }

        // Draw vertex borders
        for (Component vertex : getComponents()) {
            g2D.setColor(BORDER_COLOR_NORMAL);

            if (resizableVertices.contains(vertex))
                g2D.setColor(BORDER_COLOR_RESIZE);
            else if (targetVertex == vertex)
                g2D.setColor(BORDER_COLOR_MOVE);

            Rectangle borderRect = getBorderRect(vertex, 0);
            g2D.fillRect(borderRect.x, borderRect.y, borderRect.width, borderRect.height);
        }

        g2D.setStroke(oldStroke);
        g2D.setColor(oldColor);
    }

    /**
     * Add a vertex to the panel
     * @param v AWT or Swing component of the vertex
     */
    public void addVertex(Component v) {
        v.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        v.setSize(v.getPreferredSize()); // NOTE: since we have no layout, components wouldn't display otherwise
        add(v);
    }

    /**
     * Add an edge connecting v1 to v2
     * @param v1 AWT or Swing component of the v1
     * @param v2 AWT or Swing component of the v2
     */
    public void addEdge(Component v1, Component v2) {
        edges.add(new Edge(v1, v2));
    }

    /**
     * Represents an edge in a graph
     */
    private static class Edge {
        private Component v1;
        private Component v2;

        public Edge(Component v1, Component v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public Component getV1() {
            return v1;
        }

        public Component getV2() {
            return v2;
        }
    }
}
