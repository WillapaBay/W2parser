package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

/**
 * Active Constituent Fluxes Card
 *
 * This card contains the status of each constituent as active (ON) or inactive (OFF).
 * There is one line per constituent, with a value for each waterbody.
 */
public class ActiveConstituentFluxesCard extends W2Card {

    public ActiveConstituentFluxesCard(W2ControlFile w2ControlFile, int numFluxes, int numWaterBodies) {
        super(w2ControlFile,"CST FLUX", numFluxes, W2Globals.constants(numFluxes, numWaterBodies),
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
     * Return list of constituent fluxes states
     * @return List of constituent fluxes states
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
     * Set constituent fluxes states
     * @param states Constituent fluxes states
     */
    public void setStates(List<List<String>> states) {
        this.recordValuesList = states;
    }

    @Override
    public void updateRecordValuesList() {
        // Not needed for this class
    }

}
