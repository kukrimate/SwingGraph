package xyz.mkukri.SwingGraph;

import xyz.mkukri.SwingGraph.geometry.Line;
import xyz.mkukri.SwingGraph.geometry.Point;
import xyz.mkukri.SwingGraph.geometry.Rectangle;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Panel for displaying graphs of built from Swing components
 */
public class SwingGraph extends JPanel {
    /**
     * Color of a vertex's border
     */
    private Color vertexBorderColor = Color.lightGray;
    /**
     * Color of a vertex's border when moving
     */
    private Color vertexBorderColorMove = Color.darkGray;
    /**
     * Color of a vertex's border when resizing
     */
    private Color vertexBorderColorResize = Color.green;
    /**
     * Width of a vertex's border in pixels
     */
    private int vertexBorderWidth = 2;
    /**
     * Minimum size of a vertex
     */
    private Dimension vertexMinSize = new Dimension(10, 10);
    /**
     * Color of an edge
     */
    private Color edgeColor = Color.black;
    /**
     * Width of an edge in pixels
     */
    private int edgeWidth = 1;
    /**
     * Size of the arrow head
     */
    private int arrowHeadSize = 5;
    /**
     * Pre-calculated polygon for an edge's arrow head
     */
    private Polygon arrowHead;
    /**
     * List of resizable vertices
     */
    private List<Component> resizableVertices = new ArrayList<>();
    /**
     * Vertex targeted by the user
     */
    private Component targetVertex = null;
    /**
     * Edges in the current graph
     */
    private Set<Edge> edges = new HashSet<>();

    /**
     * Create a new GraphPanel
     */
    public SwingGraph() {
        setLayout(null);
        recreateArrowHead();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component clickedVertex = findVertex(new Point(e.getPoint()));
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
                    if (newWidth < vertexMinSize.width) {
                        newWidth = vertexMinSize.width;
                    }
                    int newHeight = e.getY() - targetVertex.getY();
                    if (newHeight < vertexMinSize.height) {
                        newHeight = vertexMinSize.height;
                    }
                    targetVertex.setBounds(targetVertex.getX(), targetVertex.getY(), newWidth, newHeight);
                } else {
                    Point offset = new Point(targetVertex.getX() - lastPos.x, targetVertex.getY() - lastPos.y);
                    targetVertex.setBounds(e.getX() + offset.x, e.getY() + offset.y,
                            targetVertex.getWidth(), targetVertex.getHeight());
                }
                revalidate();
                repaint();
                lastPos = new Point(e.getPoint());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Component hoveredVertex = findVertex(new Point(e.getPoint()));
                if (hoveredVertex != targetVertex) {
                    targetVertex = hoveredVertex;
                    revalidate();
                    repaint();
                }
                updateCursor(); // NOTE: we need to always update the cursor,
                                // because we might be coming from the inside of a vertex
                lastPos = new Point(e.getPoint());
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
     * @return rectangle
     */
    private Rectangle getBorderRect(Component vertex) {
        Rectangle border = new Rectangle(vertex.getBounds());
        border.x -= vertexBorderWidth;
        border.y -= vertexBorderWidth;
        border.width += 2 * vertexBorderWidth;
        border.height += 2 * vertexBorderWidth;
        return border;
    }

    /**
     * Get vertex based on position
     * @param p position
     * @return vertex
     */
    private Component findVertex(Point p) {
        for (Component vertex : getComponents()) {
            Rectangle border = getBorderRect(vertex);
            if (border.containsPoint(p))
                return vertex;
        }
        return null;
    }

    /**
     * Calculate a line connecting two rectangles
     * @param r1 first rectangle
     * @param r2 second rectangle
     * @return connecting line
     */
    private Line connectRects(Rectangle r1, Rectangle r2) {
        List<Line> sides1 = r1.getSides();
        List<Line> sides2 = r2.getSides();

        Line shortestLine = null;

        for (Line s1 : sides1) {
            Point m1 = s1.getMidpoint();
            for (Line s2 : sides2) {
                Point m2 = s2.getMidpoint();
                Line curLine = new Line(m1, m2);
                if (shortestLine == null || curLine.getLength() < shortestLine.getLength()) {
                    shortestLine = curLine;
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
        g2D.setStroke(new BasicStroke(edgeWidth));
        g2D.setColor(edgeColor);

        for (Edge e : edges) {
            // Draw line for the arrow
            Line l = connectRects(getBorderRect(e.getV1()), getBorderRect(e.getV2()));
            g2D.drawLine(l.x1, l.y1, l.x2, l.y2);
            // Draw the arrow head
            AffineTransform oldTransform = g2D.getTransform();
            AffineTransform transform = new AffineTransform();
            Point arrowPoint = l.pointFrom2(5);
            transform.translate(arrowPoint.x, arrowPoint.y);
            transform.rotate(Math.atan2(l.y2 - l.y1, l.x2 - l.x1) - Math.PI / 2);
            g2D.setTransform(transform);
            g2D.fill(arrowHead);
            g2D.setTransform(oldTransform);
        }

        // Draw vertex borders
        for (Component vertex : getComponents()) {
            g2D.setColor(vertexBorderColor);

            if (resizableVertices.contains(vertex))
                g2D.setColor(vertexBorderColorResize);
            else if (targetVertex == vertex)
                g2D.setColor(vertexBorderColorMove);

            Rectangle borderRect = getBorderRect(vertex);
            g2D.fillRect(borderRect.x, borderRect.y, borderRect.width, borderRect.height);
        }

        g2D.setStroke(oldStroke);
        g2D.setColor(oldColor);
    }

    public Color getVertexBorderColor() {
        return vertexBorderColor;
    }

    public void setVertexBorderColor(Color vertexBorderColor) {
        this.vertexBorderColor = vertexBorderColor;
    }

    public Color getVertexBorderColorMove() {
        return vertexBorderColorMove;
    }

    public void setVertexBorderColorMove(Color vertexBorderColorMove) {
        this.vertexBorderColorMove = vertexBorderColorMove;
    }

    public Color getVertexBorderColorResize() {
        return vertexBorderColorResize;
    }

    public void setVertexBorderColorResize(Color vertexBorderColorResize) {
        this.vertexBorderColorResize = vertexBorderColorResize;
    }

    public int getVertexBorderWidth() {
        return vertexBorderWidth;
    }

    public void setVertexBorderWidth(int vertexBorderWidth) {
        this.vertexBorderWidth = vertexBorderWidth;
    }

    public Dimension getVertexMinSize() {
        return vertexMinSize;
    }

    public void setVertexMinSize(Dimension vertexMinSize) {
        this.vertexMinSize = vertexMinSize;
    }

    public Color getEdgeColor() {
        return edgeColor;
    }

    public void setEdgeColor(Color edgeColor) {
        this.edgeColor = edgeColor;
    }

    public int getEdgeWidth() {
        return edgeWidth;
    }

    public void setEdgeWidth(int edgeWidth) {
        this.edgeWidth = edgeWidth;
    }

    public int getArrowHeadSize() {
        return arrowHeadSize;
    }

    /**
     * Re-creates the pre-rendered arrow head polygon
     */
    private void recreateArrowHead() {
        arrowHead = new Polygon();
        arrowHead.addPoint(0, 5);
        arrowHead.addPoint(-5, -5);
        arrowHead.addPoint(5, -5);
    }

    /**
     * Change the arrow head's size
     * @param arrowHeadSize arrow head size
     */
    public void setArrowHeadSize(int arrowHeadSize) {
        this.arrowHeadSize = arrowHeadSize;
        recreateArrowHead();
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
