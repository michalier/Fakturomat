package Fakturomat.Windows;

import Fakturomat.Inputs.Contractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Comparator;
import java.util.List;

public class ContractorAction extends MouseAdapter {
    private List<Contractor> contractors;
    private JComboBox<Contractor> contractorJComboBox;
    private JComboBox<Contractor> contractorBox;
    private final boolean editable;

    private Integer editNumber;

    public ContractorAction(JComboBox<Contractor> contractors, Integer toEdit) {
        this(contractors);
        editNumber = toEdit;
    }

    public ContractorAction(JComboBox<Contractor> contractors) {
        contractorJComboBox = contractors;
        this.editable = true;
    }

    public ContractorAction(List<Contractor> contractors) {
        this.contractors = contractors;
        this.editable = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Contractor toEdit = new Contractor("error", "error", "error");

        if (editable && editNumber != null) {
            editNumber = contractorJComboBox.getSelectedIndex();
            toEdit = contractorJComboBox.getItemAt(editNumber);
        }

        JDialog dialog = new JDialog();
        dialog.setTitle(editable ? "Nowy kontrachent" : "Kontrachenci");
        dialog.setSize(400, editable ? 255 : 295);
        dialog.setLayout(new FlowLayout());

        // ===================================================

        if (!editable) {
            JPanel pnl = new JPanel();
            pnl.setPreferredSize(new Dimension(400, 35));
            pnl.setLayout(new FlowLayout());

            contractorBox = new JComboBox<>();

            for (Contractor c : contractors) {
                contractorBox.addItem(c);
            }

            contractorBox.setPreferredSize(new Dimension(280, 26));
            pnl.add(contractorBox);

            dialog.add(pnl);
        }

        // ===================================================

        JPanel pnl1 = new JPanel();
        pnl1.setPreferredSize(new Dimension(400, 50));
        pnl1.setLayout(new FlowLayout());

        JLabel nameLabel = new JLabel("Nazwa:");
        nameLabel.setPreferredSize(new Dimension(50, 28));
        pnl1.add(nameLabel);

        JTextArea nameField = new JTextArea();
        nameField.setPreferredSize(new Dimension(300, 43));
        nameField.setLineWrap(true);
        nameField.setBorder(new JTextField().getBorder());
        nameField.setRows(2);
        nameField.setEditable(editable);
        if (editNumber != null) {
            nameField.setText(toEdit.name);
        }
        pnl1.add(nameField);

        dialog.add(pnl1);

        // ===================================================

        JPanel pnl2 = new JPanel();
        pnl2.setPreferredSize(new Dimension(400, 70));
        pnl2.setLayout(new FlowLayout());

        JLabel adressLabel = new JLabel("Adres:");
        adressLabel.setPreferredSize(new Dimension(50, 28));
        pnl2.add(adressLabel);

        JTextArea adressField = new JTextArea();
        adressField.setPreferredSize(new Dimension(300, 58));
        adressField.setLineWrap(true);
        adressField.setBorder(new JTextField().getBorder());
        adressField.setRows(3);
        adressField.setEditable(editable);
        if (editable && editNumber != null) {
            adressField.setText(toEdit.adress);
        }
        pnl2.add(adressField);

        dialog.add(pnl2);

        // ===================================================

        JPanel pnl3 = new JPanel();
        pnl3.setPreferredSize(new Dimension(400, 35));
        pnl3.setLayout(new FlowLayout());

        JLabel nipLabel = new JLabel("NIP:");
        nipLabel.setPreferredSize(new Dimension(50, 28));
        pnl3.add(nipLabel);

        JTextField nipField = new JTextField();
        nipField.setPreferredSize(new Dimension(300, 28));
        nipField.setEditable(editable);
        if (editNumber != null) {
            nipField.setText(toEdit.NIP);
        }
        pnl3.add(nipField);

        nameField.setBackground(nipField.getBackground());
        adressField.setBackground(nipField.getBackground());

        dialog.add(pnl3);

        // ===================================================

        JPanel pnl4 = new JPanel();
        pnl4.setPreferredSize(new Dimension(400, 35));
        pnl4.setLayout(new FlowLayout());

        if (editable) {
            JButton zapiszButton = new JButton("Zapisz");
            zapiszButton.setPreferredSize(new Dimension(70, 28));
            zapiszButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editNumber != null) {
                        contractorJComboBox.removeItemAt(editNumber);
                    }

                    contractorJComboBox.addItem(new Contractor(nameField.getText(), adressField.getText(), nipField.getText()));
                    dialog.dispose();
                }
            });
            pnl4.add(zapiszButton);

            // ===================================================

            JButton anulujButton = new JButton("Anuluj");
            anulujButton.setPreferredSize(new Dimension(70, 28));
            anulujButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            pnl4.add(anulujButton);
        } else {
            JPanel pnl4l = new JPanel();
            pnl4l.setPreferredSize(new Dimension(290, 35));
            pnl4l.setLayout(new FlowLayout());

            JButton newButton = new JButton("Nowy");
            newButton.setPreferredSize(new Dimension(70, 30));
            newButton.addMouseListener(new ContractorAction(contractorBox));
            pnl4l.add(newButton);

            // ===================================================

            JButton editButton = new JButton("Edytuj");
            editButton.setPreferredSize(new Dimension(70, 30));
            editButton.addMouseListener(new ContractorAction(contractorBox, 0));
            pnl4l.add(editButton);

            // ===================================================

            JButton deleteButton = new JButton("Usuń");
            deleteButton.setPreferredSize(new Dimension(70, 30));
            deleteButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int ind = contractorBox.getSelectedIndex();
                    if (ind >= 0) {
                        int res = JOptionPane.showOptionDialog(dialog, "Czy na pewno chcesz usunąć?", "Usuń", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, new String[]{"Tak", "Nie"}, null);

                        if (res == JOptionPane.YES_OPTION)
                            contractorBox.removeItemAt(ind);
                    }
                }
            });
            pnl4l.add(deleteButton);
            pnl4.add(pnl4l);

            // ===================================================

            JPanel pnl4r = new JPanel();
            pnl4r.setPreferredSize(new Dimension(100, 35));
            pnl4r.setLayout(new FlowLayout());

            JButton closeButton = new JButton("Zamknij");
            closeButton.setPreferredSize(new Dimension(80, 30));
            closeButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });

            pnl4r.add(closeButton);
            pnl4.add(pnl4r);

        }

        dialog.add(pnl4);

        // ===================================================

        if (!editable) {
            contractorBox.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Contractor c = (Contractor) contractorBox.getSelectedItem();

                    nameField.setText(c == null ? "" : c.name);
                    adressField.setText(c == null ? "" : c.adress);
                    nipField.setText(c == null ? "" : c.NIP);
                }
            });

            Runnable r = () -> {
                contractors.clear();

                for (int i = 0; i < contractorBox.getItemCount(); i++) {
                    contractors.add(contractorBox.getItemAt(i));
                }

                contractors.sort(Comparator.comparing(c -> c.name));
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

            if (contractorBox.getItemCount() > 0)
                contractorBox.setSelectedIndex(0);
        }

        dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
}
