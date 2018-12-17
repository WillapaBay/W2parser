package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

/**
 * Distributed Tributary Constituents Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent, with a value for each waterbody.
 */
public class DistributedTributaryConstituentsCard extends W2Card {

    public DistributedTributaryConstituentsCard(W2ControlFile w2ControlFile, int numConstituents, int numBranches) {
        super(w2ControlFile,"CDT CON", numConstituents,
                W2Globals.constants(numConstituents, numBranches),
                8, false);
        parseTable();
    }

    /**
     * Return list of constituent names
     * @return List of constituent names
     */
    public List<String> getConstituentNames() {
        return this.recordIdentifiers;
    }

    /**
     * Set constituent names
     * @param constituentNames Constituent names
     */
    public void setConstituentNames(List<String> constituentNames) {
        this.recordIdentifiers = constituentNames;
    }

    /**
     * Return list of constituent states
     * @return List of constituent states
     */
    public List<List<String>> getStates() {
        return this.recordValuesList;
    }

    /**
     * Return state for a specified constituent index and branch index
     * @param constituentIdx Constituent index
     * @param branchIdx Branch index
     * @return State
     */
    public String getState(int constituentIdx, int branchIdx) {
        return recordValuesList.get(constituentIdx).get(branchIdx);
    }

    /**
     * Set constituent states
     * @param states Constituent states
     */
    public void setStates(List<List<String>> states) {
        this.recordValuesList = states;
    }

    @Override
    public void updateRecordValuesList() {
        // Not needed for this class
    }

}
