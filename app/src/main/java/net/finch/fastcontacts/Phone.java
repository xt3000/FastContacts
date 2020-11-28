package net.finch.fastcontacts;

public class Phone {
    private int id;
    private String label;
    private String num;

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

    public String getNumDecorated() {
        StringBuilder sb = new StringBuilder(num);
        int n = num.length();

        if (n>5) {
            sb.insert(n-2, "-");
            sb.insert(n-4, "-");
        }
        if (n>6) sb.insert(n-7, " ");
        if (n>9) sb.insert(n-10, " ");

        return sb.toString();
    }

    public int getId() {
        return id;
    }
}
