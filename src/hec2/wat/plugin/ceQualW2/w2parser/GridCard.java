package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid Card
 */
public class GridCard extends W2Card_NEW {
    private int NWB;
    private int NBR;
    private int IMX;
    private int KMX;
    private int NPROC;
    private String CLOSEC;

    public GridCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "GRID", 1, W2Globals.constants(1, 6),
                8, false);
        parseTable();

        List<String> values = recordValuesList.get(0);
        NWB    = Integer.valueOf(values.get(0));
        NBR    = Integer.valueOf(values.get(1));
        IMX    = Integer.valueOf(values.get(2));
        KMX    = Integer.valueOf(values.get(3));
        if (values.size() > 4)
            NPROC  = Integer.valueOf(values.get(4));
        else
            NPROC = 1;
        if (values.size() > 5)
            CLOSEC = values.get(5);
        else
            CLOSEC = "ON";
    }

    public int getNumWaterBodies() {
        return NWB;
    }

    public void setNumWaterBodies(int nwb) {
        this.NWB = nwb;
        updateRecordValuesList();
    }

    public int getNumBranches() {
        return NBR;
    }

    public void setNumBranches(int nbr) {
        this.NBR = nbr;
        updateRecordValuesList();
    }

    public int getNumCrossSections() {
        return IMX;
    }

    public void setNumCrossSections(int imx) {
        this.IMX = imx;
        updateRecordValuesList();
    }

    public int setNumLayers() {
        return KMX;
    }

    public void setNumLayers(int kmx) {
        this.KMX = kmx;
        updateRecordValuesList();
    }

    public int getNumProcessors() {
        return NPROC;
    }

    public void setNumProcessors(int nproc) {
        this.NPROC = nproc;
        updateRecordValuesList();
    }

    public String getCloseWindow() {
        return CLOSEC;
    }

    public void setCloseWindow(String closec) {
        this.CLOSEC = closec;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(NWB));
        values.add(String.valueOf(NBR));
        values.add(String.valueOf(IMX));
        values.add(String.valueOf(KMX));
        values.add(String.valueOf(NPROC));
        values.add(String.valueOf(CLOSEC));
        recordValuesList.add(values);
    }
}


