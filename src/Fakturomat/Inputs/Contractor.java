package Fakturomat.Inputs;

import java.io.*;

public class Contractor implements Serializable {
    public String name, adress, NIP;

    public Contractor(String name, String adress, String NIP) {
        this.name = name;
        this.adress = adress;
        this.NIP = NIP;
    }

    @Override
    public String toString() { return name; }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(name);
        oos.writeObject(adress);
        oos.writeObject(NIP);
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        name = (String) ois.readObject();
        adress = (String) ois.readObject();
        NIP = (String) ois.readObject();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass().equals(this.getClass())) {
            Contractor c = (Contractor) o;

            return c.name.equals(name) && c.adress.equals(adress) && c.NIP.equals(NIP);
        }

        return false;
    }
}
