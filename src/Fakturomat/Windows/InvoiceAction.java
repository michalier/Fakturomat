package Fakturomat.Windows;

import Fakturomat.Data.*;
import Fakturomat.Fakturomat;
import Fakturomat.NumberFilter;
import Fakturomat.Inputs.*;
import Fakturomat.Panels.*;
import Fakturomat.Pair;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class InvoiceAction extends AbstractAction {
    private final Fakturomat fakturomat;
    private final Manager manager;
    private final boolean edit;
    private WZData wzData;

    public JSpinner numberSpinner;
    public final JComboBox<Contractor> contractorBox;
    public final WareScrolledPanel scrolledPanel;
    public final Integer[] index;

    public InvoiceAction(Fakturomat fakturomat, WZData data) {
        this(fakturomat, true);
        this.wzData = data;
    }

    public InvoiceAction(Fakturomat fakturomat, boolean edit) {
        this.fakturomat = fakturomat;
        this.manager = fakturomat.manager;
        this.edit = edit;
        this.wzData = null;

        contractorBox = new JComboBox<>();
        scrolledPanel = new WareScrolledPanel();
        index = new Integer[]{1};
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BaseData data = fakturomat.data;
        if (edit && data == null && wzData == null) return;

        JDialog dialog = new JDialog();
        dialog.setTitle(data == null ? "Nowa faktura" : "Edytuj fakture");
        dialog.setSize(600, 450);
        dialog.setLayout(new FlowLayout());

        // ===================================================

        JPanel pnl1 = new JPanel();
        pnl1.setPreferredSize(new Dimension(500, 40));

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(300, 40));
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(190, 40));
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // ===================================================

        JLabel contractors = new JLabel("Nabywca");
        leftPanel.add(contractors);

        // ===================================================

        contractorBox.setPreferredSize(new Dimension(200, 30));
        contractorBox.removeAllItems();

        for (Contractor c : manager.contractors) {
            contractorBox.addItem(c);
        }

        if (edit) {
            contractorBox.setSelectedItem(Objects.requireNonNullElse(wzData, data).buyer);
        }

        leftPanel.add(contractorBox);

        // ===================================================

        JLabel number = new JLabel("Numer faktury");
        rightPanel.add(number);

        // ===================================================

        numberSpinner = new JSpinner(new SpinnerNumberModel(manager.lastInvoiceNumber.intValue(), 1, 1000, 1));
        numberSpinner.setPreferredSize(new Dimension(50, 25));

        if (edit) {
            if (wzData == null) {
                assert data != null;
                numberSpinner.setValue(Integer.parseInt(data.number.split("/")[0]));
            }
        }

        ((JSpinner.DefaultEditor) numberSpinner.getEditor()).getTextField().setEditable(false);

        rightPanel.add(numberSpinner);

        // ===================================================

        pnl1.add(leftPanel);
        pnl1.add(rightPanel);
        dialog.add(pnl1);

        // ===================================================

        JPanel pnl2 = new JPanel();
        pnl2.setPreferredSize(new Dimension(580, 30));
        pnl2.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        // ===================================================

        JPanel pnl2l = new JPanel();
        pnl2l.setPreferredSize(new Dimension(290, 30));

        JLabel dateLabel1 = new JLabel();
        dateLabel1.setText("Data wydania");
        pnl2l.add(dateLabel1);

        DatePicker date1 = new DatePicker();
        if (edit) {
            if (wzData == null) {
                assert data != null;
                date1.setDate(((InvoiceData) data).dataWydania);
            } else {
                date1.setDate(wzData.dataWystawienia);
            }
        }
        pnl2l.add(date1);

        // ===================================================

        JPanel pnl2r = new JPanel();
        pnl2r.setPreferredSize(new Dimension(290, 30));

        JLabel dateLabel2 = new JLabel();
        dateLabel2.setText("Data wystawienia");

        pnl2r.add(dateLabel2);

        DatePicker date2 = new DatePicker();
        if (edit) {
            if (wzData == null) {
                assert data != null;
                date2.setDate(data.dataWystawienia);
            } else {
                date2.setDate(wzData.dataWystawienia);
            }
        }
        pnl2r.add(date2);

        // ===================================================

        pnl2.add(pnl2l);
        pnl2.add(pnl2r);

        dialog.add(pnl2);

        // ===================================================

        JPanel pnl3 = new JPanel();
        pnl3.setPreferredSize(new Dimension(580, 30));
        pnl3.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        // ===================================================

        JPanel pnl3l = new JPanel();
        pnl3l.setPreferredSize(new Dimension(320, 30));

        JLabel paymentMethodLabel = new JLabel();
        paymentMethodLabel.setText("Metoda płatności");

        pnl3l.add(paymentMethodLabel);

        JComboBox<String> paymentMethodBox = new JComboBox<>();
        paymentMethodBox.setPreferredSize(new Dimension(150, 26));
        paymentMethodBox.addItem("Przelew");
        paymentMethodBox.addItem("Gotówka");

        if (edit) {
            if (wzData == null) {
                assert data != null;
                paymentMethodBox.setSelectedItem(((InvoiceData) data).paymentMethod);
            }
        }

        pnl3l.add(paymentMethodBox);

        // ===================================================

        JPanel pnl3r = new JPanel();
        pnl3r.setPreferredSize(new Dimension(260, 30));

        JLabel paymentDateLabel = new JLabel();
        paymentDateLabel.setText("Termin");
        pnl3r.add(paymentDateLabel);

        JTextField paymentDueAmount = new JTextField();
        paymentDueAmount.setPreferredSize(new Dimension(80, 28));
        paymentDueAmount.addKeyListener(new NumberFilter());

        if (edit) {
            if (wzData == null) {
                assert data != null;
                paymentDueAmount.setText(String.valueOf(ChronoUnit.DAYS.between(data.dataWystawienia,
                                                                                ((InvoiceData) data).paymentDeadline)));
            }
        }

        pnl3r.add(paymentDueAmount);

        // ===================================================

        pnl3.add(pnl3l);
        pnl3.add(pnl3r);
        dialog.add(pnl3);

        // ===================================================

        scrolledPanel.panels.clear();
        scrolledPanel.setPreferredSize(new Dimension(540, 250));
        scrolledPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0,0));

        JScrollPane scrollPane = new JScrollPane(scrolledPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(570, 250));

        dialog.add(scrollPane);

        // ===================================================

        scrolledPanel.removeAll();
        index[0] = 0;

        if (edit) {
            Objects.requireNonNullElse(wzData, data).wares.forEach((w, s) -> {
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
                InvoiceData invoiceData = new InvoiceData();
                invoiceData.bankName = fakturomat.bankName;

                if (wzData != null) invoiceData.wzNumber = wzData.number;

                invoiceData.buyer = (Contractor) contractorBox.getSelectedItem();

                scrolledPanel.panels.forEach(bp -> {
                    Pair<Ware, String> value = bp.getValue();

                    invoiceData.wares.put(value.getKey(), value.getValue());
                });

                invoiceData.dataWydania = date1.getDate();
                invoiceData.dataWystawienia = date2.getDate();

                int ostatni = (Integer)numberSpinner.getValue();
                int month = invoiceData.dataWystawienia.getMonthValue();

                String numer = fakturomat.manager.numberFormat;
                numer = numer.replaceAll("Numer",   (ostatni < 10 ? "0" : "") + ostatni);
                numer = numer.replaceAll("Miesiąc", (month <= 9 ? "0" : "") + (month));
                numer = numer.replaceAll("Rok",                Integer.toString(invoiceData.dataWystawienia.getYear()));

                invoiceData.number = numer;
                manager.lastInvoiceNumber = ostatni + 1;

                invoiceData.seller = manager.seller;

                invoiceData.paymentMethod = (String) paymentMethodBox.getSelectedItem();
                invoiceData.paymentDeadline = invoiceData.dataWystawienia.plusDays(Integer.parseInt(paymentDueAmount.getText()));

                fakturomat.data = invoiceData;

                try {
                    String p = Fakturomat.path + "/" + fakturomat.data.number.replaceAll("/", "_") + ".faktura";
                    File f = new File(p);

                    if (f.exists()) {
                        int res = JOptionPane.showOptionDialog(
                                fakturomat,
                                "Istnieje zapisnana faktura o numerze " + invoiceData.number + ". Nadpisać?",
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

