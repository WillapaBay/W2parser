package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

/**
 * Time Series Record (TSR) Plot W2Card_OLD
 */
public class TsrPlotCard extends W2Card_OLD {
    private String TSRC;
    private int NTSR;
    private int NITSR;

    public TsrPlotCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "TSR PLOT", 1);
        parseTable();
    }

    public String getTSRC() {
        return TSRC;
    }

    public void setTSRC(String tsrc) {
        this.TSRC = tsrc;
        updateText();
    }

    public int getNTSR() {
        return NTSR;
    }

    public void setNTSR(int ntsr) {
        this.NTSR = ntsr;
        updateText();
    }

    public int getNITSR() {
        return NITSR;
    }

    public void setNITSR(int nitsr) {
        this.NITSR = nitsr;
        updateText();
    }

    @Override
    public void parseTable() {
        List<String> fields = parseLine(table.get(0), 8, 2, 10);
        TSRC = fields.get(0);
        NTSR = Integer.parseInt(fields.get(1));
        NITSR = Integer.parseInt(fields.get(2));
    }

    @Override
    public void updateText() {
        String str = String.format("%8s%8s%8d%8d",
                "", TSRC, NTSR, NITSR);
        table.set(0, str);
    }
}
