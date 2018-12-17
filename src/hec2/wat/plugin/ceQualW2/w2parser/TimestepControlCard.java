package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Timestep Control Card
 */
public class TimestepControlCard extends W2Card {
    private int NDT;
    private double DLTMIN;
    private String DLTINTR;
    private final String format = "%8.5f";

    public TimestepControlCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, W2CardNames.TimestepControl, 1,
                W2Globals.constants(1, 3),
                8, false);
        parseTable();
        init();
    }

    public void init() {
        List<String> values = recordValuesList.get(0);
        NDT     = Integer.valueOf(values.get(0));
        DLTMIN  = Double.valueOf(values.get(1));
        if (values.size() > 2)
            DLTINTR = values.get(2);
        else
            DLTINTR = "OFF";
    }

    public int getNDT() {
        return NDT;
    }

    public void setNDT(int NDT) {
        this.NDT = NDT;
        updateRecordValuesList();
    }

    public double getDLTMIN() {
        return DLTMIN;
    }

    public void setDLTMIN(double DLTMIN) {
        this.DLTMIN = DLTMIN;
        updateRecordValuesList();
    }

    public String getDLTINTR() {
        return DLTINTR;
    }

    public void setDLTINTR(String DLTINTR) {
        this.DLTINTR = DLTINTR.toUpperCase();
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(NDT));
        values.add(String.format(format, DLTMIN));
        values.add(String.valueOf(DLTINTR));
        recordValuesList.add(values);
    }
}
