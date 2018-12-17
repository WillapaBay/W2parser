package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

public class HeatExchangeCard extends W2Card_NEW {
    private List<String> SLHTC;
    private List<String> SROC;
    private List<String> RHEVAP;
    private List<String> METIC;
    private List<String> FETCHC;
    private List<Double> AFW;
    private List<Double> BFW;
    private List<Double> CFW;
    private List<Double> WINDH;
    private int numWaterBodies;
    private final String format = "%8.5f";

    public HeatExchangeCard(W2ControlFile w2ControlFile, int numWaterBodies) {
        super(w2ControlFile, "HEAT EXCH", numWaterBodies,
                W2Globals.constants(numWaterBodies, 9), 8, false);
        this.numWaterBodies = numWaterBodies;
        parseTable();
        init();
    }

    public void init() {
        SLHTC = new ArrayList<>();
        SROC = new ArrayList<>();
        RHEVAP = new ArrayList<>();
        METIC = new ArrayList<>();
        FETCHC = new ArrayList<>();
        AFW = new ArrayList<>();
        BFW = new ArrayList<>();
        CFW = new ArrayList<>();
        WINDH = new ArrayList<>();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = recordValuesList.get(i);
            SLHTC.add(record.get(0));
            SROC.add(record.get(1));
            RHEVAP.add(record.get(2));
            METIC.add(record.get(3));
            FETCHC.add(record.get(4));
            AFW.add(Double.valueOf(record.get(5)));
            BFW.add(Double.valueOf(record.get(6)));
            CFW.add(Double.valueOf(record.get(7)));
            WINDH.add(Double.valueOf(record.get(8)));
        }
    }

    public List<String> getIdentifiers() {
        return recordIdentifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.recordIdentifiers = identifiers;
    }

    public List<String> getSLHTC() {
        return SLHTC;
    }

    public void setSLHTC(List<String> SLHTC) {
        this.SLHTC = SLHTC;
        updateRecordValuesList();
    }

    public List<String> getSROC() {
        return SROC;
    }

    public void setSROC(List<String> SROC) {
        this.SROC = SROC;
        updateRecordValuesList();
    }

    public List<String> getRHEVAP() {
        return RHEVAP;
    }

    public void setRHEVAP(List<String> RHEVAP) {
        this.RHEVAP = RHEVAP;
        updateRecordValuesList();
    }

    public List<String> getMETIC() {
        return METIC;
    }

    public void setMETIC(List<String> METIC) {
        this.METIC = METIC;
        updateRecordValuesList();
    }

    public List<String> getFETCHC() {
        return FETCHC;
    }

    public void setFETCHC(List<String> FETCHC) {
        this.FETCHC = FETCHC;
        updateRecordValuesList();
    }

    public List<Double> getAFW() {
        return AFW;
    }

    public void setAFW(List<Double> AFW) {
        this.AFW = AFW;
        updateRecordValuesList();
    }

    public List<Double> getBFW() {
        return BFW;
    }

    public void setBFW(List<Double> BFW) {
        this.BFW = BFW;
        updateRecordValuesList();
    }

    public List<Double> getCFW() {
        return CFW;
    }

    public void setCFW(List<Double> CFW) {
        this.CFW = CFW;
        updateRecordValuesList();
    }

    public List<Double> getWINDH() {
        return WINDH;
    }

    public void setWINDH(List<Double> WINDH) {
        this.WINDH = WINDH;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> record = new ArrayList<>();
            record.add(SLHTC.get(i));
            record.add(SROC.get(i));
            record.add(RHEVAP.get(i));
            record.add(METIC.get(i));
            record.add(FETCHC.get(i));
            record.add(String.format(format, AFW.get(i)));
            record.add(String.format(format, BFW.get(i)));
            record.add(String.format(format, CFW.get(i)));
            record.add(String.format(format, WINDH.get(i)));
            recordValuesList.add(record);
        }
    }
}

