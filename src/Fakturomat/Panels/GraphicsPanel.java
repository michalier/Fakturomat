package Fakturomat.Panels;

import Fakturomat.Fakturomat;

import javax.swing.*;
import java.awt.*;

public class GraphicsPanel extends JPanel {
    private final Fakturomat data;

    public GraphicsPanel(Fakturomat f) {
        this.setPreferredSize(new Dimension(595, 841));
        this.data = f;
    }

    public void redraw() { this.paintComponent(this.getGraphics()); }

    @Override
    protected void paintComponent(Graphics g) {
        if (data.data != null) {
            data.data.drawGraphics(g);
        }
    }
}
