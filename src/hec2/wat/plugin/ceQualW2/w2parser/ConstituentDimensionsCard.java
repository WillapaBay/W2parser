package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Constituent Dimensions Card
 */
public class ConstituentDimensionsCard extends W2Card_NEW {
    private int NGC;  // Number of generic constituents
    private int NSS;  // Number of inorganic suspended solids
    private int NAL;  // Number of algal groups
    private int NEP;  // Number of epiphyton/periphyton groups
    private int NBOD; // Number of CBOD groups
    private int NMC;  // Number of macrophyte groups
    private int NZP;  // Number of zooplankton groups

    public ConstituentDimensionsCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "CONSTITU", 1,
                W2Globals.constants(1, 7),
                8, false);
        parseTable();
        init();
    }

    public void init() {
        List<String> values = recordValuesList.get(0);
        NGC  = Integer.valueOf(values.get(0));
        NSS  = Integer.valueOf(values.get(1));
        NAL  = Integer.valueOf(values.get(2));
        NEP  = Integer.valueOf(values.get(3));
        NBOD = Integer.valueOf(values.get(4));
        NMC  = Integer.valueOf(values.get(5));
        NZP  = Integer.valueOf(values.get(6));
    }

    public int getNGC() {
        return NGC;
    }

    public void setNGC(int ngc) {
        this.NGC = ngc;
        updateRecordValuesList();
    }

    public int getNSS() {
        return NSS;
    }

    public void setNSS(int nss) {
        this.NSS = nss;
        updateRecordValuesList();
    }

    public int getNAL() {
        return NAL;
    }

    public void setNAL(int nal) {
        this.NAL = nal;
        updateRecordValuesList();
    }

    public int getNEP() {
        return NEP;
    }

    public void setNEP(int nep) {
        this.NEP = nep;
        updateRecordValuesList();
    }

    public int getNBOD() {
        return NBOD;
    }

    public void setNBOD(int nbod) {
        this.NBOD = nbod;
        updateRecordValuesList();
    }

    public int getNMC() {
        return NMC;
    }

    public void setNMC(int nmc) {
        this.NMC = nmc;
        updateRecordValuesList();
    }

    public int getNZP() {
        return NZP;
    }

    public void setNZP(int nzp) {
        this.NZP = nzp;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(NGC));
        values.add(String.valueOf(NSS));
        values.add(String.valueOf(NAL));
        values.add(String.valueOf(NEP));
        values.add(String.valueOf(NBOD));
        values.add(String.valueOf(NMC));
        values.add(String.valueOf(NZP));
        recordValuesList.add(values);
    }
}
