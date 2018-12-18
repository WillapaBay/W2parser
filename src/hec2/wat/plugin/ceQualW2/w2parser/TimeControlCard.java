package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Time Control Card
 */
public class TimeControlCard extends W2Card {
    private double jdayMin;
    private double jdayMax;
    private int startYear;
    private final String format = "%8.3f";

    public TimeControlCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "TIME CON", 1,
                W2Globals.constants(1, 3), 8,
                false);
        parseTable();
        init();
    }

    public void init() {
        List<String> values = recordValuesList.get(0);
        jdayMin = Double.valueOf(values.get(0));
        jdayMax = Double.valueOf(values.get(1));
        startYear = Integer.valueOf(values.get(2));
    }

    public double getJdayMin() {
        return jdayMin;
    }

    public void setJdayMin(double jdayMin) {
        this.jdayMin = jdayMin;
        updateRecordValuesList();
    }

    public double getJdayMax() {
        return jdayMax;
    }

    public void setJdayMax(double jdayMax) {
        this.jdayMax = jdayMax;
        updateRecordValuesList();
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(String.format(format, jdayMin));
        values.add(String.format(format, jdayMax));
        values.add(String.valueOf(startYear));
        recordValuesList.add(values);
    }
}

