package net.finch.fastcontacts;

import android.icu.util.EthiopicCalendar;

public class Phone {
    public int id;
    public String label;
    public String num;

    public Phone (String label, String num, int id) {
        this.label = label;
        this.num = num;
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public String getNum() {
        return num;
    }

    public int getId() {
        return id;
    }
}
