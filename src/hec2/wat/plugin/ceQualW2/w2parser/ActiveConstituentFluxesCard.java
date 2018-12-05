package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Active W2Constituent Fluxes W2Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent, with a value for each waterbody.
 */
public class ActiveConstituentFluxesCard extends W2Card {
    private List<String> constituentNames; // W2Constituent names
    private List<List<String>> values;     // State of each constituent (ON or OFF)
    private int numFluxes;
    private int numWaterbodies;

    public ActiveConstituentFluxesCard(W2ControlFile w2ControlFile, int numFluxes, int numWaterbodies) {
        super(w2ControlFile, "CST FLUX",
                (int) Math.ceil(numFluxes/9.0) * numFluxes);
        this.numFluxes = numFluxes;
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

        for (int jc = 0; jc < numFluxes; jc++) {
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
        // TODO Update to handle multi-line fluxes
//        for (int i = 0; i < numFluxes; i++) {
//            String str = String.format("%-8s", constituentNames.get(i));
//            for (int j = 0; j < numWaterbodies; j++) {
//                str += String.format("%8s", values.get(j).get(i));
//                table.set(i, str);
//            }
//        }
    }
}
