package Fakturomat.Data;

import Fakturomat.Inputs.Contractor;
import Fakturomat.Inputs.Ware;
import Fakturomat.Pair;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

// szerokosc a4 595
// wysokosc  a4 841

public class WZData extends BaseData implements Serializable {
    public WZData() { this.wares = new ArrayList<>(); }

    private void generateGraphics(Graphics graphics, int y, boolean clear) {
        if (clear) {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 10000, 10000);
            graphics.setColor(Color.BLACK);
        }

        String header = this.seller.name + "\n" + this.seller.adress + "\n \n" +
                "Odbiorca:\n" + this.buyer.name + "\n" + this.buyer.adress;

        int dy = y + 10;
        for (String line : header.split("\n")) {
            graphics.drawString(line, 30, dy += graphics.getFontMetrics().getHeight());
        }

        dy = y + 10 + ((header.split("\n").length + 2) * graphics.getFontMetrics().getHeight());

        AtomicInteger i = new AtomicInteger(1);
        AtomicInteger ddy = new AtomicInteger(dy);

        this.wares.forEach((p) -> {
            Ware w = p.getKey();
            String s = p.getValue();

            graphics.drawString(i.getAndIncrement() + ".", 30, ddy.get());
            graphics.drawString(w.name.replaceAll("~", " "), 45, ddy.get());

            graphics.drawString("szt.", 450, ddy.get());

            int delta = 80 - (int)graphics.getFontMetrics().getStringBounds(s, graphics).getWidth();    // offset od napisu "szt." tak aby s było dopasowane do prawej

            graphics.drawString(s, 460 + delta, ddy.getAndAccumulate(graphics.getFontMetrics().getHeight(), Integer::sum));
        });

        String srodek = "WZ Wydanie materiałów na zewnątrz";
        int deltaX = (int)(graphics.getFontMetrics().getStringBounds(srodek, graphics).getWidth() / 2.0);
        graphics.drawString("WZ Wydanie materiałów na zewnątrz", 297 - deltaX, y + 70);

        deltaX = (int)(graphics.getFontMetrics().getStringBounds("Nr " + this.number, graphics).getWidth() / 2.0);
        graphics.drawString("Nr " + this.number, 297 - deltaX, y + 70 + graphics.getFontMetrics().getHeight());

        int msc = this.dataWystawienia.getMonthValue();

        String miejsceData = "Kielce, "
                + this.dataWystawienia.getDayOfMonth() + "."
                + (msc < 10 ? "0" : "") + msc + "."
                + this.dataWystawienia.getYear() + " r.";

        deltaX = (int)(graphics.getFontMetrics().getStringBounds(miejsceData, graphics).getWidth());
        graphics.drawString(miejsceData, 595 - 30 - deltaX, y + 10 + graphics.getFontMetrics().getHeight());

        int spodY = 315;

        graphics.drawString("wydał", 100, y + spodY);
        graphics.drawString("odebrał", 455, y + spodY );

        graphics.drawString("data", 45, y + spodY + graphics.getFontMetrics().getHeight());
        graphics.drawString("podpis", 160, y + spodY + graphics.getFontMetrics().getHeight());

        graphics.drawString("data", 400, y + spodY + graphics.getFontMetrics().getHeight());
        graphics.drawString("podpis", 515, y + spodY + graphics.getFontMetrics().getHeight());
    }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(buyer);
        oos.writeObject(seller);
        oos.writeObject(number);
        oos.writeObject(wares);
        oos.writeObject(dataWystawienia);
    }

    @Serial
    @SuppressWarnings (value="unchecked")
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        buyer = (Contractor) ois.readObject();
        seller = (Contractor) ois.readObject();
        number = (String) ois.readObject();
        wares = (ArrayList<Pair<Ware, String>>) ois.readObject();
        dataWystawienia = (LocalDate) ois.readObject();
    }

    @Override
    public void drawGraphics(Graphics graphics) {
        this.generateGraphics(graphics, 0, true);
        this.generateGraphics(graphics, 420, false);
    }
}
