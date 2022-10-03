package Fakturomat;

import Fakturomat.Data.*;
import Fakturomat.Inputs.Manager;
import Fakturomat.Panels.GraphicsPanel;
import Fakturomat.Windows.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class Fakturomat extends JFrame {
    public GraphicsPanel graphicsPanel = new GraphicsPanel(this);
    public Manager manager;

    public static String path = "./Fakturomat";

    public String bankName;

    public BaseData data;

    public Fakturomat() {
        super("Fakturomat");
        setSize(new Dimension(600, 500));
        setLayout(null);

        // ===================================================

        try {
            File fi = new File(path);
            if (!fi.exists() && !fi.mkdir()) {
                throw new Exception("Cannot create main directory");
            }

            File f = new File("./Manager.txt");

            if (f.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./Manager.txt"));
                bankName = (String) ois.readObject();
                manager = (Manager) ois.readObject();
                ois.close();
            } else {
                manager = new Manager();
            }
        } catch (Exception exc) {
            System.out.println("abc: " + exc + " | " + exc.getMessage());
            return;
        }

        // ===================================================

        setMenuBar();

        // ===================================================

        JScrollPane scrollPane = new JScrollPane(graphicsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setSize(585, 400);

        add(scrollPane);

        // ===================================================

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        bottomPanel.setSize(new Dimension(600, 40));
        bottomPanel.setLocation(new Point(0, 400));

        JButton editButton = new JButton("Edytuj");
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (data != null) {
                    if (WZData.class.equals(data.getClass())) {
                        new WZAction(Fakturomat.this, true).actionPerformed(e);
                    } else {
                        new InvoiceAction(Fakturomat.this, true).actionPerformed(e);
                    }
                }
            }
        });

        bottomPanel.add(editButton);

        JButton printButton = new JButton("Drukuj");
        printButton.setPreferredSize(new Dimension(80, 30));
        printButton.addActionListener(new PrintAction(this));

        bottomPanel.add(printButton);

        add(bottomPanel);

        // ===================================================

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./Manager.txt"));
                    oos.writeObject(bankName);
                    oos.writeObject(manager);
                    oos.close();
                } catch (Exception exc) {
                    System.out.println(exc + " | " + exc.getMessage());
                }
            }
        });

        // ===================================================

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void setMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu wz = new JMenu("WZ");

        JMenuItem nowyWZ = new JMenuItem("Nowy");
        nowyWZ.addActionListener(new WZAction(this, false));
        wz.add(nowyWZ);

        JMenuItem wystawiony = new JMenuItem("Wystawiony");
        wystawiony.addActionListener(new OpenSavedDataAction(this,"wz"));
        wz.add(wystawiony);

        bar.add(wz);

        // ===================================================

        JMenu faktura = new JMenu("Faktura");

        JMenuItem nowaFaktura = new JMenuItem("Nowa");
        nowaFaktura.addActionListener(new InvoiceAction(Fakturomat.this, false));
        faktura.add(nowaFaktura);

        JMenuItem fakturaZWZ = new JMenuItem("Nowa z WZ");
        fakturaZWZ.addActionListener(new InvoiceFromWZAction(Fakturomat.this));
        faktura.add(fakturaZWZ);

        JMenuItem wystawionaFaktura = new JMenuItem("Wystawiona");
        wystawionaFaktura.addActionListener(new OpenSavedDataAction(this, "faktura"));
        faktura.add(wystawionaFaktura);

        bar.add(faktura);

        // ===================================================

        JMenu kontrachenci = new JMenu("Kontrachenci");
        kontrachenci.addMouseListener(new ContractorAction(manager.contractors));

        bar.add(kontrachenci);

        // ===================================================

        JMenu baza = new JMenu("Baza towarów");
        baza.addMouseListener(new WareAction(manager.wares));

        bar.add(baza);

        // ===================================================

        JMenu opcje = new JMenu("Opcje");
        opcje.addMouseListener(new SettingsAction(this));

        bar.add(opcje);


        this.setJMenuBar(bar);
    }
}
