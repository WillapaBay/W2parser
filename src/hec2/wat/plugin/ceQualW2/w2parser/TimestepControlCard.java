package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

/**
 * Timestep Control W2Card
 */
public class TimestepControlCard extends W2Card {
    private int ndt;
    private double dltMin;
    private String dltIntr;

    public TimestepControlCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, W2CardNames.TimestepControl, 1);
        parseTable();
    }

    public int getNdt() {
        return ndt;
    }

    public void setNdt(int ndt) {
        this.ndt = ndt;
        updateText();
    }

    public double getDltMin() {
        return dltMin;
    }

    public void setDltMin(double dltMin) {
        this.dltMin = dltMin;
        updateText();
    }

    public String getDltIntr() {
        return dltIntr;
    }

    public void setDltIntr(String dltIntr) {
        this.dltIntr = dltIntr.toUpperCase();
        updateText();
    }

    @Override
    public void parseTable() {
//        String[] records = table.get(0).trim().split("\\s+");
        List<String> fields = parseLine(table.get(0), 8, 2, 10);
        ndt = Integer.parseInt(fields.get(0));
        dltMin = Double.parseDouble(fields.get(1));
        dltIntr = fields.get(2);
    }

    @Override
    public void updateText() {
        String str = String.format("%8s%8d%8.5f%8s", "",
                ndt, dltMin, dltIntr);
        table.set(0, str);
    }
}

