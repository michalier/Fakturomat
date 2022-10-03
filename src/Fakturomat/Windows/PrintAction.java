package Fakturomat.Windows;

import Fakturomat.Fakturomat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class PrintAction extends AbstractAction {
    private final Fakturomat fakturomat;

    public PrintAction(Fakturomat f) { this.fakturomat = f; }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrinterJob pj = PrinterJob.getPrinterJob();

        pj.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex != 0)
                return Printable.NO_SUCH_PAGE;

            fakturomat.data.drawGraphics(graphics);

            return Printable.PAGE_EXISTS;
        });

        if (pj.printDialog()) {
            try {
                pj.print();
            }
            catch (PrinterException exc) {
                exc.printStackTrace();
            }
        }
    }
}
