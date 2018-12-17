package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Miscellaneous Card
 */
public class MiscellCard extends W2Card_NEW {
    private int NDAY;
    private String SELECTC;
    private String HABTATC;
    private String ENVIRPC;
    private String AERATEC;
    private String INITUWL;

    public MiscellCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "MISCELL", 1,
                W2Globals.constants(1, 6), 8,
                false);
        parseTable();
        init();
    }

    public void init() {
        List<String> values = recordValuesList.get(0);
        NDAY    = Integer.valueOf(values.get(0));
        // Handle legacy W2 control files
        if (values.size() > 1) {
            SELECTC = values.get(1);
            HABTATC = values.get(2);
            ENVIRPC = values.get(3);
            AERATEC = values.get(4);
            INITUWL = values.get(5);
        } else {
            SELECTC = "OFF";
            HABTATC = "OFF";
            ENVIRPC = "OFF";
            AERATEC = "OFF";
            INITUWL = "OFF";
        }
    }

    public int getNday() {
        return NDAY;
    }

    public void setNday(int NDAY) {
        this.NDAY = NDAY;
        updateRecordValuesList();
    }

    public String getSELECTC() {
        return SELECTC;
    }

    public void setSELECTC(String SELECTC) {
        this.SELECTC = SELECTC.toUpperCase();
        updateRecordValuesList();
    }

    public String getENVIRPC() {
        return ENVIRPC;
    }

    public void setENVIRPC(String ENVIRPC) {
        this.ENVIRPC = ENVIRPC.toUpperCase();
        updateRecordValuesList();
    }

    public String getAERATEC() {
        return AERATEC;
    }

    public void setAERATEC(String AERATEC) {
        this.AERATEC = AERATEC.toUpperCase();
        updateRecordValuesList();
    }

    public String getINITUWL() {
        return INITUWL;
    }

    public void setINITUWL(String INITUWL) {
        this.INITUWL = INITUWL.toUpperCase();
        updateRecordValuesList();
    }

    public String getHABTATC() {
        return HABTATC;
    }

    public void setHABTATC(String HABTATC) {
        this.HABTATC = HABTATC.toUpperCase();
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(NDAY));
        values.add(SELECTC);
        values.add(HABTATC);
        values.add(ENVIRPC);
        values.add(AERATEC);
        values.add(INITUWL);
        recordValuesList.add(values);
    }
}
