package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Branch Geometry Card
 */
public class BranchGeometryCard extends Card {
    private List<Integer> US;         // Branch upstream segment
    private List<Integer> DS;         // Branch downstream segment
    private List<Integer> UHS;        // Upstream boundary condition
    private List<Integer> DHS;        // Downstream boundary condition
    private List<Integer> UQB;        // Upstream internal flow boundary condition - IGNORE
    private List<Integer> DQB;        // Downstream internal flow boundary condition - IGNORE
    private List<Integer> NLMIN;      // Minimum number of layers for a segment to be active
    private List<Double> slopeList;       // Branch bottom slope (actual)
    private List<Double> slopeClist;      // Hydraulic equivalent branch slope
    private List<String> identifiers; // identifiers

    public BranchGeometryCard(W2ControlFile w2ControlFile, int numBranches) {
        super(w2ControlFile, "BRANCH G", numBranches);
        parseTable();
    }

    public List<Integer> getUS() {
        return US;
    }

    public void setUS(List<Integer> US) {
        this.US = US;
        updateText();
    }

    public List<Integer> getDS() {
        return DS;
    }

    public void setDS(List<Integer> DS) {
        this.DS = DS;
        updateText();
    }

    public List<Integer> getUHS() {
        return UHS;
    }

    public void setUHS(List<Integer> UHS) {
        this.UHS = UHS;
        updateText();
    }

    public List<Integer> getDHS() {
        return DHS;
    }

    public void setDHS(List<Integer> DHS) {
        this.DHS = DHS;
        updateText();
    }

    public List<Integer> getUQB() {
        return UQB;
    }

    public void setUQB(List<Integer> UQB) {
        this.UQB = UQB;
        updateText();
    }

    public List<Integer> getDQB() {
        return DQB;
    }

    public void setDQB(List<Integer> DQB) {
        this.DQB = DQB;
        updateText();
    }

    public List<Integer> getNLMIN() {
        return NLMIN;
    }

    public void setNLMIN(List<Integer> NLMIN) {
        this.NLMIN = NLMIN;
        updateText();
    }

    public List<Double> getSlopeList() {
        return slopeList;
    }

    public void setSlopeList(List<Double> slopeList) {
        this.slopeList = slopeList;
        updateText();
    }

    public List<Double> getSlopeClist() {
        return slopeClist;
    }

    public void setSlopeClist(List<Double> slopeClist) {
        this.slopeClist = slopeClist;
        updateText();
    }

    @Override
    public void parseTable() {
        US = new ArrayList<>();
        DS = new ArrayList<>();
        UHS = new ArrayList<>();
        DHS = new ArrayList<>();
        UQB = new ArrayList<>();
        DQB = new ArrayList<>();
        NLMIN = new ArrayList<>();
        slopeList = new ArrayList<>();
        slopeClist = new ArrayList<>();
        identifiers = new ArrayList<>();

        for (int i = 0; i < numCardDataLines; i++) {
            List<String> records = parseLine(table.get(i), 8, 1, 10);
            identifiers.add(records.get(0));
            US.add(Integer.parseInt(records.get(1)));
            DS.add(Integer.parseInt(records.get(2)));
            UHS.add(Integer.parseInt(records.get(3)));
            DHS.add(Integer.parseInt(records.get(4)));
            UQB.add(Integer.parseInt(records.get(5)));
            DQB.add(Integer.parseInt(records.get(6)));
            NLMIN.add(Integer.parseInt(records.get(7)));
            slopeList.add(Double.parseDouble(records.get(8)));
            slopeClist.add(Double.parseDouble(records.get(9)));
        }
    }

    @Override
    public void updateText() {
        table.clear();
        for (int i = 0; i < numCardDataLines; i++) {
            String str = String.format("%-8s%8d%8d%8d%8d%8d%8d%8d%8.5f%8.5f",
                    identifiers.get(i), US.get(i), DS.get(i), UHS.get(i), DHS.get(i), UQB.get(i),
                    DQB.get(i), NLMIN.get(i), slopeList.get(i), slopeClist.get(i));
            table.add(str);
        }
    }
}

