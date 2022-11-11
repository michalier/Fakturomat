package Fakturomat.Windows;

import Fakturomat.Data.BaseData;
import Fakturomat.Data.WZData;
import Fakturomat.Fakturomat;
import Fakturomat.Inputs.*;
import Fakturomat.Panels.BaseWarePanel;
import Fakturomat.Panels.WareEditablePanel;
import Fakturomat.Panels.WarePanel;
import Fakturomat.Panels.WareScrolledPanel;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.Locale;

public class WZAction extends AbstractAction {
    private final Fakturomat fakturomat;
    private final Manager manager;
    private final boolean edit;

    public JSpinner numberSpinner;
    public final JComboBox<Contractor> contractorBox;
    public final WareScrolledPanel scrolledPanel;
    public final Integer[] index;

    public WZAction(Fakturomat fakturomat, boolean edit) {
        this.fakturomat = fakturomat;
        this.manager = fakturomat.manager;
        this.edit = edit;

        contractorBox = new JComboBox<>();
        scrolledPanel = new WareScrolledPanel();
        index = new Integer[]{1};
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BaseData data = fakturomat.data;
        if (edit && data == null) return;

        JDialog dialog = new JDialog();
        dialog.setTitle(edit ? "Edytuj WZ" : "Nowy WZ");
        dialog.setSize(600, 420);
        dialog.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));

        // ===================================================

        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(500, 70));

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(300, 70));
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(190, 70));
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // ===================================================

        JLabel contractors = new JLabel("Odbiorca");
        leftPanel.add(contractors);

        // ===================================================

        contractorBox.setPreferredSize(new Dimension(200, 30));
        contractorBox.removeAllItems();

        for (Contractor c : manager.contractors) {
            contractorBox.addItem(c);
        }

        if (edit) {
            contractorBox.setSelectedItem(data.buyer);
        }

        leftPanel.add(contractorBox);

        // ===================================================

        DatePicker datePicker = new DatePicker();
        datePicker.setLocale(new Locale("pl"));
        datePicker.setDate(LocalDate.now());
        rightPanel.add(datePicker);

        // ===================================================

        JLabel number = new JLabel("Numer WZ");
        rightPanel.add(number);

        // ===================================================

        numberSpinner = new JSpinner(new SpinnerNumberModel(manager.lastWZNumber.intValue(), 1, 100, 1));
        numberSpinner.setPreferredSize(new Dimension(50, 25));

        if (edit) {
            numberSpinner.setValue(Integer.parseInt(data.number.split("/")[0]));
        }

        ((JSpinner.DefaultEditor) numberSpinner.getEditor()).getTextField().setEditable(false);

        rightPanel.add(numberSpinner);

        // ===================================================

        topPanel.add(leftPanel);
        topPanel.add(rightPanel);
        dialog.add(topPanel);

        // ===================================================

        scrolledPanel.panels.clear();
        scrolledPanel.setPreferredSize(new Dimension(540, 250));
        scrolledPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JScrollPane scrollPane = new JScrollPane(scrolledPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(570, 250));

        dialog.add(scrollPane);

        // ===================================================

        scrolledPanel.removeAll();
        index[0] = 0;

        if (edit) {
            data.wares.forEach((p) -> {
                Ware w = p.getKey();
                String s = p.getValue();

                BaseWarePanel warePanel;

                if (fakturomat.manager.wares.contains(w)) {
                    WarePanel wp = new WarePanel(index, manager, scrolledPanel);
                    wp.waresBox.setSelectedItem(w);
                    wp.amountField.setText(s);

                    warePanel = wp;
                } else {
                    WareEditablePanel wep = new WareEditablePanel(index, manager, scrolledPanel);
                    wep.nameField.setText(w.name);
                    wep.amountField.setText(s);
                    wep.vatField.setText(w.vat.toString());
                    wep.priceField.setText(w.price);

                    wep.measureBox.setSelectedItem(w.measureUnit);

                    warePanel = wep;
                }

                scrolledPanel.addPanel(warePanel);

                index[0]++;
            });
        } else {
            scrolledPanel.addPanel(new WarePanel(index, manager, scrolledPanel));
            index[0]++;
        }

        // ===================================================

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(550, 40));
        buttonPanel.setLayout(new FlowLayout());

        // ===================================================

        JButton addButton = new JButton("Dodaj towar");
        addButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrolledPanel.addPanel(new WarePanel(index, manager, scrolledPanel));
                index[0]++;

                scrolledPanel.updateUI();
            }
        });

        buttonPanel.add(addButton);

        // ===================================================

        JButton addNewButton = new JButton("Dodaj nowy towar");
        addNewButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrolledPanel.addPanel(new WareEditablePanel(index, manager, scrolledPanel));
                index[0]++;

                scrolledPanel.updateUI();
            }
        });

        buttonPanel.add(addNewButton);

        // ===================================================

        JButton confirmButton = new JButton("Zapisz");
        confirmButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WZData data = new WZData();

                data.buyer = (Contractor) contractorBox.getSelectedItem();

                scrolledPanel.panels.forEach(bp -> data.wares.add(bp.getValue()));

                data.dataWystawienia = datePicker.getDate();

                int ostatni = (Integer)numberSpinner.getValue();

                String numer = fakturomat.manager.numberFormat;
                numer = numer.replaceAll("Numer",   (ostatni < 10 ? "0" : "") + ostatni);
                numer = numer.replaceAll("Miesiąc", (data.dataWystawienia.getMonthValue() < 10 ? "0" : "") + (data.dataWystawienia.getMonthValue()));
                numer = numer.replaceAll("Rok",                Integer.toString(data.dataWystawienia.getYear()));

                data.number = numer;
                manager.lastWZNumber = ostatni + 1;

                data.seller = manager.seller;

                fakturomat.data = data;

                try {
                    String p = Fakturomat.path + "/" + data.number.replaceAll("/", "_") + ".wz";
                    File f = new File(p);

                    if (f.exists()) {
                        int res = JOptionPane.showOptionDialog(
                                fakturomat,
                                "Istnieje zapisnane WZ o numerze " + data.number + ". Nadpisać?",
                                "",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                new String[]{"Tak", "Nie"},
                                null);

                        if (res == 0) {
                            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
                            oos.writeObject(fakturomat.data);
                            oos.close();
                        }
                    } else {
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
                        oos.writeObject(fakturomat.data);
                        oos.close();
                    }
                } catch (Exception exc) {
                    System.out.println(exc.getMessage());
                }

                dialog.removeAll();
                dialog.dispose();

                fakturomat.graphicsPanel.redraw();
            }
        });

        buttonPanel.add(confirmButton);

        dialog.add(buttonPanel);

        // ===================================================

        dialog.setVisible(true);
    }
}
