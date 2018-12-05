package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

/**
 * W2Constituent Computations W2Card
 */
public class ConstituentComputationsCard extends W2Card {
    private String CCC;
    private String LIMC;
    private int CUF;

    public ConstituentComputationsCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, W2CardNames.ConstituentComputations, 1);
        parseTable();
    }

    public String getCCC() {
        return CCC;
    }

    public void setCCC(String ccc) {
        this.CCC = ccc;
        updateText();
    }

    public String getLIMC() {
        return LIMC;
    }

    public void setLIMC(String limc) {
        this.LIMC = limc;
        updateText();
    }

    public int getCUF() {
        return CUF;
    }

    public void setCUF(int cuf) {
        this.CUF = cuf;
        updateText();
    }

    @Override
    public void parseTable() {
        List<String> Fields = parseLine(table.get(0), 8, 2, 10);
        CCC = Fields.get(0);
        LIMC = Fields.get(1);
        CUF = Integer.parseInt(Fields.get(2));
    }

    @Override
    public void updateText() {
        String str = String.format("%8s%8s%8s%8d",
                "", CCC, LIMC, CUF);
        table.set(0, str);
    }
}


