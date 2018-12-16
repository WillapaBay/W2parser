package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

/**
 * Inflow/Outflow W2Card_OLD
 */
public class InOutflowCard extends W2Card_OLD {
    private int NTR; // Number of tributaries
    private int NST; // Number of structures
    private int NIW; // Number of internal weirs
    private int NWD; // Number of withdrawals
    private int NGT; // Number of gates
    private int NSP; // Number of spillways
    private int NPI; // Number of pipes
    private int NPU; // Number of pumps

    public InOutflowCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "IN/OUTFL", 1);
        parseTable();
    }

    public int getNumTributaries() {
        return NTR;
    }

    public void setNumTributaries(int ntr) {
        this.NTR = ntr;
        updateText();
    }

    public int getNumStructures() {
        return NST;
    }

    public void setNumStructures(int nst) {
        this.NST = nst;
        updateText();
    }

    public int getNumInternalWeirs() {
        return NIW;
    }

    public void setNumInternalWeirs(int niw) {
        this.NIW = niw;
        updateText();
    }

    public int getNumWithdrawals() {
        return NWD;
    }

    public void setNumWithdrawals(int nwd) {
        this.NWD = nwd;
        updateText();
    }

    public int getNumGates() {
        return NGT;
    }

    public void setNumGates(int ngt) {
        this.NGT = ngt;
        updateText();
    }

    public int getNumSpillways() {
        return NSP;
    }

    public void setNumSpillways(int nsp) {
        this.NSP = nsp;
        updateText();
    }

    public int getNumPipes() {
        return NPI;
    }

    public void setNumPipes(int npi) {
        this.NPI = npi;
        updateText();
    }

    public int getNumPumps() {
        return NPU;
    }

    public void setNumPumps(int npu) {
        this.NPU = npu;
        updateText();
    }

    @Override
    public void parseTable() {
//        String[] records = table.get(0).trim().split("\\s+");
        List<String> Fields = parseLine(table.get(0), 8, 2, 10);
        NTR = Integer.parseInt(Fields.get(0));
        NST = Integer.parseInt(Fields.get(1));
        NIW = Integer.parseInt(Fields.get(2));
        NWD = Integer.parseInt(Fields.get(3));
        NGT = Integer.parseInt(Fields.get(4));
        NSP = Integer.parseInt(Fields.get(5));
        NPI = Integer.parseInt(Fields.get(6));
        NPU = Integer.parseInt(Fields.get(7));
    }

    @Override
    public void updateText() {
        String str = String.format("%8s%8d%8d%8d%8d%8d%8d%8d%8d",
                "", NTR, NST, NIW, NWD, NGT, NSP, NPI, NPU);
        table.set(0, str);
    }
}
