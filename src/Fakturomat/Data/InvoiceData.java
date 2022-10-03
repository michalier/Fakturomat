package Fakturomat.Data;

import Fakturomat.Inputs.Contractor;
import Fakturomat.Inputs.Ware;
import Fakturomat.TextDrawer;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// szerokosc a4 595
// wysokosc  a4 841

public class InvoiceData extends BaseData {
    public LocalDate dataWydania, paymentDeadline;
    public String paymentMethod, wzNumber, bankName;

    public static int[] bars;
    public static String[] headers;

    static {
        bars = new int[]{60, 80, 250, 295, 335, 375, 420, 445, 490, 535};
        headers = new String[]{"L.p.", "Nazwa towaru/usługi\n(SWW/KU)", "J.m.", "Ilość", "Cena j.\nnetto", "Wartość\nnetto",
                "VAT\n%", "Kwota\npodatku", "Wartość\nbrutto"};
    }

    private static class Prices {
        public int nettoPrice, taxAmount, bruttoPrice;

        public Prices() {
            nettoPrice = 0;
            taxAmount = 0;
            bruttoPrice = 0;
        }

        public void add(Prices p) {
            this.nettoPrice += p.nettoPrice;
            this.taxAmount += p.taxAmount;
            this.bruttoPrice += p.bruttoPrice;
        }
    }

    public InvoiceData() { this.wares = new HashMap<>(); }

    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(buyer);
        oos.writeObject(seller);
        oos.writeObject(number);
        oos.writeObject(wzNumber);
        oos.writeObject(wares);
        oos.writeObject(dataWystawienia);
        oos.writeObject(dataWydania);
        oos.writeObject(paymentDeadline);
        oos.writeObject(paymentMethod);
    }

    @Serial
    @SuppressWarnings (value="unchecked")
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        buyer = (Contractor) ois.readObject();
        seller = (Contractor) ois.readObject();
        number = (String) ois.readObject();
        wzNumber = (String) ois.readObject();
        wares = (HashMap<Ware, String>) ois.readObject();
        dataWystawienia = (LocalDate) ois.readObject();
        dataWydania = (LocalDate) ois.readObject();
        paymentDeadline = (LocalDate) ois.readObject();
        paymentMethod = (String) ois.readObject();
    }

    @Override
    public void drawGraphics(Graphics g) {
        Map<String, Prices> subtotals = new TreeMap<>();
        subtotals.put("zw", new Prices());

        Graphics2D graphics = (Graphics2D) g;

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 10000, 10000);
        graphics.setColor(Color.BLACK);

        graphics.setFont(graphics.getFont().deriveFont(9.0f));

        int fontSize = graphics.getFontMetrics().getHeight();

        int rowHeight = fontSize * 2;

        // ===================================================

        graphics.drawString("Data wystawienia:", 400, 20);

        int x = 550 - TextDrawer.getWidth(graphics, dataWystawienia.toString());
        graphics.drawString(formatDate(dataWystawienia), x, 20);

        x = (150 - TextDrawer.getWidth(graphics, "Kielce")) / 2;
        graphics.drawString("Kielce", 400 + x, 20 + graphics.getFontMetrics().getHeight());

        // ===================================================

        String header = "FAKTURA VAT " + this.number;

        x = (595 / 2) - (TextDrawer.getWidth(graphics, header) / 2);

        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));
        graphics.drawString(header, x, 50);

        graphics.drawString("Sprzedawca:", 63, 70);
        graphics.drawString("Nabywca:", 353, 70);

        graphics.setFont(graphics.getFont().deriveFont(Font.PLAIN));

        // ===================================================

        String sprzedawca = seller.name + "\n\n" + seller.adress + "\nNIP: " + seller.NIP;

        TextDrawer.drawMultiline(graphics, sprzedawca, TextDrawer.Alignment.Left, 60, 70 + fontSize, 0);

        // ===================================================

        String nabywca = buyer.name + (buyer.name.contains("\n") ? "\n" : "\n\n") + buyer.adress + "\nNIP: " + buyer.NIP;

        TextDrawer.drawMultiline(graphics, nabywca, TextDrawer.Alignment.Left, 350, 70 + fontSize, 0);

        // ===================================================

        int dy = 154 + graphics.getFontMetrics().getHeight();
        graphics.drawString("Data wydania towaru: " + formatDate(dataWydania), 60, dy);

        if (wzNumber != null)
            graphics.drawString("Wystawiono na postawie WZ nr: " + wzNumber, 350, dy);

        // ===================================================

        dy += graphics.getFontMetrics().getHeight() * 2;

        graphics.setStroke(new BasicStroke(2));
        graphics.drawLine(60, dy, 535, dy);
        graphics.drawLine(60, dy + rowHeight, 535, dy + rowHeight);

        for (int i : bars) {
            graphics.drawLine(i, dy, i, dy + rowHeight);
        }

        dy += 9;

        int n = 0;
        for (String h : headers) {
            TextDrawer.drawMultiline(graphics, h, TextDrawer.Alignment.Center, bars[n], dy , bars[n + 1] - bars[n]);
            n++;
        }

        // ===================================================

        AtomicInteger ai = new AtomicInteger(dy - 9 + (graphics.getFontMetrics().getHeight() * 2));
        AtomicInteger nr = new AtomicInteger(1);

        graphics.setStroke(new BasicStroke(1));

        wares.forEach((w, s) -> {
            int h = drawRow(graphics, w, s, ai.get(), nr.getAndIncrement(), subtotals);
            ai.accumulateAndGet(h, Integer::sum);
        });

        // ===================================================

        dy = ai.get() + 10;

        graphics.setStroke(new BasicStroke(2));
        graphics.drawLine(bars[4], ai.get(), bars[9], ai.get());
        graphics.drawLine(bars[4], ai.get() + fontSize, bars[9], ai.get() + fontSize);

        for (int j = 4; j < bars.length; j++) {
            graphics.drawLine(bars[j], ai.get(), bars[j], ai.get() + fontSize);
        }

        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));

        TextDrawer.drawMultiline(graphics, "RAZEM:", TextDrawer.Alignment.Left, bars[4], dy, bars[5] - bars[4]);

        Prices total = new Prices();

        subtotals.values().forEach(total::add);

        TextDrawer.drawMultiline(graphics, printPrice(total.nettoPrice),  TextDrawer.Alignment.Right, bars[5], dy, bars[6] - bars[5]);
        TextDrawer.drawMultiline(graphics, "X",                      TextDrawer.Alignment.Right, bars[6], dy, bars[7] - bars[6]);
        TextDrawer.drawMultiline(graphics, printPrice(total.taxAmount),   TextDrawer.Alignment.Right, bars[7], dy, bars[8] - bars[7]);
        TextDrawer.drawMultiline(graphics, printPrice(total.bruttoPrice), TextDrawer.Alignment.Right, bars[8], dy, bars[9] - bars[8]);

        graphics.setFont(graphics.getFont().deriveFont(Font.PLAIN));

        // ===================================================

        ai.getAndAccumulate(fontSize, Integer::sum);

        graphics.setStroke(new BasicStroke(1));

        final AtomicBoolean first = new AtomicBoolean(true);

        subtotals.keySet().stream().sorted(Comparator.reverseOrder()).forEach(s -> {
            Prices p = subtotals.get(s);

            for (int j = (first.get() ? 4 : 5); j < bars.length; j++) {
                graphics.drawLine(bars[j], ai.get(), bars[j], ai.get() + fontSize);
            }

            if (first.get()) {
                TextDrawer.drawMultiline(graphics, "W tym:", TextDrawer.Alignment.Left, bars[4], ai.get() + 10, bars[5] - bars[4]);
            }

            TextDrawer.drawMultiline(graphics, printPrice(p.nettoPrice),  TextDrawer.Alignment.Right, bars[5], ai.get() + 10, bars[6] - bars[5]);
            TextDrawer.drawMultiline(graphics, s,                         TextDrawer.Alignment.Right, bars[6], ai.get() + 10, bars[7] - bars[6]);
            TextDrawer.drawMultiline(graphics, printPrice(p.taxAmount),   TextDrawer.Alignment.Right, bars[7], ai.get() + 10, bars[8] - bars[7]);
            TextDrawer.drawMultiline(graphics, printPrice(p.bruttoPrice), TextDrawer.Alignment.Right, bars[8], ai.get() + 10, bars[9] - bars[8]);

            ai.getAndAccumulate(fontSize, Integer::sum);
            graphics.drawLine(bars[(first.get() ? 4 : 5)], ai.get(), bars[9], ai.get());

            first.set(false);
        });

        // ===================================================

        dy = ai.get() + rowHeight;

        graphics.setStroke(new BasicStroke(2));

        graphics.drawLine(bars[0], dy, bars[9], dy);
        graphics.drawLine(bars[0], dy + fontSize, bars[9], dy + fontSize);
        graphics.drawLine(bars[0], dy + rowHeight, bars[9], dy + rowHeight);
        graphics.drawLine(bars[0], dy, bars[0], dy + rowHeight);
        graphics.drawLine(bars[9], dy, bars[9], dy + rowHeight);

        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));
        graphics.drawString("Do zapłaty: " + printPrice(total.bruttoPrice) + " zł", bars[0] + 3, dy + 10);
        graphics.setFont(graphics.getFont().deriveFont(Font.PLAIN));
        graphics.drawString("Sposób zapłaty: " + paymentMethod, bars[2], dy + 10);
        graphics.drawString("Termin zapłaty: " + formatDate(paymentDeadline), bars[6], dy + 10);

        TextDrawer.drawMultiline(graphics, "Słownie złotych:" + priceToText(printPrice(total.bruttoPrice)),
                TextDrawer.Alignment.Center, bars[0], dy + 10 + fontSize, bars[9] - bars[0]);

        // ===================================================

        dy += rowHeight * 2;

        graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));
        graphics.drawString(bankName, bars[0], dy);
        graphics.setFont(graphics.getFont().deriveFont(Font.PLAIN));

        // ===================================================

        dy += fontSize * 5;

        graphics.setStroke(new BasicStroke(1));
        graphics.drawLine(bars[0], dy, bars[0] + 150, dy);
        graphics.drawLine(bars[9] - 150, dy, bars[9], dy);

        graphics.setFont(graphics.getFont().deriveFont(10.0f));

        TextDrawer.drawMultiline(graphics, "Dariusz Majchrzyk", TextDrawer.Alignment.Center, bars[9] - 150, dy - 2, 150);

        dy += 7;

        graphics.setFont(graphics.getFont().deriveFont(7.0f));
        TextDrawer.drawMultiline(graphics, "Osoba upoważniona do odbioru",     TextDrawer.Alignment.Center, bars[0], dy, 150);
        TextDrawer.drawMultiline(graphics, "Osoba upoważniona do wystawienia", TextDrawer.Alignment.Center, bars[9] - 150, dy, 150);
    }

    private String priceToText(String p) {
        String integer = p.split(",")[0], decimal = p.split(",")[1];

        integer = new StringBuilder(integer).reverse().toString();

        int c = (integer.length() - 1) / 3;

        String[] nteens = {
                "dziesięć", "jedenaście", "dwanaście", "trzynaście", "czternaście", "piętnaście",
                "szesnaście", "siedemnaście", "osiemnaście", "dziewiętnaście"
        };

        String[][] numbers = {
                {"jeden", "dwa", "trzy", "cztery", "pięć", "sześć", "siedem", "osiem", "dziewięć"},
                {"dziesięć", "dwadzieścia", "trzydzieści", "czterdzieści", "pięćdziesiąt",
                        "sześćdziesiąt", "siedemdziesiąt", "osiemdziesiąt", "dziewięćdziesiąt"},
                {"sto", "dwieście", "trzysta", "czterysta", "pięćset", "sześćset", "siedemset", "osiemset", "dziewięćset"}
        };

        String[][] positions = {
                {"tysiąc", "tysiące", "tysięcy"},
                {"milion", "miliony", "milionów"}
        };

        StringBuilder sb = new StringBuilder();

        boolean hasDigits = false;
        int posVariant = 0;

        for (int i = integer.length() - 1; i >= 0; i--) {
            if (i % 3 == 1 && integer.charAt(i) == '1') {
                sb.append(" ").append(nteens[integer.charAt(i - 1) - 48]);
                i--;
                hasDigits = true;
                posVariant = 2;
            } else {
                if (integer.charAt(i) != '0') {
                    sb.append(" ").append(numbers[i % 3][integer.charAt(i) - 49]);
                    int ch = integer.charAt(i) - 48;

                    if (ch == 1) {
                        posVariant = 0;
                    } else if (ch > 1 && ch < 5) {
                        posVariant = 1;
                    } else {
                        posVariant = 2;
                    }

                    hasDigits = true;
                }
            }

            if (i % 3 == 0 && c > 0) {
                if (hasDigits) {
                    sb.append(" ").append(positions[c - 1][posVariant]);
                }

                hasDigits = false;
                c--;
            }
        }

        sb.append(" ").append(decimal).append("/100");

        return sb.toString();
    }

    private String formatDate(LocalDate date) { return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")); }

    private int roundPrice(int i) {
        String s = String.valueOf(i);

        int a = s.charAt(s.length() - 1) - 48;
        i = i - a;

        if (a >= 5) {
            i += 10;
        }

        return i;
    }

    private String printPrice(int x) {
        String s = String.valueOf(x);

        return switch (s.length()) {
            case 0, 1 -> "";
            case 2 -> "0,0" + s.charAt(0);
            case 3 -> "0," + s.substring(0, 2);
            default -> s.substring(0, s.length() - 3) + "," + s.substring(s.length() - 3, s.length() - 1);
        };
    }

    private Prices calculatePrices(Ware ware, String amount) {
        Prices p = new Prices();

        int warePrice;

        if (ware.price.contains(",") || ware.price.contains("\\.")) {
            String[] parts = ware.price.split("[.,]");

            StringBuilder sb = new StringBuilder();
            if (Integer.parseInt(parts[0]) > 0) {
                sb.append(parts[0]);
            }

            switch (parts[1].length()) {
                case 0 -> sb.append("000");
                case 1 -> sb.append(parts[1]).append("00");
                case 2 -> sb.append(parts[1]).append("0");
                default -> sb.append(parts[1]);
            }

            warePrice = Integer.parseInt(sb.toString());
        } else {
            warePrice = Integer.parseInt(ware.price + "000");
        }

        double wareAmount = Double.parseDouble(amount.replaceAll(",", "\\."));

        p.nettoPrice = roundPrice((int)(warePrice * wareAmount));
        p.taxAmount = roundPrice(p.nettoPrice * ware.vat / 100);
        p.bruttoPrice = p.nettoPrice + p.taxAmount;

        return p;
    }

    private int drawRow(Graphics2D graphics, Ware ware, String amount, int dy, int nr, Map<String, Prices> subtotals) {
        int height = graphics.getFontMetrics().getHeight();
        int width = 165;

        if (ware.name.contains("~")) {
            height +=  ware.name.chars().filter(e -> e == '~').count() * graphics.getFontMetrics().getHeight();
            TextDrawer.drawMultiline(graphics, ware.name.replaceAll("~", "\n"), TextDrawer.Alignment.Left, bars[1], dy + 10, 0);
        } else {
            if (TextDrawer.getWidth(graphics, ware.name) > width) {
                String[] parts = ware.name.split(" ");

                StringBuilder main = new StringBuilder();
                StringBuilder temp = new StringBuilder();

                for (String s : parts) {
                    if (TextDrawer.getWidth(graphics, temp + s) > width) {
                        main.append(temp).append("\n");
                        temp = new StringBuilder().append(s).append(" ");
                        height += graphics.getFontMetrics().getHeight();
                    } else {
                        temp.append(s).append(" ");
                    }
                }

                main.append(temp);

                TextDrawer.drawMultiline(graphics, main.toString(), TextDrawer.Alignment.Left, bars[1], dy + 10, 0);
            } else {
                TextDrawer.drawMultiline(graphics, ware.name, TextDrawer.Alignment.Left, bars[1], dy + 10, 0);
            }
        }

        Prices p = calculatePrices(ware, amount);

        if (!subtotals.containsKey(Integer.toString(ware.vat))) {
            subtotals.put(Integer.toString(ware.vat), new Prices());
        }

        subtotals.get(Integer.toString(ware.vat)).add(p);

        TextDrawer.drawMultiline(graphics, String.valueOf(nr),        TextDrawer.Alignment.Left,   bars[0], dy + 10, bars[1] - bars[0]);
        TextDrawer.drawMultiline(graphics, ware.measureUnit,          TextDrawer.Alignment.Center, bars[2], dy + 10, bars[3] - bars[2]);
        TextDrawer.drawMultiline(graphics, amount,                    TextDrawer.Alignment.Right , bars[3], dy + 10, bars[4] - bars[3]);
        TextDrawer.drawMultiline(graphics, ware.price,                TextDrawer.Alignment.Right,  bars[4], dy + 10, bars[5] - bars[4]);
        TextDrawer.drawMultiline(graphics, printPrice(p.nettoPrice),  TextDrawer.Alignment.Right,  bars[5], dy + 10, bars[6] - bars[5]);
        TextDrawer.drawMultiline(graphics, String.valueOf(ware.vat),  TextDrawer.Alignment.Right,  bars[6], dy + 10, bars[7] - bars[6]);
        TextDrawer.drawMultiline(graphics, printPrice(p.taxAmount),   TextDrawer.Alignment.Right,  bars[7], dy + 10, bars[8] - bars[7]);
        TextDrawer.drawMultiline(graphics, printPrice(p.bruttoPrice), TextDrawer.Alignment.Right,  bars[8], dy + 10, bars[9] - bars[8]);

        for (int i : bars) {
            graphics.drawLine(i, dy, i, dy + height);
        }

        graphics.drawLine(bars[0], dy + height, 535, dy + height);

        return height;
    }
}
