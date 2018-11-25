package w2parser;

import java.util.List;

/**
 * Grid Card
 */
public class GridCard extends Card {
    private int NWB;
    private int NBR;
    private int IMX;
    private int KMX;
    private int NPROC;
    private String CLOSEC;
    private String identifier;

    public GridCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "GRID", 1);
        parseTable();
    }

    public int getNumWaterBodies() {
        return NWB;
    }

    public void setNumWaterBodies(int nwb) {
        this.NWB = nwb;
        updateText();
    }

    public int getNumBranches() {
        return NBR;
    }

    public void setNumBranches(int nbr) {
        this.NBR = nbr;
        updateText();
    }

    public int getNumCrossSections() {
        return IMX;
    }

    public void setNumCrossSections(int imx) {
        this.IMX = imx;
        updateText();
    }

    public int setNumLayers() {
        return KMX;
    }

    public void setNumLayers(int kmx) {
        this.KMX = kmx;
        updateText();
    }

    public int getNumProcessors() {
        return NPROC;
    }

    public void setNumProcessors(int nproc) {
        this.NPROC = nproc;
        updateText();
    }

    public String getCloseWindow() {
        return CLOSEC;
    }

    public void setCloseWindow(String closec) {
        this.CLOSEC = closec;
        updateText();
    }

    @Override
    public void parseTable() {
        List<String> fields = parseLine(table.get(0), 8, 1, 10);
        identifier = fields.get(0);
        NWB = Integer.parseInt(fields.get(1));
        NBR = Integer.parseInt(fields.get(2));
        IMX = Integer.parseInt(fields.get(3));
        KMX = Integer.parseInt(fields.get(4));
        NPROC = Integer.parseInt(fields.get(5));
        CLOSEC = fields.get(6);
    }

    @Override
    public void updateText() {
        String str = String.format("%-8s%8d%8d%8d%8d%8d%8s",
                identifier, NWB, NBR, IMX, KMX, NPROC, CLOSEC);
        table.set(0, str);
    }
}


