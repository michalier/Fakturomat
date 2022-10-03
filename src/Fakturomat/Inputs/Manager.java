package Fakturomat.Inputs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Manager implements Serializable {
    public List<Contractor> contractors = new ArrayList<>();
    public List<Ware> wares = new ArrayList<>();

    public Integer lastWZNumber, lastInvoiceNumber;
    public String numberFormat;

    public Contractor seller;

    public Manager()  {
        lastWZNumber = 1;
        lastInvoiceNumber = 1;
        numberFormat = "Numer/Miesiąc/Rok";
        seller = new Contractor("Zakład Poligraficzny " + (char)34 + "STEF-GRAF" + (char)34,
                "ul. Wojska Polskiego 93\n25-201 Kielce", "657-252-03-24");
    }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(lastWZNumber);
        oos.writeObject(lastInvoiceNumber);
        oos.writeObject(contractors);
        oos.writeObject(wares);
    }

    @Serial
    @SuppressWarnings (value="unchecked")
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        numberFormat = "Numer/Miesiąc/Rok";

        seller = new Contractor("Zakład Poligraficzny " + (char)34 + "STEF-GRAF" + (char)34,
                "ul. Wojska Polskiego 93\n25-201 Kielce", "657-252-03-24");

        lastWZNumber      = (Integer) ois.readObject();
        lastInvoiceNumber = (Integer) ois.readObject();
        contractors       = (List<Contractor>) ois.readObject();
        wares             = (List<Ware>) ois.readObject();
    }
}
