package xyz.mkukri.SwingGraph.edge;

import xyz.mkukri.SwingGraph.geometry.Rectangle;

import java.awt.Graphics2D;

public interface EdgeRenderer {
    void renderEdge(Graphics2D graphics2D, Rectangle v1, Rectangle v2);
}
