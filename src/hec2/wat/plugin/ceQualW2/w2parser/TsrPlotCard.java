package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Time Series Record (TSR) Plot Card
 */
public class TsrPlotCard extends W2Card {
    private String TSRC;
    private int NTSR;
    private int NITSR;

    public TsrPlotCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "TSR PLOT", 1,
                W2Globals.constants(1, 3), 8,
                false);
        parseTable();
        init();
    }

    public void init() {
        List<String> values = recordValuesList.get(0);
        TSRC  = values.get(0);
        NTSR  = Integer.valueOf(values.get(1));
        NITSR = Integer.valueOf(values.get(2));
    }

    public String getTSRC() {
        return TSRC;
    }

    public void setTSRC(String tsrc) {
        this.TSRC = tsrc;
        updateRecordValuesList();
    }

    public int getNTSR() {
        return NTSR;
    }

    public void setNTSR(int ntsr) {
        this.NTSR = ntsr;
        updateRecordValuesList();
    }

    public int getNITSR() {
        return NITSR;
    }

    public void setNITSR(int nitsr) {
        this.NITSR = nitsr;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(TSRC));
        values.add(String.valueOf(NTSR));
        values.add(String.valueOf(NITSR));
        recordValuesList.add(values);
    }
}
