package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshot Print Card
 */
public class SnapshotPrintCard extends W2Card_NEW {
    private List<String> SNPC; // Specifies if information is written to shapshot file (ON or OFF)
    private List<Integer> NSNP; // Number of snapshot dates
    private List<Integer> NISNP; // Number of snapshot segments
    private int numWaterBodies;

    public SnapshotPrintCard(W2ControlFile w2ControlFile, int numWaterBodies) {
        super(w2ControlFile, "SNP PRINT", numWaterBodies,
                W2Globals.constants(numWaterBodies, 9),
                8, false);
        this.numWaterBodies = numWaterBodies;
        parseTable();
        init();
    }

    public void init() {
        SNPC = new ArrayList<>();
        NSNP = new ArrayList<>();
        NISNP = new ArrayList<>();

        for (List<String> values : recordValuesList) {
            SNPC.add(values.get(0));
            NSNP.add(Integer.valueOf(values.get(1)));
            NISNP.add(Integer.valueOf(values.get(2)));
        }
    }

    public List<String> getSNPC() {
        return SNPC;
    }

    public void setSNPC(List<String> SNPC) {
        this.SNPC = SNPC;
        updateRecordValuesList();
    }

    public List<Integer> getNSNP() {
        return NSNP;
    }

    public void setNSNP(List<Integer> NSNP) {
        this.NSNP = NSNP;
        updateRecordValuesList();
    }

    public List<Integer> getNISNP() {
        return NISNP;
    }

    public void setNISNP(List<Integer> NISNP) {
        this.NISNP = NISNP;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(SNPC));
            values.add(String.valueOf(NSNP));
            values.add(String.valueOf(NISNP));
            recordValuesList.add(values);
        }
    }
}
