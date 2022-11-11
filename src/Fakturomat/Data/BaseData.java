package Fakturomat.Data;

import Fakturomat.Inputs.Contractor;
import Fakturomat.Inputs.Ware;
import Fakturomat.Pair;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public abstract class BaseData implements Serializable {
    public Contractor seller, buyer;
    public List<Pair<Ware, String>> wares;
    public LocalDate dataWystawienia;
    public String number;

    public abstract void drawGraphics(Graphics graphics);
}
