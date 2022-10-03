package Fakturomat.Panels;

import Fakturomat.Inputs.Ware;
import Fakturomat.Pair;

import javax.swing.*;

public abstract class BaseWarePanel extends JPanel {
    protected JLabel index;
    public JTextField amountField;

    public void setNumber(int i) { this.index.setText((i + 1) + "."); }
    public abstract Pair<Ware, String> getValue();
    public abstract int getHeight();
    public abstract boolean hasValidValues();
}
