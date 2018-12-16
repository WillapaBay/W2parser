package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

/**
 * W2Constituent Dimensions W2Card_OLD
 */
public class ConstituentDimensionsCard extends W2Card_OLD {
    private int NGC;  // Number of generic constituents
    private int NSS;  // Number of inorganic suspended solids
    private int NAL;  // Number of algal groups
    private int NEP;  // Number of epiphyton/periphyton groups
    private int NBOD; // Number of CBOD groups
    private int NMC;  // Number of macrophyte groups
    private int NZP;  // Number of zooplankton groups

    public ConstituentDimensionsCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "CONSTITU", 1);
        parseTable();
    }

    public int getNGC() {
        return NGC;
    }

    public void setNGC(int ngc) {
        this.NGC = ngc;
        updateText();
    }

    public int getNSS() {
        return NSS;
    }

    public void setNSS(int nss) {
        this.NSS = nss;
        updateText();
    }

    public int getNAL() {
        return NAL;
    }

    public void setNAL(int nal) {
        this.NAL = nal;
        updateText();
    }

    public int getNEP() {
        return NEP;
    }

    public void setNEP(int nep) {
        this.NEP = nep;
        updateText();
    }

    public int getNBOD() {
        return NBOD;
    }

    public void setNBOD(int nbod) {
        this.NBOD = nbod;
        updateText();
    }

    public int getNMC() {
        return NMC;
    }

    public void setNMC(int nmc) {
        this.NMC = nmc;
        updateText();
    }

    public int getNZP() {
        return NZP;
    }

    public void setNZP(int nzp) {
        this.NZP = nzp;
        updateText();
    }

    @Override
    public void parseTable() {
        List<String> Fields = parseLine(table.get(0), 8, 2, 10);
        NGC = Integer.parseInt(Fields.get(0));
        NSS = Integer.parseInt(Fields.get(1));
        NAL = Integer.parseInt(Fields.get(2));
        NEP = Integer.parseInt(Fields.get(3));
        NBOD = Integer.parseInt(Fields.get(4));
        NMC = Integer.parseInt(Fields.get(5));
        NZP = Integer.parseInt(Fields.get(6));
    }

    @Override
    public void updateText() {
        String str = String.format("%8s%8d%8d%8d%8d%8d%8d%8d",
                "", NGC, NSS, NAL, NEP, NBOD, NMC, NZP);
        table.set(0, str);
    }
}


