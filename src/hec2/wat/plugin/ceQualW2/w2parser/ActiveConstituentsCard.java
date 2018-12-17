package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

import static hec2.wat.plugin.ceQualW2.w2parser.W2Globals.ones;

/**
 * Active Constituents Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent.
 */
public class ActiveConstituentsCard extends W2Card {
    private List<String> states; // State of each constituent (ON or OFF); Variable: CAC

    public ActiveConstituentsCard(W2ControlFile w2ControlFile, int numConstituents) {
        super(w2ControlFile, "CST ACTIVE", numConstituents, ones(numConstituents),
               8, false);
        parseTable();
        init();
    }

    private void init() {
        states = new ArrayList<>();
        for (List<String> values : recordValuesList) {
            states.add(values.get(0));
        }
    }

    /**
     * Return list of constituent names
     * @return List of constituent names
     */
    public List<String> getConstituentNames() {
        return recordIdentifiers;
    }

    /**
     * Set constituent names
     * @param constituentNames Constituent names
     */
    public void setConstituentNames(List<String> constituentNames) {
        this.recordIdentifiers = constituentNames;
    }

    /**
     * Get states for each constituent
     * @return States for each constituent
     */
    public List<String> getStates() {
        return states;
    }

    /**
     * Get states for each constituent
     * @return States for each constituent
     */
    public List<String> getCAC() {
        return getStates();
    }

    /**
     * Set constituent states
     * @param states List of constituent states
     */
    public void setStates(List<String> states) {
        this.states = states;
    }

    /**
     * Set constituent states
     * @param CAC List of constituent states
     */
    public void setCAC(List<String> CAC) {
        setStates(CAC);
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
