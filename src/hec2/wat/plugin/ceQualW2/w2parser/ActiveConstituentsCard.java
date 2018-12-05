package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Active constituents W2Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent.
 */
public class ActiveConstituentsCard extends W2Card {
    private List<String> constituentNames; // W2Constituent names
    private List<String> CAC;     // State of each constituent (ON or OFF)
    private int numConstituents;

    public ActiveConstituentsCard(W2ControlFile w2ControlFile, int numConstituents) {
        super(w2ControlFile, "CST ACTIVE", numConstituents);
        this.numConstituents = numConstituents;
        parseTable();
    }

    public List<String> getConstituentNames() {
        return constituentNames;
    }

    public void setConstituentNames(List<String> constituentNames) {
        this.constituentNames = constituentNames;
        updateText();
    }

    public List<String> getCAC() {
        return CAC;
    }

    public void setCAC(List<String> CAC) {
        this.CAC = CAC;
        updateText();
    }

    @Override
    public void parseTable() {
        constituentNames = new ArrayList<>();
        CAC = new ArrayList<>();

        for (int i = 0; i < numConstituents; i++) {
            List<String> fields = parseLine(table.get(i), 8, 1, 10);
            constituentNames.add(fields.get(0));
            CAC.add(fields.get(1));
        }
    }

    @Override
    public void updateText() {
        table.clear();
        for (int i = 0; i < numConstituents; i++) {
            String str = String.format("%-8s%8s",
                    constituentNames.get(i), CAC.get(i));
            table.add(str);
        }
    }
}
