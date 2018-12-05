package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

/**
 * Time Control W2Card
 */
public class TimeControlCard extends W2Card {
    private double jdayMin;
    private double jdayMax;
    private int startYear;

    public TimeControlCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "TIME CON", 1);
        parseTable();
    }

    public double getJdayMin() {
        return jdayMin;
    }

    public void setJdayMin(double jdayMin) {
        this.jdayMin = jdayMin;
        updateText();
    }

    public double getJdayMax() {
        return jdayMax;
    }

    public void setJdayMax(double jdayMax) {
        this.jdayMax = jdayMax;
        updateText();
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
        updateText();
    }

    @Override
    public void parseTable() {
        List<String> fields = parseLine(table.get(0), 8, 2, 10);
        jdayMin = Double.parseDouble(fields.get(0));
        jdayMax = Double.parseDouble(fields.get(1));
        startYear = Integer.parseInt(fields.get(2));
    }

    @Override
    public void updateText() {
        String str = String.format("%8s%8.3f%8.3f%8d", "",
                jdayMin, jdayMax, startYear);
        table.set(0, str);
    }
}

