package Fakturomat.Data;

import Fakturomat.Inputs.Contractor;
import Fakturomat.Inputs.Ware;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;

public abstract class BaseData implements Serializable {
    public Contractor seller, buyer;
    public HashMap<Ware, String> wares;
    public LocalDate dataWystawienia;
    public String number;

    public abstract void drawGraphics(Graphics graphics);
}
