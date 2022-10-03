package Fakturomat;

import java.awt.*;

public class TextDrawer {
    public enum Alignment {
        Left,
        Center,
        Right,
    }

    public static void drawMultiline(Graphics graphics, String text, Alignment alignment, int x, int y, int width) {
        for (String line : text.split("\n")) {
            int dx = switch(alignment) {
                case Left -> x + 3;
                case Center -> x + (width - getWidth(graphics, line)) / 2;
                case Right -> x + width - getWidth(graphics, text) - 3;
            };

            graphics.drawString(line, dx, y);

            y += graphics.getFontMetrics().getHeight();
        }
    }

    public static int getWidth(Graphics graphics, String text) { return (int) graphics.getFontMetrics().getStringBounds(text, graphics).getWidth(); }
}
