package Fakturomat.Inputs;

import java.io.*;

public class Ware implements Serializable {
    public String name, measureUnit, price;
    public Integer vat;

    public Ware(String name, String price, Integer vat, String measureUnit) {
        this.name = name;
        this.price = price;
        this.vat = vat;
        this.measureUnit = measureUnit;
    }

    @Override
    public String toString() { return name; }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(name);
        oos.writeObject(measureUnit);
        oos.writeObject(price);
        oos.writeObject(vat);
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        name = (String) ois.readObject();
        measureUnit = (String) ois.readObject();
        price = (String) ois.readObject();
        vat = (Integer) ois.readObject();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass().equals(this.getClass())) {
            Ware w = (Ware) o;

            return w.name.equals(name) && w.price.equals(price) &&
                    w.vat.equals(vat) && w.measureUnit.equals(measureUnit);
        }

        return false;
    }
}
