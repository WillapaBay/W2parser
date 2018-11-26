package w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Active Derived constituents Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent, with a value for each waterbody.
 */
public class ActiveDerivedConstituentsCard extends Card {
    private List<String> constituentNames; // Constituent names
    private List<List<String>> values;     // State of each constituent (ON or OFF)
    private int numConstituents;
    private int numWaterbodies;

    public ActiveDerivedConstituentsCard(W2ControlFile w2ControlFile, int numConstituents, int numWaterbodies) {
        super(w2ControlFile, "CST DERI",
                (int) Math.ceil(numConstituents/9.0) * numConstituents);
        this.numConstituents = numConstituents;
        this.numWaterbodies = numWaterbodies;
        parseTable();
    }

    public List<String> getConstituentNames() {
        return constituentNames;
    }

    public void setConstituentNames(List<String> constituentNames) {
        this.constituentNames = constituentNames;
        updateText();
    }

    public List<List<String>> getValues() {
        return values;
    }

    public void setCAC(List<List<String>> values) {
        this.values = values;
        updateText();
    }

    @Override
    public void parseTable() {
        constituentNames = new ArrayList<>();
        values = new ArrayList<>();
        List<List<String>> records = new ArrayList<>();

        for (int jc = 0; jc < numConstituents; jc++) {
            List<String> Values = parseRecord(table, jc, numWaterbodies);
            records.add(Values);
        }

        for (int i = 0; i < numWaterbodies; i++) {
            values.add(new ArrayList<>());
        }

        records.forEach(record -> {
            constituentNames.add(record.get(0));
            for (int i = 0; i < numWaterbodies; i++) {
                int col = i + 1;
                values.get(i).add(record.get(col));
            }
        });
    }

    @Override
    public void updateText() {
        // TODO Implement multi-line record method
//        for (int i = 0; i < numConstituents; i++) {
//            String str = String.format("%-8s", constituentNames.get(i));
//            for (int j = 0; j < numWaterbodies; j++) {
//                str += String.format("%8s", values.get(j).get(i));
//                table.set(i, str);
//            }
//        }
    }
}
