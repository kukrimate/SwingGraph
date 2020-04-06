package xyz.mkukri.SwingGraph;

import javax.swing.*;
import java.awt.*;

public class GraphGui extends JFrame {
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

    public GraphGui() {
        this.setPreferredSize(DEFAULT_SIZE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
    }

    private static JPanel createVertexPanel(String test) {
        JPanel p = new JPanel();
        GroupLayout l = new GroupLayout(p);
        l.setAutoCreateContainerGaps(true);
        l.setAutoCreateGaps(true);
        p.setLayout(l);

        JLabel label = new JLabel(test);
        JTextField textField = new JTextField("text editor field...");
        JButton button = new JButton("Button");

        l.setVerticalGroup(l.createSequentialGroup()
                .addComponent(label)
                .addComponent(textField)
                .addComponent(button));

        l.setHorizontalGroup(l.createParallelGroup()
                .addComponent(label)
                .addComponent(textField)
                .addComponent(button));

        return p;
    }

    public static void main(String[] args) {
        SwingGraph swingGraph = new SwingGraph();

        JPanel b1 = createVertexPanel("Test 1");
        JPanel b2 = createVertexPanel("Test 2");
        JPanel b3 = createVertexPanel("Test 3");
        JPanel b4 = createVertexPanel("Test 4");

        swingGraph.addVertex(b1);
        swingGraph.addVertex(b2);
        swingGraph.addVertex(b3);
        swingGraph.addVertex(b4);
        swingGraph.addEdge(b1, b3);
        swingGraph.addEdge(b1, b2);
        swingGraph.addEdge(b2, b4);
        swingGraph.addEdge(b3, b4);

        GraphGui graphGui = new GraphGui();
        graphGui.setContentPane(swingGraph);
        graphGui.pack();
        graphGui.setVisible(true);
    }
}