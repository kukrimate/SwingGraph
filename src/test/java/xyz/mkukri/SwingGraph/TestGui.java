package xyz.mkukri.SwingGraph;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.*;

public class TestGui extends JFrame {
    private static Dimension DEFAULT_SIZE = new Dimension(800, 600);

    public TestGui() {
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
        SwingGraph swingGraph = new SwingGraph( );

        JPanel b1 = createVertexPanel("Test 1");
        b1.setLocation(0, 0);
        JPanel b2 = createVertexPanel("Test 2");
        b2.setLocation(100, 0);
        JPanel b3 = createVertexPanel("Test 3");
        b3.setLocation(0, 100);
        JPanel b4 = createVertexPanel("Test 4");
        b4.setLocation(100, 100);

        swingGraph.addVertex(b1);
        swingGraph.addVertex(b2);
        swingGraph.addVertex(b3);
        swingGraph.addVertex(b4);
        swingGraph.addEdge(b1, b3);
        swingGraph.addEdge(b1, b2);
        swingGraph.addEdge(b2, b4);
        swingGraph.addEdge(b3, b4);

        TestGui graphGui = new TestGui();
        graphGui.setContentPane(swingGraph);
        graphGui.pack();
        graphGui.setVisible(true);
    }
}
