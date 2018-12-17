package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculations Card
 *
 * This card has one line per water body
 */
public class CalculationsCard extends W2Card_NEW {
    private List<String> VBC; // Volume balance calculation, ON or OFF
    private List<String> EBC; // Thermal energy balance calculation, ON or OFF
    private List<String> MBC; // Mass balance calculation, ON or OFF
    private List<String> PQC; // Density placed inflows, ON or OFF
    private List<String> EVC; // Evaporation included in water budget, ON or OFF
    private List<String> PRC; // Precipitation included, ON or OFF
    private int numWaterBodies;

    public CalculationsCard(W2ControlFile w2ControlFile, int numWaterBodies) {
        super(w2ControlFile, W2CardNames.Calculations, numWaterBodies,
                W2Globals.constants(numWaterBodies,6),
                8, false);
        this.numWaterBodies = numWaterBodies;
        parseTable();
        init();
    }

    public void init() {
        VBC = new ArrayList<>();
        EBC = new ArrayList<>();
        MBC = new ArrayList<>();
        PQC = new ArrayList<>();
        EVC = new ArrayList<>();
        PRC = new ArrayList<>();

        for (List<String> values : recordValuesList) {
            VBC.add(String.valueOf(values.get(0)));
            EBC.add(String.valueOf(values.get(1)));
            MBC.add(String.valueOf(values.get(2)));
            PQC.add(String.valueOf(values.get(3)));
            EVC.add(String.valueOf(values.get(4)));
            PRC.add(String.valueOf(values.get(5)));
        }
    }

    public List<String> getIdentifiers() {
        return recordIdentifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        recordIdentifiers = identifiers;
    }

    public List<String> getVBC() { return VBC; }

    public void setVBC(List<String> VBC) {
        this.VBC = VBC;
        updateRecordValuesList();
    }

    public List<String> getEBC() { return EBC; }

    public void setEBC(List<String> EBC) {
        this.EBC = EBC;
        updateRecordValuesList();
    }

    public List<String> getMBC() { return MBC; }

    public void setMBC(List<String> MBC) {
        this.MBC = MBC;
        updateRecordValuesList();
    }

    public List<String> getPQC() { return PQC; }

    public void setPQC(List<String> PQC) {
        this.PQC = PQC;
        updateRecordValuesList();
    }

    public List<String> getEVC() { return EVC; }

    public void setEVC(List<String> EVC) {
        this.EVC = EVC;
        updateRecordValuesList();
    }

    public List<String> getPRC() { return PRC; }

    public void setPRC(List<String> PRC) {
        this.PRC = PRC;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (int i = 0; i < numWaterBodies; i++) {
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(VBC));
            values.add(String.valueOf(EBC));
            values.add(String.valueOf(MBC));
            values.add(String.valueOf(PQC));
            values.add(String.valueOf(EVC));
            values.add(String.valueOf(PRC));
            recordValuesList.add(values);
        }
    }
}


