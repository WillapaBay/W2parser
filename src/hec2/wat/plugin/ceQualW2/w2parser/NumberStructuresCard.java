package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Number of Structures Card
 *
 * This card has one line per water body
 */
public class NumberStructuresCard extends W2Card {
    private List<Integer> NSTR;     // Number of branch outlet structures
    private List<String> DYNELEV;   // Use the dynamic centerline elevation for the structure?
                                    // Valid values: ON, OFF, or blank (equals OFF)
    private final String DYNELEV_DEFAULT = W2Globals.OFF;
    private int numBranches;

    public NumberStructuresCard(W2ControlFile w2ControlFile, int numBranches) {
        super(w2ControlFile, W2CardNames.NumberStructures, numBranches,
                W2Globals.constants(numBranches, 2), 8,
                false);
        this.numBranches = numBranches;
        parseTable();
        init();
    }

    public void init() {
        NSTR = new ArrayList<>();
        DYNELEV = new ArrayList<>();
        for (int i = 0; i < numBranches; i++) {
            List<String> record = recordValuesList.get(i);
            NSTR.add(Integer.valueOf(record.get(0)));
            if (record.size() > 1)
                DYNELEV.add(record.get(1));
            else
                DYNELEV.add(DYNELEV_DEFAULT);
        }
    }

    public List<Integer> getNSTR() {
        return NSTR;
    }

    public void setNSTR(List<Integer> NSTR) {
        this.NSTR = NSTR;
        updateRecordValuesList();
    }

    public List<String> getDYNELEV() {
        return DYNELEV;
    }

    public void setDYNELEV(List<String> DYNELEV) {
        this.DYNELEV = DYNELEV;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (int i = 0; i < numBranches; i++) {
            List<String> record = new ArrayList<>();
            record.add(String.valueOf(NSTR.get(i)));
            if (DYNELEV.size() > 0)
                record.add(DYNELEV.get(i));
            recordValuesList.add(record);
        }
    }
}
