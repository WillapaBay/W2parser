package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

/**
 * Miscellaneous W2Card_OLD
 */
public class MiscellCard extends W2Card_OLD {
    private int nday;
    private String SELECTC;
    private String HABTATC;
    private String ENVIRPC;
    private String AERATEC;
    private String INITUWL;

    public MiscellCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "MISCELL", 1);
        parseTable();
    }

    public int getNday() {
        return nday;
    }

    public void setNday(int nday) {
        this.nday = nday;
        updateText();
    }

    public String getSELECTC() {
        return SELECTC;
    }

    public void setSELECTC(String SELECTC) {
        this.SELECTC = SELECTC.toUpperCase();
        updateText();
    }

    public String getENVIRPC() {
        return ENVIRPC;
    }

    public void setENVIRPC(String ENVIRPC) {
        this.ENVIRPC = ENVIRPC.toUpperCase();
        updateText();
    }

    public String getAERATEC() {
        return AERATEC;
    }

    public void setAERATEC(String AERATEC) {
        this.AERATEC = AERATEC.toUpperCase();
        updateText();
    }

    public String getINITUWL() {
        return INITUWL;
    }

    public void setINITUWL(String INITUWL) {
        this.INITUWL = INITUWL.toUpperCase();
        updateText();
    }

    public String getHABTATC() {
        return HABTATC;
    }

    public void setHABTATC(String HABTATC) {
        this.HABTATC = HABTATC.toUpperCase();
        updateText();
    }

    @Override
    public void parseTable() {
        List<String> fields = parseLine(table.get(0), 8, 2, 10);
        nday = Integer.parseInt(fields.get(0));
        SELECTC = fields.get(1);
        HABTATC = fields.get(2);
        ENVIRPC = fields.get(3);
        AERATEC = fields.get(4);
        INITUWL = fields.get(5);
    }

    @Override
    public void updateText() {
        String str = String.format("%8s%8d%8s%8s%8s%8s%8s", "",
                nday, SELECTC, HABTATC, ENVIRPC, AERATEC, INITUWL);
        table.set(0, str);
    }
}

