package Fakturomat.Windows;

import Fakturomat.Inputs.Ware;
import Fakturomat.NumberFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class WareAction extends MouseAdapter {
    private List<Ware> wares;
    private JComboBox<Ware> wareJComboBox;
    private JComboBox<Ware> wareBox;
    private final boolean editable;

    private Integer editNumber;

    public WareAction(JComboBox<Ware> wares, Integer toEdit) {
        this(wares);
        editNumber = toEdit;
    }

    public WareAction(JComboBox<Ware> wares) {
        wareJComboBox = wares;
        this.editable = true;
    }

    public WareAction(List<Ware> wares) {
        this.wares = wares;
        this.editable = false;
    }

    @Override
    @SuppressWarnings (value="unchecked")
    public void mouseClicked(MouseEvent e) {
        Ware toEdit = new Ware("error", "0.0", 0, "szt.");

        if (editable && editNumber != null) {
            editNumber = wareJComboBox.getSelectedIndex();
            toEdit = wareJComboBox.getItemAt(editNumber);
        }

        JDialog dialog = new JDialog();
        dialog.setTitle(editable ? "Edytuj Towar" : "Towary");
        dialog.setSize(400, editable ? 170 :210);
        dialog.setLayout(new FlowLayout());

        // ===================================================

        if (!editable) {
            JPanel pnl = new JPanel();
            pnl.setPreferredSize(new Dimension(400, 35));
            pnl.setLayout(new FlowLayout());

            wareBox = new JComboBox<>();

            for (Ware w : wares) {
                wareBox.addItem(w);
            }

            wareBox.setPreferredSize(new Dimension(280, 26));
            pnl.add(wareBox);

            dialog.add(pnl);
        }

        // ===================================================

        JPanel pnl1 = new JPanel();
        pnl1.setPreferredSize(new Dimension(400, 35));
        pnl1.setLayout(new FlowLayout());

        JLabel nameLabel = new JLabel("Nazwa:");
        nameLabel.setPreferredSize(new Dimension(50, 28));
        pnl1.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(300, 28));
        nameField.setEditable(editable);
        if (editNumber != null) {
            nameField.setText(toEdit.name);
        }
        pnl1.add(nameField);

        dialog.add(pnl1);

        // ===================================================

        JPanel pnl2 = new JPanel();
        pnl2.setPreferredSize(new Dimension(400, 35));
        pnl2.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        // ===================================================

        JPanel pnl2l = new JPanel();
        pnl2l.setPreferredSize(new Dimension(130, 35));
        pnl2l.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel priceLabel = new JLabel("Cena:");
        priceLabel.setPreferredSize(new Dimension(40, 28));
        pnl2l.add(priceLabel);

        JTextField priceField = new JTextField();
        priceField.addKeyListener(new NumberFilter());
        priceField.setPreferredSize(new Dimension(70, 28));
        priceField.setHorizontalAlignment(JTextField.RIGHT);
        priceField.setEditable(editable);
        if (editNumber != null) {
            priceField.setText(toEdit.price);
        }
        pnl2l.add(priceField);

        pnl2.add(pnl2l);

        // ===================================================

        JPanel pnl2c = new JPanel();
        pnl2c.setPreferredSize(new Dimension(130, 35));
        pnl2c.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel vatLabel = new JLabel("Vat:");
        vatLabel.setPreferredSize(new Dimension(35, 28));
        pnl2c.add(vatLabel);

        JTextField vatField = new JTextField();
        vatField.addKeyListener(new NumberFilter());
        vatField.setPreferredSize(new Dimension(70, 28));
        vatField.setHorizontalAlignment(JTextField.RIGHT);
        vatField.setEditable(editable);
        if (editNumber != null) {
            vatField.setText(toEdit.vat.toString());
        }
        pnl2c.add(vatField);

        pnl2.add(pnl2c);

        // ===================================================

        JPanel pnl2r = new JPanel();
        pnl2r.setPreferredSize(new Dimension(130, 35));
        pnl2r.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel measureLabel = new JLabel("JM:");
        measureLabel.setPreferredSize(new Dimension(35, 28));
        pnl2r.add(measureLabel);

        JComponent measure;

        if (editable) {
            JComboBox<String> measureBox = new JComboBox<>();
            List<String> elems = List.of(new String[]{"szt.", "1000 szt.", "kolor"});
            elems.forEach(measureBox::addItem);
            measureBox.setPreferredSize(new Dimension(70, 28));
            if (editNumber != null) {
                measureBox.setSelectedItem(toEdit.measureUnit);
            }

            measure = measureBox;
        } else {
            JTextField measureField = new JTextField();
            measureField.setPreferredSize(new Dimension(70, 28));
            measureField.setHorizontalAlignment(JTextField.RIGHT);
            measureField.setEditable(false);

            measure = measureField;
        }
        pnl2r.add(measure);
        pnl2.add(pnl2r);

        dialog.add(pnl2);

        // ===================================================

        JPanel pnl3 = new JPanel();
        pnl3.setPreferredSize(new Dimension(400, 35));
        pnl3.setLayout(new FlowLayout());

        if (editable) {
            JButton saveButton = new JButton("Zapisz");
            saveButton.setPreferredSize(new Dimension(70, 28));
            saveButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editNumber != null) {
                        wareJComboBox.removeItemAt(editNumber);
                    }

                    wareJComboBox.addItem(new Ware(nameField.getText(),
                                                priceField.getText(),
                                                Integer.parseInt(vatField.getText()),
                                                (String) ((JComboBox<String>)measure).getSelectedItem()));

                    dialog.dispose();
                }
            });
            pnl3.add(saveButton);

            // ===================================================

            JButton cancelButton = new JButton("Anuluj");
            cancelButton.setPreferredSize(new Dimension(70, 28));
            cancelButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            pnl3.add(cancelButton);
        } else {
            JPanel pnl3l = new JPanel();
            pnl3l.setPreferredSize(new Dimension(290, 35));
            pnl3l.setLayout(new FlowLayout());

            JButton newButton = new JButton("Nowy");
            newButton.setPreferredSize(new Dimension(70, 28));
            newButton.addMouseListener(new WareAction(wareBox));
            pnl3l.add(newButton);

            // ===================================================

            JButton editButton = new JButton("Edytuj");
            editButton.setPreferredSize(new Dimension(70, 28));
            editButton.addMouseListener(new WareAction(wareBox, 0));
            pnl3l.add(editButton);

            // ===================================================

            JButton deleteButton = new JButton("Usuń");
            deleteButton.setPreferredSize(new Dimension(70, 28));
            deleteButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int ind = wareBox.getSelectedIndex();
                    if (ind >= 0) {
                        int res = JOptionPane.showOptionDialog(dialog, "Czy na pewno chcesz usunąć?", "Usuń", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, null, null);

                        if (res == JOptionPane.YES_OPTION)
                            wareBox.removeItemAt(ind);
                    }
                }
            });

            pnl3l.add(deleteButton);
            pnl3.add(pnl3l);

            // ===================================================

            JPanel pnl3r = new JPanel();
            pnl3r.setPreferredSize(new Dimension(100, 35));
            pnl3r.setLayout(new FlowLayout());

            JButton closeButton = new JButton("Zamknij");
            closeButton.setPreferredSize(new Dimension(80, 30));
            closeButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });

            pnl3r.add(closeButton);
            pnl3.add(pnl3r);
        }

        dialog.add(pnl3);

        // ===================================================

        if (!editable) {
            wareBox.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Ware w = (Ware)wareBox.getSelectedItem();

                    nameField.setText(w == null ? "" : w.name);
                    priceField.setText(w == null ? "" : w.price);
                    vatField.setText(w == null ? "" : w.vat.toString());
                    ((JTextField) measure).setText(w == null ? "" : w.measureUnit);
                }
            });

            Runnable r = () -> {
                wares.clear();

                for (int i = 0; i < wareBox.getItemCount(); i++) {
                    wares.add(wareBox.getItemAt(i));
                }

                wares.sort(Comparator.comparing(o -> o.name));
            };

            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    r.run();
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    r.run();
                }
            });

            if (wareBox.getItemCount() > 0)
                wareBox.setSelectedIndex(0);
        }

        // ===================================================

        dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
}
