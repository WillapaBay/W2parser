package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

import static hec2.wat.plugin.ceQualW2.w2parser.W2Globals.ones;

/**
 * Active W2Constituent Fluxes W2Card_OLD
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent, with a value for each waterbody.
 */
public class DistributedTributariesCard extends W2Card {
    private List<String> states;

    public DistributedTributariesCard(W2ControlFile w2ControlFile, int numBranches) {
        super(w2ControlFile, W2CardNames.DistributedTributaries, numBranches,
                ones(numBranches), 8, false);
        parseTable();
        init();
    }

    public void init() {
        states = new ArrayList<>();
        for (List<String> values : recordValuesList) {
            states.add(values.get(0));
        }
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        for (String state : states) {
            List<String> values = new ArrayList<>();
            values.add(state);
            recordValuesList.add(values);
        }
    }

}
