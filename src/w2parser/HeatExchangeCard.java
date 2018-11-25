package w2parser;

import java.util.ArrayList;
import java.util.List;

public class HeatExchangeCard extends Card {
    private int numWaterbodies;
    private List<String> SLHTC;
    private List<String> SROC;
    private List<String> RHEVAP;
    private List<String> METIC;
    private List<String> FETCHC;
    private List<Double> AFW;
    private List<Double> BFW;
    private List<Double> CFW;
    private List<Double> WINDH;
    private List<String> identifiers;

    public HeatExchangeCard(W2ControlFile w2ControlFile, int numWaterbodies) {
        super(w2ControlFile, "HEAT EXCH", numWaterbodies);
        this.numWaterbodies = numWaterbodies;
        parseTable();
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public List<String> getSLHTC() {
        return SLHTC;
    }

    public void setSLHTC(List<String> SLHTC) {
        this.SLHTC = SLHTC;
        updateText();
    }

    public List<String> getSROC() {
        return SROC;
    }

    public void setSROC(List<String> SROC) {
        this.SROC = SROC;
        updateText();
    }

    public List<String> getRHEVAP() {
        return RHEVAP;
    }

    public void setRHEVAP(List<String> RHEVAP) {
        this.RHEVAP = RHEVAP;
        updateText();
    }

    public List<String> getMETIC() {
        return METIC;
    }

    public void setMETIC(List<String> METIC) {
        this.METIC = METIC;
        updateText();
    }

    public List<String> getFETCHC() {
        return FETCHC;
    }

    public void setFETCHC(List<String> FETCHC) {
        this.FETCHC = FETCHC;
        updateText();
    }

    public List<Double> getAFW() {
        return AFW;
    }

    public void setAFW(List<Double> AFW) {
        this.AFW = AFW;
        updateText();
    }

    public List<Double> getBFW() {
        return BFW;
    }

    public void setBFW(List<Double> BFW) {
        this.BFW = BFW;
        updateText();
    }

    public List<Double> getCFW() {
        return CFW;
    }

    public void setCFW(List<Double> CFW) {
        this.CFW = CFW;
        updateText();
    }

    public List<Double> getWINDH() {
        return WINDH;
    }

    public void setWINDH(List<Double> WINDH) {
        this.WINDH = WINDH;
        updateText();
    }

    @Override
    public void parseTable() {
        SLHTC = new ArrayList<>();
        SROC = new ArrayList<>();
        RHEVAP = new ArrayList<>();
        METIC = new ArrayList<>();
        FETCHC = new ArrayList<>();
        AFW = new ArrayList<>();
        BFW = new ArrayList<>();
        CFW = new ArrayList<>();
        WINDH = new ArrayList<>();
        identifiers = new ArrayList<>();

        for (int i = 0; i < numLines; i++) {
            List<String> Fields = parseLine(table.get(i), 8, 1, 10);
            identifiers.add(Fields.get(0));
            SLHTC.add(Fields.get(1));
            SROC.add(Fields.get(2));
            RHEVAP.add(Fields.get(3));
            METIC.add(Fields.get(4));
            FETCHC.add(Fields.get(5));
            AFW.add(Double.parseDouble(Fields.get(6)));
            BFW.add(Double.parseDouble(Fields.get(7)));
            CFW.add(Double.parseDouble(Fields.get(8)));
            WINDH.add(Double.parseDouble(Fields.get(9)));
        }
    }

    @Override
    public void updateText() {
        for (int i = 0; i < numLines; i++) {
            String str = String.format("%-8s%8s%8s%8s%8s%8s%8.5f%8.5f%8.5f%8.5f",
                    identifiers.get(i), SLHTC.get(i), SROC.get(i), RHEVAP.get(i), METIC.get(i),
                    FETCHC.get(i), AFW.get(i), BFW.get(i), CFW.get(i), WINDH.get(i));
            table.set(i, str);
        }
    }
}

