package Fakturomat.Windows;

import Fakturomat.Fakturomat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsAction extends MouseAdapter {
    private final Fakturomat fakturomat;

    public SettingsAction(Fakturomat fakturomat) { this.fakturomat = fakturomat; }

    @Override
    public void mouseClicked(MouseEvent e) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ustawienia");
        dialog.setSize(470, 125);
        dialog.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));

        // ===================================================

        JPanel pnl1 = new JPanel();
        pnl1.setPreferredSize(new Dimension(450, 40));

        JLabel bankLabel = new JLabel("Bank:");
        bankLabel.setPreferredSize(new Dimension(40, 30));
        pnl1.add(bankLabel);

        JTextArea bankTextArea = new JTextArea();
        bankTextArea.setPreferredSize(new Dimension(400, 35));
        bankTextArea.setBorder(new JTextField().getBorder());
        bankTextArea.setRows(2);

        if (fakturomat.bankName != null) {
            bankTextArea.setText(fakturomat.bankName);
        }

        pnl1.add(bankTextArea);

        dialog.add(pnl1);

        // ===================================================

        JPanel pnl2 = new JPanel();
        pnl2.setPreferredSize(new Dimension(400, 30));

        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fakturomat.bankName = bankTextArea.getText();

                dialog.dispose();
            }
        });

        pnl2.add(saveButton);

        // ===================================================

        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        pnl2.add(cancelButton);

        dialog.add(pnl2);

        // ===================================================

        dialog.setVisible(true);
    }
}
