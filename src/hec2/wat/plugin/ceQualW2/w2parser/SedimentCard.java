package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Sediment W2Card_OLD
 *
 * This card has one line per water body
 */
public class SedimentCard extends W2Card_NEW {
    private List<String> SEDC;
    private List<String> SEDPRC;
    private List<Double> SEDCI;
    private List<Double> SEDS;
    private List<Double> SEDK;
    private List<Double> FSOD;
    private List<Double> FSED;
    private List<Double> SEDBR;
    private List<String> DYNSEDK;
    private int numWaterBodies;
    private final String format = "%8.5f";

    public SedimentCard(W2ControlFile w2ControlFile, int numWaterBodies) {
        super(w2ControlFile, "SEDIMENT", numWaterBodies,
                W2Globals.constants(numWaterBodies, 9), 8,
                false);
        this.numWaterBodies = numWaterBodies;
        parseTable();
        init();
    }

    public void init() {
        SEDC = new ArrayList<>();
        SEDPRC = new ArrayList<>();
        SEDCI = new ArrayList<>();
        SEDS = new ArrayList<>();
        SEDK = new ArrayList<>();
        FSOD = new ArrayList<>();
        FSED = new ArrayList<>();
        SEDBR = new ArrayList<>();
        DYNSEDK = new ArrayList<>();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = recordValuesList.get(i);
            SEDC.add(record.get(0));
            SEDPRC.add(record.get(1));
            SEDCI.add(Double.valueOf(record.get(2)));
            SEDS.add(Double.valueOf(record.get(3)));
            SEDK.add(Double.valueOf(record.get(4)));
            FSOD.add(Double.valueOf(record.get(5)));
            FSED.add(Double.valueOf(record.get(6)));
            SEDBR.add(Double.valueOf(record.get(7)));
            DYNSEDK.add(record.get(8));
        }
    }

    public List<String> getSEDC() {
        return SEDC;
    }

    public void setSEDC(List<String> SEDC) {
        this.SEDC = SEDC;
        updateRecordValuesList();
    }

    public List<String> getSEDPRC() {
        return SEDPRC;
    }

    public void setSEDPRC(List<String> SEDPRC) {
        this.SEDPRC = SEDPRC;
        updateRecordValuesList();
    }

    public List<Double> getSEDCI() {
        return SEDCI;
    }

    public void setSEDCI(List<Double> SEDCI) {
        this.SEDCI = SEDCI;
        updateRecordValuesList();
    }

    public List<Double> getSEDS() {
        return SEDS;
    }

    public void setSEDS(List<Double> SEDS) {
        this.SEDS = SEDS;
        updateRecordValuesList();
    }

    public List<Double> getSEDK() {
        return SEDK;
    }

    public void setSEDK(List<Double> SEDK) {
        this.SEDK = SEDK;
        updateRecordValuesList();
    }

    public List<Double> getFSOD() {
        return FSOD;
    }

    public void setFSOD(List<Double> FSOD) {
        this.FSOD = FSOD;
        updateRecordValuesList();
    }

    public List<Double> getFSED() {
        return FSED;
    }

    public void setFSED(List<Double> FSED) {
        this.FSED = FSED;
        updateRecordValuesList();
    }

    public List<Double> getSEDBR() {
        return SEDBR;
    }

    public void setSEDBR(List<Double> SEDBR) {
        this.SEDBR = SEDBR;
        updateRecordValuesList();
    }

    public List<String> getDYNSEDK() {
        return DYNSEDK;
    }

    public void setDYNSEDK(List<String> DYNSEDK) {
        this.DYNSEDK = DYNSEDK;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = new ArrayList<>();
            record.add(SEDC.get(i));
            record.add(SEDPRC.get(i));
            record.add(String.format(format, SEDCI.get(i)));
            record.add(String.format(format, SEDS.get(i)));
            record.add(String.format(format, SEDK.get(i)));
            record.add(String.format(format, FSOD.get(i)));
            record.add(String.format(format, FSED.get(i)));
            record.add(String.format(format, SEDBR.get(i)));
            record.add(DYNSEDK.get(i));
            recordValuesList.add(record);
        }
    }
}
