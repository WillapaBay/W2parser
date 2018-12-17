package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Ice Cover W2Card_OLD
 *
 * This card has one line per water body
 */
public class IceCoverCard extends W2Card {
    private List<String> ICEC;
    private List<String> SLICEC;
    private List<Double> ALBEDO;
    private List<Double> HWICE;
    private List<Double> BICE;
    private List<Double> GICE;
    private List<Double> ICEMIN;
    private List<Double> ICET2;
    private int numWaterBodies;
    private final String format = "%8.5f";

    public IceCoverCard(W2ControlFile w2ControlFile, int numWaterBodies) {
        super(w2ControlFile, "ICE COVE", numWaterBodies,
                W2Globals.constants(numWaterBodies, 8), 8, false);
        this.numWaterBodies = numWaterBodies;
        parseTable();
        init();
    }

    public void init() {
        ICEC = new ArrayList<>();
        SLICEC = new ArrayList<>();
        ALBEDO = new ArrayList<>();
        HWICE = new ArrayList<>();
        BICE = new ArrayList<>();
        GICE = new ArrayList<>();
        ICEMIN = new ArrayList<>();
        ICET2 = new ArrayList<>();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = recordValuesList.get(i);
            ICEC.add(record.get(0));
            SLICEC.add(record.get(1));
            ALBEDO.add(Double.valueOf(record.get(2)));
            HWICE.add(Double.valueOf(record.get(3)));
            BICE.add(Double.valueOf(record.get(4)));
            GICE.add(Double.valueOf(record.get(5)));
            ICEMIN.add(Double.valueOf(record.get(6)));
            ICET2.add(Double.valueOf(record.get(7)));
        }
    }

    public List<String> getICEC() {
        return ICEC;
    }

    public void setICEC(List<String> ICEC) {
        this.ICEC = ICEC;
        updateRecordValuesList();
    }

    public List<String> getSLICEC() {
        return SLICEC;
    }

    public void setSLICEC(List<String> SLICEC) {
        this.SLICEC = SLICEC;
        updateRecordValuesList();
    }

    public List<Double> getALBEDO() {
        return ALBEDO;
    }

    public void setALBEDO(List<Double> ALBEDO) {
        this.ALBEDO = ALBEDO;
        updateRecordValuesList();
    }

    public List<Double> getHWICE() {
        return HWICE;
    }

    public void setHWICE(List<Double> HWICE) {
        this.HWICE = HWICE;
        updateRecordValuesList();
    }

    public List<Double> getBICE() {
        return BICE;
    }

    public void setBICE(List<Double> BICE) {
        this.BICE = BICE;
        updateRecordValuesList();
    }

    public List<Double> getGICE() {
        return GICE;
    }

    public void setGICE(List<Double> GICE) {
        this.GICE = GICE;
        updateRecordValuesList();
    }

    public List<Double> getICEMIN() {
        return ICEMIN;
    }

    public void setICEMIN(List<Double> ICEMIN) {
        this.ICEMIN = ICEMIN;
        updateRecordValuesList();
    }

    public List<Double> getICET2() {
        return ICET2;
    }

    public void setICET2(List<Double> ICET2) {
        this.ICET2 = ICET2;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = new ArrayList<>();
            record.add(ICEC.get(i));
            record.add(SLICEC.get(i));
            record.add(String.format(format, ALBEDO.get(i)));
            record.add(String.format(format, HWICE.get(i)));
            record.add(String.format(format, BICE.get(i)));
            record.add(String.format(format, GICE.get(i)));
            record.add(String.format(format, ICEMIN.get(i)));
            record.add(String.format(format, ICET2.get(i)));
            recordValuesList.add(record);
        }
    }
}
