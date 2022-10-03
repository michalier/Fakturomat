package Fakturomat.Panels;

import Fakturomat.Inputs.Manager;
import Fakturomat.Inputs.Ware;
import Fakturomat.NumberFilter;
import Fakturomat.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class WarePanel extends BaseWarePanel {
    public final JComboBox<Ware> waresBox = new JComboBox<>();

    public WarePanel(Integer[] number, Manager manager, WareScrolledPanel scrolledPanel) {
        this.setPreferredSize(new Dimension(530, 35));
        this.setLayout(null);

        // ===================================================

        index = new JLabel();
        this.setNumber(number[0]);
        index.setSize(new Dimension(20, 26));
        index.setLocation(new Point(15, 5));

        this.add(index);

        // ===================================================

        for (Ware w : manager.wares) {
            waresBox.addItem(w);
        }
        waresBox.setSize(new Dimension(245, 26));
        waresBox.setLocation(new Point(40, 5));

        this.add(waresBox);

        // ===================================================

        JLabel amountLabel = new JLabel("szt:");
        amountLabel.setSize(new Dimension(55, 26));
        amountLabel.setLocation(new Point(285, 5));
        amountLabel.setHorizontalAlignment(JLabel.RIGHT);

        this.add(amountLabel);

        // ===================================================

        amountField = new JTextField();
        amountField.addKeyListener(new NumberFilter());
        amountField.setHorizontalAlignment(JFormattedTextField.RIGHT);
        amountField.setSize(new Dimension(65, 26));
        amountField.setLocation(new Point(345, 5));

        this.add(amountField);

        // ===================================================

        JButton usunButton = new JButton();
        usunButton.setText("Usuń towar");
        usunButton.setSize(new Dimension(110, 26));
        usunButton.setLocation(new Point(420, 5));
        usunButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrolledPanel.removePanel(WarePanel.this);
                number[0]--;
            }
        });

        this.add(usunButton);

        // ===================================================

        waresBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                amountLabel.setText(((Ware) Objects.requireNonNull(waresBox.getSelectedItem())).measureUnit);
            }
        });
    }

    @Override
    public Pair<Ware, String> getValue() { return new Pair<>((Ware) waresBox.getSelectedItem(), amountField.getText()); }

    @Override
    public int getHeight() { return 35; }

    @Override
    public boolean hasValidValues() { return waresBox.getSelectedItem() != null && amountField.getText().length() > 0; }
}
