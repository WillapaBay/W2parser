package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Location Card
 *
 * This card has one line per water body
 */
public class LocationCard extends Card {
    private List<Double> LAT;   // Latitude, degrees
    private List<Double> LONG;  // Longitude, degrees
    private List<Double> EBOT;  // Bottom elevation of waterbody, m
    private List<Integer> BS;    // Starting branch of waterbody
    private List<Integer> BE;    // Ending branch of waterbody
    private List<Integer> JBDN;  // Downstream branch of waterbody
    private List<String> identifiers;

    public LocationCard(W2ControlFile w2ControlFile, int numRecordLines) {
        super(w2ControlFile, CardNames.Location, numRecordLines);
        parseTable();
    }

    public List<Double> getLAT() {
        return LAT;
    }

    public void setLAT(List<Double> LAT) {
        this.LAT = LAT;
        updateText();
    }

    public List<Double> getLONG() {
        return LONG;
    }

    public void setLONG(List<Double> LONG) {
        this.LONG = LONG;
        updateText();
    }

    public List<Double> getEBOT() {
        return EBOT;
    }

    public void setEBOT(List<Double> EBOT) {
        this.EBOT = EBOT;
        updateText();
    }

    public List<Integer> getBS() {
        return BS;
    }

    public void setBS(List<Integer> BS) {
        this.BS = BS;
        updateText();
    }

    public List<Integer> getBE() {
        return BE;
    }

    public void setBE(List<Integer> BE) {
        this.BE = BE;
        updateText();
    }

    public List<Integer> getJBDN() {
        return JBDN;
    }

    public void setJBDN(List<Integer> JBDN) {
        this.JBDN = JBDN;
        updateText();
    }

    @Override
    public void parseTable() {
        LAT = new ArrayList<>();
        LONG = new ArrayList<>();
        EBOT = new ArrayList<>();
        BS = new ArrayList<>();
        BE = new ArrayList<>();
        JBDN = new ArrayList<>();
        identifiers = new ArrayList<>();

        for (int i = 0; i < numCardDataLines; i++) {
            List<String> fields = parseLine(table.get(i), 8, 1, 10);
            identifiers.add(fields.get(0));
            LAT.add(Double.parseDouble(fields.get(1)));
            LONG.add(Double.parseDouble(fields.get(2)));
            EBOT.add(Double.parseDouble(fields.get(3)));
            BS.add(Integer.parseInt(fields.get(4)));
            BE.add(Integer.parseInt(fields.get(5)));
            JBDN.add(Integer.parseInt(fields.get(6)));
        }
    }

    @Override
    public void updateText() {
        table.clear();
        for (int i = 0; i < numCardDataLines; i++) {
            String str = String.format("%-8s%8.5f%8.5f%8.3f%8d%8d%8d",
                    identifiers.get(i), LAT.get(i), LONG.get(i), EBOT.get(i), BS.get(i), BE.get(i), JBDN.get(i));
            table.add(str);
        }
    }
}
