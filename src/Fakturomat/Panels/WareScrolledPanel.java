package Fakturomat.Panels;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WareScrolledPanel extends JPanel {
    public List<BaseWarePanel> panels;
    private int height;

    public WareScrolledPanel() {
        height = 0;
        this.panels = new ArrayList<>();

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }

    public void addPanel(BaseWarePanel panel) {
        height += panel.getHeight();
        if (height > 250) {
            this.setPreferredSize(new Dimension(540, height));
        }

        this.panels.add(panel);
        this.add(panel);
    }

    public void removePanel(BaseWarePanel panel) {
        height -= panel.getHeight();
        if (height <= 250) {
            this.setPreferredSize(new Dimension(540, 250));
        } else {
            this.setPreferredSize(new Dimension(540, height));
        }

        this.panels.remove(panel);
        this.remove(panel);

        AtomicInteger i = new AtomicInteger(0);
        panels.forEach(p -> p.setNumber(i.getAndIncrement()));

        this.updateUI();
    }
}
