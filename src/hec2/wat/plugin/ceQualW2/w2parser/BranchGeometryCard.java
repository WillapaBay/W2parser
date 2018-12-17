package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Branch Geometry Card
 */
public class BranchGeometryCard extends W2Card {
    private List<Integer> US;         // Branch upstream segment
    private List<Integer> DS;         // Branch downstream segment
    private List<Integer> UHS;        // Upstream boundary condition
    private List<Integer> DHS;        // Downstream boundary condition
    private List<Integer> UQB;        // Upstream internal flow boundary condition - IGNORE
    private List<Integer> DQB;        // Downstream internal flow boundary condition - IGNORE
    private List<Integer> NLMIN;      // Minimum number of layers for a segment to be active
    private List<Double> slopeList;   // Branch bottom slope (actual)
    private List<Double> slopeClist;  // Hydraulic equivalent branch slope
    private int numBranches;

    public BranchGeometryCard(W2ControlFile w2ControlFile, int numBranches) {
        super(w2ControlFile, "BRANCH G", numBranches,
                W2Globals.constants(numBranches, 9),
                8, false);
        this.numBranches = numBranches;
        parseTable();
        init();
    }

    public void init() {
        US = new ArrayList<>();
        DS = new ArrayList<>();
        UHS = new ArrayList<>();
        DHS = new ArrayList<>();
        UQB = new ArrayList<>();
        DQB = new ArrayList<>();
        NLMIN = new ArrayList<>();
        slopeList = new ArrayList<>();
        slopeClist = new ArrayList<>();

        for (List<String> values : recordValuesList) {
            US.add(Integer.valueOf(values.get(0)));
            DS.add(Integer.valueOf(values.get(1)));
            UHS.add(Integer.valueOf(values.get(2)));
            DHS.add(Integer.valueOf(values.get(3)));
            UQB.add(Integer.valueOf(values.get(4)));
            DQB.add(Integer.valueOf(values.get(5)));
            NLMIN.add(Integer.valueOf(values.get(6)));
            slopeList.add(Double.valueOf(values.get(7)));
            if (values.size() > 8) {
                slopeClist.add(Double.valueOf(values.get(8)));
            } else {
                // TODO: test that this is valid
                slopeClist.add(Double.valueOf(values.get(7)));
            }
        }
    }

    public List<Integer> getUS() {
        return US;
    }

    public void setUS(List<Integer> US) {
        this.US = US;
        updateRecordValuesList();
    }

    public List<Integer> getDS() {
        return DS;
    }

    public void setDS(List<Integer> DS) {
        this.DS = DS;
        updateRecordValuesList();
    }

    public List<Integer> getUHS() {
        return UHS;
    }

    public void setUHS(List<Integer> UHS) {
        this.UHS = UHS;
        updateRecordValuesList();
    }

    public List<Integer> getDHS() {
        return DHS;
    }

    public void setDHS(List<Integer> DHS) {
        this.DHS = DHS;
        updateRecordValuesList();
    }

    public List<Integer> getUQB() {
        return UQB;
    }

    public void setUQB(List<Integer> UQB) {
        this.UQB = UQB;
        updateRecordValuesList();
    }

    public List<Integer> getDQB() {
        return DQB;
    }

    public void setDQB(List<Integer> DQB) {
        this.DQB = DQB;
        updateRecordValuesList();
    }

    public List<Integer> getNLMIN() {
        return NLMIN;
    }

    public void setNLMIN(List<Integer> NLMIN) {
        this.NLMIN = NLMIN;
        updateRecordValuesList();
    }

    public List<Double> getSlopeList() {
        return slopeList;
    }

    public void setSlopeList(List<Double> slopeList) {
        this.slopeList = slopeList;
        updateRecordValuesList();
    }

    public List<Double> getSlopeClist() {
        return slopeClist;
    }

    public void setSlopeClist(List<Double> slopeClist) {
        this.slopeClist = slopeClist;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (int i = 0; i < numBranches; i++) {
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(US));
            values.add(String.valueOf(DS));
            values.add(String.valueOf(UHS));
            values.add(String.valueOf(DHS));
            values.add(String.valueOf(UQB));
            values.add(String.valueOf(DQB));
            values.add(String.valueOf(NLMIN));
            values.add(String.valueOf(slopeList));
            values.add(String.valueOf(slopeClist));
            recordValuesList.add(values);
        }
    }
}
