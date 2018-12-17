package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

import static hec2.wat.plugin.ceQualW2.w2parser.W2Globals.ones;

/**
 * Active Derived Constituents Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent.
 */
public class ActiveDerivedConstituentsCard extends W2Card_NEW {
    private List<List<String>> states; // State of each constituent (ON or OFF); Variable: CAC

    public ActiveDerivedConstituentsCard(W2ControlFile w2ControlFile, int numConstituents,
                                         int numWaterBodies) {
        super(w2ControlFile, "CST DERI", numConstituents,
                W2Globals.constants(numConstituents, numWaterBodies),
               8, false);
        parseTable();
        states = recordValuesList;
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
    public List<List<String>> getStates() {
        return states;
    }

    /**
     * Set constituent states
     * @param states List of constituent states
     */
    public void setStates(List<List<String>> states) {
        this.states = states;
        this.recordValuesList = states;
    }

    @Override
    public void updateRecordValuesList() {
        // Not needed or used for this class
    }
}
