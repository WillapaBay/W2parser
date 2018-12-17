package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Inflow/Outflow Card
 */
public class InflowOutflowCard extends W2Card {
    private int NTR; // Number of tributaries
    private int NST; // Number of structures
    private int NIW; // Number of internal weirs
    private int NWD; // Number of withdrawals
    private int NGT; // Number of gates
    private int NSP; // Number of spillways
    private int NPI; // Number of pipes
    private int NPU; // Number of pumps

    public InflowOutflowCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "IN/OUTFL", 1,
                W2Globals.constants(1, 8), 8, false);
        parseTable();
        init();
    }

    public void init() {
        List<String> values = recordValuesList.get(0);
        NTR    = Integer.valueOf(values.get(0));
        NST    = Integer.valueOf(values.get(1));
        NIW    = Integer.valueOf(values.get(2));
        NWD    = Integer.valueOf(values.get(3));
        NGT    = Integer.valueOf(values.get(4));
        NSP    = Integer.valueOf(values.get(5));
        NPI    = Integer.valueOf(values.get(6));
        NPU    = Integer.valueOf(values.get(7));
    }

    public int getNumTributaries() {
        return NTR;
    }

    public void setNumTributaries(int ntr) {
        this.NTR = ntr;
        updateRecordValuesList();
    }

    public int getNumStructures() {
        return NST;
    }

    public void setNumStructures(int nst) {
        this.NST = nst;
        updateRecordValuesList();
    }

    public int getNumInternalWeirs() {
        return NIW;
    }

    public void setNumInternalWeirs(int niw) {
        this.NIW = niw;
        updateRecordValuesList();
    }

    public int getNumWithdrawals() {
        return NWD;
    }

    public void setNumWithdrawals(int nwd) {
        this.NWD = nwd;
        updateRecordValuesList();
    }

    public int getNumGates() {
        return NGT;
    }

    public void setNumGates(int ngt) {
        this.NGT = ngt;
        updateRecordValuesList();
    }

    public int getNumSpillways() {
        return NSP;
    }

    public void setNumSpillways(int nsp) {
        this.NSP = nsp;
        updateRecordValuesList();
    }

    public int getNumPipes() {
        return NPI;
    }

    public void setNumPipes(int npi) {
        this.NPI = npi;
        updateRecordValuesList();
    }

    public int getNumPumps() {
        return NPU;
    }

    public void setNumPumps(int npu) {
        this.NPU = npu;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(NTR));
        values.add(String.valueOf(NST));
        values.add(String.valueOf(NIW));
        values.add(String.valueOf(NWD));
        values.add(String.valueOf(NGT));
        values.add(String.valueOf(NSP));
        values.add(String.valueOf(NPI));
        values.add(String.valueOf(NPU));


        recordValuesList.add(values);

    }
}
