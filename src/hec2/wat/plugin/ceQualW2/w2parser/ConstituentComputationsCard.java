package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Constituent Computations Card
 */
public class ConstituentComputationsCard extends W2Card {
    private String CCC;
    private String LIMC;
    private int CUF;

    public ConstituentComputationsCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, W2CardNames.ConstituentComputations, 1,
                W2Globals.constants(1, 3), 8, false);
        parseTable();
        init();
    }

    private void init() {
        CCC = recordValuesList.get(0).get(0);
        LIMC = recordValuesList.get(0).get(1);
        CUF = Integer.valueOf(recordValuesList.get(0).get(2));
    }

    public String getCCC() {
        return CCC;
    }

    public void setCCC(String ccc) {
        this.CCC = ccc;
        updateRecordValuesList();
    }

    public String getLIMC() {
        return LIMC;
    }

    public void setLIMC(String limc) {
        this.LIMC = limc;
        updateRecordValuesList();
    }

    public int getCUF() {
        return CUF;
    }

    public void setCUF(int cuf) {
        this.CUF = cuf;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(CCC);
        values.add(LIMC);
        values.add(String.valueOf(CUF));
        recordValuesList.add(values);
//        recordValuesList.add(valueToStringList(CCC));
//        recordValuesList.add(valueToStringList(LIMC));
//        recordValuesList.add(valueToStringList(CUF));
    }
}


