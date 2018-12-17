package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;


/**
 * Restart Card
 */
public class RestartCard extends W2Card {
    private String RSOC;
    private int NRSO;
    private String RSIC;

    public RestartCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "RESTART", 1,
                W2Globals.constants(1, 3), 8,
                false);
        parseTable();
        init();
    }

    public void init() {
        List<String> values = recordValuesList.get(0);
        RSOC = values.get(0);
        NRSO = Integer.valueOf(values.get(1));
        RSOC = values.get(2);
    }

    public String getRSOC() {
        return RSOC;
    }

    public void setRSOC(String RSOC) {
        this.RSOC = RSOC;
        updateRecordValuesList();
    }

    public int getNRSO() {
        return NRSO;
    }

    public void setNRSO(int NRSO) {
        this.NRSO = NRSO;
        updateRecordValuesList();
    }

    public String getRSIC() {
        return RSIC;
    }

    public void setRSIC(String RSIC) {
        this.RSIC = RSIC;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(RSOC));
        values.add(String.valueOf(NRSO));
        values.add(String.valueOf(RSIC));
        recordValuesList.add(values);
    }
}
