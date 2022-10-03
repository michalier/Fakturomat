package Fakturomat.Panels;

import Fakturomat.Inputs.Manager;
import Fakturomat.Inputs.Ware;
import Fakturomat.NumberFilter;
import Fakturomat.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;

public class WareEditablePanel extends BaseWarePanel {
    public final JTextField nameField, vatField, priceField;
    public final JComboBox<String> measureBox;

    public WareEditablePanel(Integer[] number, Manager manager, WareScrolledPanel scrolledPanel) {
        this.setPreferredSize(new Dimension(530, 70));
        this.setLayout(null);

        // ===================================================

        JPanel topPanel = new JPanel();
        topPanel.setSize(new Dimension(420, 35));
        topPanel.setLocation(new Point(0, 0));
        topPanel.setLayout(null);

        // ===================================================

        this.index = new JLabel();
        this.setNumber(number[0]);

        index.setSize(new Dimension(20, 26));
        index.setLocation(new Point(15, 5));

        topPanel.add(index);

        this.add(topPanel);

        // ===================================================

        JLabel nameLabel = new JLabel();
        nameLabel.setText("Nazwa:");

        nameLabel.setSize(new Dimension(40, 26));
        nameLabel.setLocation(new Point(40, 5));

        topPanel.add(nameLabel);

        // ===================================================

        nameField = new JTextField();
        nameField.setSize(new Dimension(200, 28));
        nameField.setLocation(new Point(85, 5));

        topPanel.add(nameField);

        // ===================================================

        JLabel amountLabel = new JLabel();
        amountLabel.setText("szt:");
        amountLabel.setSize(new Dimension(55, 26));
        amountLabel.setLocation(new Point(285, 5));
        amountLabel.setHorizontalAlignment(JLabel.RIGHT);

        topPanel.add(amountLabel);

        // ===================================================

        amountField = new JTextField();
        amountField.addKeyListener(new NumberFilter());
        amountField.setHorizontalAlignment(JFormattedTextField.RIGHT);
        amountField.setSize(new Dimension(65, 26));
        amountField.setLocation(new Point(345, 5));

        topPanel.add(amountField);

        // ===================================================

        JPanel bottomPanel = new JPanel();
        bottomPanel.setSize(new Dimension(420, 35));
        bottomPanel.setLocation(new Point(0, 35));

        this.add(bottomPanel);

        // ===================================================

        JLabel priceLabel = new JLabel();
        priceLabel.setText("Cena:");

        bottomPanel.add(priceLabel);

        // ===================================================

        priceField = new JTextField();
        priceField.setPreferredSize(new Dimension(70, 28));
        priceField.addKeyListener(new NumberFilter());
        bottomPanel.add(priceField);

        // ===================================================

        JLabel vatLabel = new JLabel();
        vatLabel.setText("Vat:");

        bottomPanel.add(vatLabel);

        // ===================================================

        vatField = new JTextField();
        vatField.setPreferredSize(new Dimension(70, 28));
        vatField.addKeyListener(new NumberFilter());
        bottomPanel.add(vatField);

        // ===================================================

        JLabel measureLabel = new JLabel();
        measureLabel.setText("JM:");

        bottomPanel.add(measureLabel);

        // ===================================================

        measureBox = new JComboBox<>();
        measureBox.setPreferredSize(new Dimension(80, 28));

        List.of(new String[]{"szt.", "1000 szt.", "kolor"}).forEach(measureBox::addItem);

        bottomPanel.add(measureBox);

        // ===================================================

        JPanel rightPanel = new JPanel();
        rightPanel.setSize(new Dimension(110, 70));
        rightPanel.setLocation(new Point(420, 0));
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 5));

        // ===================================================

        JButton deleteButton = new JButton("Usuń towar");
        deleteButton.setPreferredSize(new Dimension(110, 26));
        deleteButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrolledPanel.removePanel(WareEditablePanel.this);
                number[0]--;
            }
        });

        rightPanel.add(deleteButton);

        // ===================================================

        JButton saveButton = new JButton("Zapisz towar");
        saveButton.setPreferredSize(new Dimension(110, 26));
        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameField.getText().length() > 0 && vatField.getText().length() > 0 &&
                        priceField.getText().length() > 0 && measureBox.getSelectedItem() != null) {
                    manager.wares.add(new Ware(nameField.getText(), priceField.getText(),
                            Integer.parseInt(vatField.getText()), (String) measureBox.getSelectedItem()));
                }
            }
        });

        rightPanel.add(saveButton);

        this.add(rightPanel);

        measureBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                amountLabel.setText((String) Objects.requireNonNull(measureBox.getSelectedItem()));
            }
        });
    }

    @Override
    public Pair<Ware, String> getValue() {
        return new Pair<>(new Ware(nameField.getText(), priceField.getText(),
                                    Integer.parseInt(vatField.getText()), (String) measureBox.getSelectedItem()),
                            amountField.getText());
    }

    @Override
    public int getHeight() { return 70; }

    @Override
    public boolean hasValidValues() {
        return nameField.getText().length() > 0 && vatField.getText().length() > 0 && priceField.getText().length() > 0 &&
                measureBox.getSelectedItem() != null && amountField.getText().length() > 0;
    }
}
