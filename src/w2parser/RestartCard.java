package w2parser;

import java.util.List;

public class RestartCard extends Card {
    private String RSOC;
    private int NRSO;
    private String RSIC;

    public RestartCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "RESTART", 1);
        parseTable();
    }

    public String getRSOC() {
        return RSOC;
    }

    public void setRSOC(String RSOC) {
        this.RSOC = RSOC;
        updateText();
    }

    public int getNRSO() {
        return NRSO;
    }

    public void setNRSO(int NRSO) {
        this.NRSO = NRSO;
        updateText();
    }

    public String getRSIC() {
        return RSIC;
    }

    public void setRSIC(String RSIC) {
        this.RSIC = RSIC;
        updateText();
    }

    @Override
    public void parseTable() {
        List<String> fields = parseLine(table.get(0), 8, 2, 10);
        RSOC = fields.get(0);
        NRSO = Integer.parseInt(fields.get(1));
        RSIC = fields.get(2);
    }

    @Override
    public void updateText() {
        String str = String.format("%8s%8s%8d%8s", "",
                RSOC, NRSO, RSIC);
        table.set(0, str);
    }
}

