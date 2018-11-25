package w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Active Derived Constituents Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent, with a value for each waterbody.
 */
public class ActiveDerivedConstituentsCard extends Card {
    private List<String> constituentNames; // Constituent names
    private List<List<String>> Data;     // State of each constituent (ON or OFF)
    private int numConstituents;
    private int numWaterbodies;

    public ActiveDerivedConstituentsCard(W2ControlFile w2ControlFile, int numConstituents, int numWaterbodies) {
        super(w2ControlFile, "CST DERI",
                (int) Math.ceil(numConstituents/9.0) * numConstituents);
        this.numConstituents = numConstituents;
        this.numWaterbodies = numWaterbodies;
        parseText();
    }

    public List<String> getConstituentNames() {
        return constituentNames;
    }

    public void setConstituentNames(List<String> constituentNames) {
        this.constituentNames = constituentNames;
        updateText();
    }

    public List<List<String>> getData() {
        return Data;
    }

    public void setCAC(List<List<String>> Data) {
        this.Data = Data;
        updateText();
    }

    @Override
    public void parseText() {
        constituentNames = new ArrayList<>();
        Data = new ArrayList<>();
        List<List<String>> Records = new ArrayList<>();

        for (int jc = 0; jc < numConstituents; jc++) {
            List<String> Values = parseRecord(recordLines, jc, numWaterbodies);
            Records.add(Values);
        }

        for (int i = 0; i < numWaterbodies; i++) {
            Data.add(new ArrayList<>());
        }

        Records.forEach(record -> {
            constituentNames.add(record.get(0));
            for (int i = 0; i < numWaterbodies; i++) {
                int col = i + 1;
                Data.get(i).add(record.get(col));
            }
        });
    }

    @Override
    public void updateText() {
        for (int i = 0; i < numConstituents; i++) {
            String str = String.format("%-8s", constituentNames.get(i));
            for (int j = 0; j < numWaterbodies; j++) {
                str += String.format("%8s", Data.get(j).get(i));
                recordLines.set(i, str);
            }
        }
    }
}
