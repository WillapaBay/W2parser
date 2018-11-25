package w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Active Constituent Fluxes Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent, with a value for each waterbody.
 */
public class ActiveConstituentFluxesCard extends Card {
    private List<String> constituentNames; // Constituent names
    private List<List<String>> values;     // State of each constituent (ON or OFF)
    private int numFluxes;
    private int numWaterbodies;

    public ActiveConstituentFluxesCard(W2ControlFile w2ControlFile, int numFluxes, int numWaterbodies) {
        super(w2ControlFile, "CST FLUX", numFluxes);
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
        for (int jwb = 0; jwb < numWaterbodies; jwb++) {
            values.add(new ArrayList<>());
        }

        for (int jc = 0; jc < numFluxes; jc++) {
            List<String> fields = parseLine(table.get(jc), 8, 1, 10);
            constituentNames.add(fields.get(0));
            for (int jwb = 0; jwb < numWaterbodies; jwb++) {
                int col = jwb + 1;
                values.get(jwb).add(fields.get(col));
            }
        }
    }

    @Override
    public void updateText() {
        for (int i = 0; i < numFluxes; i++) {
            String str = String.format("%-8s", constituentNames.get(i));
            for (int j = 0; j < numWaterbodies; j++) {
                str += String.format("%8s", values.get(j).get(i));
                table.set(i, str);
            }
        }
    }
}
