package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Initial Conditions W2Card
 *
 * This card has one line per water body
 */
public class InitialConditionsCard extends W2Card {
    private List<Double> T2I;
    private List<Double> ICEI;
    private List<String> WTYPEC;
    private List<String> GRIDC;
    private List<String> identifiers;
    private int numWaterbodies;

    public InitialConditionsCard(W2ControlFile w2ControlFile, int numWaterbodies) {
        super(w2ControlFile, "INIT CND", numWaterbodies);
        this.numWaterbodies = numWaterbodies;
        parseTable();
    }

    public List<Double> getT2I() {
        return T2I;
    }

    public void setT2I(List<Double> t2I) {
        T2I = t2I;
        updateText();
    }

    public List<Double> getICEI() {
        return ICEI;
    }

    public void setICEI(List<Double> ICEI) {
        this.ICEI = ICEI;
        updateText();
    }

    public List<String> getWTYPEC() {
        return WTYPEC;
    }

    public void setWTYPEC(List<String> WTYPEC) {
        this.WTYPEC = WTYPEC;
        updateText();
    }

    public List<String> getGRIDC() {
        return GRIDC;
    }

    public void setGRIDC(List<String> GRIDC) {
        this.GRIDC = GRIDC;
        updateText();
    }

    public int getNumWaterbodies() {
        return numWaterbodies;
    }

    public void setNumWaterbodies(int numWaterbodies) {
        this.numWaterbodies = numWaterbodies;
        updateText();
    }

    @Override
    public void parseTable() {
        T2I = new ArrayList<>();
        ICEI = new ArrayList<>();
        WTYPEC = new ArrayList<>();
        GRIDC = new ArrayList<>();
        identifiers = new ArrayList<>();

        for (int i = 0; i < numWaterbodies; i++) {
            List<String> fields = parseLine(table.get(i), 8, 1, 10);
            identifiers.add(fields.get(0));
            T2I.add(Double.parseDouble(fields.get(1)));
            ICEI.add(Double.parseDouble(fields.get(2)));
            WTYPEC.add(fields.get(3));
            GRIDC.add(fields.get(4));
        }
    }

    @Override
    public void updateText() {
        table.clear();
        for (int i = 0; i < numWaterbodies; i++) {
            String str = String.format("%-8s%8.5f%8.5f%8s%8s",
                    identifiers.get(i), T2I.get(i), ICEI.get(i), WTYPEC.get(i), GRIDC.get(i));
            table.add(str);
        }
    }
}
