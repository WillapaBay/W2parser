package w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Number of Structures Card
 *
 * This card has one line per water body
 */
public class NumberStructuresCard extends Card {
    private List<Integer> NSTR;     // Number of branch outlet structures
    private List<String> DYNELEV;   // Use the dynamic centerline elevation for the structure? (ON, OFF, blank (equals OFF))
    private List<String> identifiers;
    private static final String DYNELEV_default = Globals.OFF;
    private int numBranches;

    public NumberStructuresCard(W2ControlFile w2ControlFile, int numBranches) {
        super(w2ControlFile, CardNames.NumberStructures, numBranches);
        this.numBranches = numBranches;
        parseTable();
    }

    public List<Integer> getNSTR() {
        return NSTR;
    }

    public void setNSTR(List<Integer> NSTR) {
        this.NSTR = NSTR;
        updateText();
    }

    public List<String> getDYNELEV() {
        return DYNELEV;
    }

    public void setDYNELEV(List<String> DYNELEV) {
        this.DYNELEV = DYNELEV;
        updateText();
    }

    @Override
    public void parseTable() {
        NSTR = new ArrayList<>();
        DYNELEV = new ArrayList<>();
        identifiers = new ArrayList<>();

        for (int i = 0; i < numBranches; i++) {
            List<String> fields = parseLine(table.get(i), 8, 1, 10);
            identifiers.add(fields.get(0));
            NSTR.add(Integer.parseInt(fields.get(1)));
            if (fields.size() > 2) {
                DYNELEV.add(fields.get(2));
            } else {
                DYNELEV.add(DYNELEV_default);
            }
        }
    }

    @Override
    public void updateText() {
        for (int i = 0; i < numBranches; i++) {
            String str = String.format("%-8s%8d%8s",
                    identifiers.get(i), NSTR.get(i), DYNELEV.get(i));
            table.set(i, str);
        }
    }
}
