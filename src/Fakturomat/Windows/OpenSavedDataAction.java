package Fakturomat.Windows;

import Fakturomat.Data.InvoiceData;
import Fakturomat.Data.WZData;
import Fakturomat.Fakturomat;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class OpenSavedDataAction extends AbstractAction{
    private final Fakturomat fakturomat;
    private final String type;

    public OpenSavedDataAction(Fakturomat fakturomat, String type) {
        this.fakturomat = fakturomat;
        this.type = type;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JFileChooser jfc = new JFileChooser(Fakturomat.path);

            jfc.setFileFilter(new FileNameExtensionFilter(
                    switch (type) {
                        case "wz" -> "WZ";
                        case "faktura" -> "Faktury";
                        default -> "all";
                    },
                    switch (type) {
                        case "wz" -> "wz";
                        case "faktura" -> "faktura";
                        default -> "";
                    })
            );

            jfc.showDialog(fakturomat, "Wybierz");

            File f = jfc.getSelectedFile();

            if (f != null && f.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                switch (type) {
                    case "wz" -> fakturomat.data = (WZData) ois.readObject();
                    case "faktura" -> fakturomat.data = (InvoiceData) ois.readObject();
                }

                fakturomat.graphicsPanel.redraw();
                ois.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
