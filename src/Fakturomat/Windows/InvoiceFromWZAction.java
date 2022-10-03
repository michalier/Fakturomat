package Fakturomat.Windows;

import Fakturomat.Data.WZData;
import Fakturomat.Fakturomat;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class InvoiceFromWZAction extends AbstractAction {
    private final Fakturomat fakturomat;

    public InvoiceFromWZAction(Fakturomat fakturomat) {
        this.fakturomat = fakturomat;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JFileChooser jfc = new JFileChooser(Fakturomat.path);

            jfc.setFileFilter(new FileNameExtensionFilter("WZ", "wz"));
            jfc.showDialog(fakturomat, "Wybierz");

            File f = jfc.getSelectedFile();

            if (f != null && f.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                WZData wzData = (WZData) ois.readObject();
                ois.close();

                new InvoiceAction(fakturomat, wzData).actionPerformed(null);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
