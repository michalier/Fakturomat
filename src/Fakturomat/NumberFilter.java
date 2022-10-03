package Fakturomat;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class NumberFilter extends KeyAdapter {
    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
        if (!(e.getKeyChar() >= '0' && e.getKeyChar() <= '9' || e.getKeyChar() == ',' ||
                e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_END)) {
            e.consume();
        }
    }
}
