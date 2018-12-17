package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Location Card
 *
 * This card has one line per water body
 */
public class LocationCard extends W2Card {
    private List<Double> LAT;   // Latitude, degrees
    private List<Double> LONG;  // Longitude, degrees
    private List<Double> EBOT;  // Bottom elevation of waterbody, m
    private List<Integer> BS;    // Starting branch of waterbody
    private List<Integer> BE;    // Ending branch of waterbody
    private List<Integer> JBDN;  // Downstream branch of waterbody
    private int numWaterBodies;
    private final String format = "%8.5f";

    public LocationCard(W2ControlFile w2ControlFile, int numWaterBodies) {
        super(w2ControlFile, W2CardNames.Location, numWaterBodies,
                W2Globals.constants(numWaterBodies, 6), 8,
                false);
        this.numWaterBodies = numWaterBodies;
        parseTable();
        init();
    }

    public void init() {
        LAT = new ArrayList<>();
        LONG = new ArrayList<>();
        EBOT = new ArrayList<>();
        BS = new ArrayList<>();
        BE = new ArrayList<>();
        JBDN = new ArrayList<>();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = recordValuesList.get(i);
            LAT.add(Double.valueOf(record.get(0)));
            LONG.add(Double.valueOf(record.get(1)));
            EBOT.add(Double.valueOf(record.get(2)));
            BS.add(Integer.valueOf(record.get(3)));
            BE.add(Integer.valueOf(record.get(4)));
            JBDN.add(Integer.valueOf(record.get(5)));
        }
    }

    public List<Double> getLAT() {
        return LAT;
    }

    public void setLAT(List<Double> LAT) {
        this.LAT = LAT;
        updateRecordValuesList();
    }

    public List<Double> getLONG() {
        return LONG;
    }

    public void setLONG(List<Double> LONG) {
        this.LONG = LONG;
        updateRecordValuesList();
    }

    public List<Double> getEBOT() {
        return EBOT;
    }

    public void setEBOT(List<Double> EBOT) {
        this.EBOT = EBOT;
        updateRecordValuesList();
    }

    public List<Integer> getBS() {
        return BS;
    }

    public void setBS(List<Integer> BS) {
        this.BS = BS;
        updateRecordValuesList();
    }

    public List<Integer> getBE() {
        return BE;
    }

    public void setBE(List<Integer> BE) {
        this.BE = BE;
        updateRecordValuesList();
    }

    public List<Integer> getJBDN() {
        return JBDN;
    }

    public void setJBDN(List<Integer> JBDN) {
        this.JBDN = JBDN;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = new ArrayList<>();
            record.add(String.format(format, LAT.get(i)));
            record.add(String.format(format, LONG.get(i)));
            record.add(String.format(format, EBOT.get(i)));
            record.add(String.valueOf(BS));
            record.add(String.valueOf(BE));
            record.add(String.valueOf(JBDN));
            recordValuesList.add(record);
        }
    }
}
