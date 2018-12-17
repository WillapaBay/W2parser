package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Initial Conditions Card
 *
 * This card has one line per water body
 */
public class InitialConditionsCard extends W2Card {
    private List<Double> T2I;
    private List<Double> ICEI;
    private List<String> WTYPEC;
    private List<String> GRIDC;
    private int numWaterBodies;
    private final String format = "%8.5f";

    public InitialConditionsCard(W2ControlFile w2ControlFile, int numWaterBodies) {
        super(w2ControlFile, "INIT CND", numWaterBodies,
                W2Globals.constants(numWaterBodies, 4),
                8, false);
        this.numWaterBodies = numWaterBodies;
        parseTable();
        init();
    }

    public void init() {
        T2I = new ArrayList<>();
        ICEI = new ArrayList<>();
        WTYPEC = new ArrayList<>();
        GRIDC = new ArrayList<>();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = recordValuesList.get(i);
            T2I.add(Double.valueOf(record.get(0)));
            ICEI.add(Double.valueOf(record.get(1)));
            WTYPEC.add(record.get(2));
            if (record.size() > 3)
                GRIDC.add(record.get(3));
            else
                GRIDC.add("RECT");
        }
    }

    public List<Double> getT2I() {
        return T2I;
    }

    public void setT2I(List<Double> t2I) {
        T2I = t2I;
        updateRecordValuesList();
    }

    public List<Double> getICEI() {
        return ICEI;
    }

    public void setICEI(List<Double> ICEI) {
        this.ICEI = ICEI;
        updateRecordValuesList();
    }

    public List<String> getWTYPEC() {
        return WTYPEC;
    }

    public void setWTYPEC(List<String> WTYPEC) {
        this.WTYPEC = WTYPEC;
        updateRecordValuesList();
    }

    public List<String> getGRIDC() {
        return GRIDC;
    }

    public void setGRIDC(List<String> GRIDC) {
        this.GRIDC = GRIDC;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = new ArrayList<>();
            record.add(String.format(format, T2I.get(i)));
            record.add(String.format(format, ICEI.get(i)));
            record.add(WTYPEC.get(i));
            record.add(GRIDC.get(i));
            recordValuesList.add(record);
        }
    }
}
