package w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Active Constituents Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent.
 */
public class ActiveConstituentsCard extends Card {
    private List<String> constituentNames; // Constituent names
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
            List<String> Fields = parseLine(recordLines.get(i), 8, 1, 10);
            constituentNames.add(Fields.get(0));
            CAC.add(Fields.get(1));
        }
    }

    @Override
    public void updateText() {
        for (int i = 0; i < numConstituents; i++) {
            String str = String.format("%-8s%8s",
                    constituentNames.get(i), CAC.get(i));
            recordLines.set(i, str);
        }
    }
}
