package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Ice Cover Card
 *
 * This card has one line per water body
 */
public class IceCoverCard extends Card {
    private List<String> ICEC;
    private List<String> SLICEC;
    private List<Double> ALBEDO;
    private List<Double> HWICE;
    private List<Double> BICE;
    private List<Double> GICE;
    private List<Double> ICEMIN;
    private List<Double> ICET2;
    private List<String> identifiers;
    private int numWaterbodies;

    public IceCoverCard(W2ControlFile w2ControlFile, int numWaterbodies) {
        super(w2ControlFile, "ICE COVE", numWaterbodies);
        this.numWaterbodies = numWaterbodies;
        parseTable();
    }

    public List<String> getICEC() {
        return ICEC;
    }

    public void setICEC(List<String> ICEC) {
        this.ICEC = ICEC;
        updateText();
    }

    public List<String> getSLICEC() {
        return SLICEC;
    }

    public void setSLICEC(List<String> SLICEC) {
        this.SLICEC = SLICEC;
        updateText();
    }

    public List<Double> getALBEDO() {
        return ALBEDO;
    }

    public void setALBEDO(List<Double> ALBEDO) {
        this.ALBEDO = ALBEDO;
        updateText();
    }

    public List<Double> getHWICE() {
        return HWICE;
    }

    public void setHWICE(List<Double> HWICE) {
        this.HWICE = HWICE;
        updateText();
    }

    public List<Double> getBICE() {
        return BICE;
    }

    public void setBICE(List<Double> BICE) {
        this.BICE = BICE;
        updateText();
    }

    public List<Double> getGICE() {
        return GICE;
    }

    public void setGICE(List<Double> GICE) {
        this.GICE = GICE;
        updateText();
    }

    public List<Double> getICEMIN() {
        return ICEMIN;
    }

    public void setICEMIN(List<Double> ICEMIN) {
        this.ICEMIN = ICEMIN;
        updateText();
    }

    public List<Double> getICET2() {
        return ICET2;
    }

    public void setICET2(List<Double> ICET2) {
        this.ICET2 = ICET2;
        updateText();
    }

    @Override
    public void parseTable() {
        ICEC = new ArrayList<>();
        SLICEC = new ArrayList<>();
        ALBEDO = new ArrayList<>();
        HWICE = new ArrayList<>();
        BICE = new ArrayList<>();
        GICE = new ArrayList<>();
        ICEMIN = new ArrayList<>();
        ICET2 = new ArrayList<>();
        identifiers = new ArrayList<>();

        for (int i = 0; i < numWaterbodies; i++) {
            List<String> fields = parseLine(table.get(i), 8, 1, 10);
            identifiers.add(fields.get(0));
            ICEC.add(fields.get(1));
            SLICEC.add(fields.get(2));
            ALBEDO.add(Double.parseDouble(fields.get(3)));
            HWICE.add(Double.parseDouble(fields.get(4)));
            BICE.add(Double.parseDouble(fields.get(5)));
            GICE.add(Double.parseDouble(fields.get(6)));
            ICEMIN.add(Double.parseDouble(fields.get(7)));
            ICET2.add(Double.parseDouble(fields.get(8)));
        }
    }

    @Override
    public void updateText() {
        table.clear();
        for (int i = 0; i < numWaterbodies; i++) {
            String str = String.format("%-8s%8s%8s%8.5f%8.5f%8.5f%8.5f%8.5f%8.5f",
                    identifiers.get(i), ICEC.get(i), SLICEC.get(i), ALBEDO.get(i),
                    HWICE.get(i), BICE.get(i), GICE.get(i), ICEMIN.get(i), ICET2.get(i));
            table.add(str);
        }
    }
}