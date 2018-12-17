package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Withdrawal Output W2Card_OLD
 */
public class WithdrawalOutputCard extends W2Card {
    private String WDOC;
    private int NWDO;
    private int NIWDO;

    public WithdrawalOutputCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "WITH OUT", 1,
                W2Globals.constants(1, 3), 8,
                false);
        parseTable();
        init();
    }

    public void init() {
        List<String> values = recordValuesList.get(0);
        WDOC  = values.get(0);
        NWDO  = Integer.valueOf(values.get(1));
        NIWDO = Integer.valueOf(values.get(2));
    }

    public String getWDOC() {
        return WDOC;
    }

    public void setWDOC(String WDOC) {
        this.WDOC = WDOC;
        updateRecordValuesList();
    }

    public int getNWDO() {
        return NWDO;
    }

    public void setNWDO(int NWDO) {
        this.NWDO = NWDO;
        updateRecordValuesList();
    }

    public int getNIWDO() {
        return NIWDO;
    }

    public void setNIWDO(int NIWDO) {
        this.NIWDO = NIWDO;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList.clear();
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(WDOC));
        values.add(String.valueOf(NWDO));
        values.add(String.valueOf(NIWDO));
        recordValuesList.add(values);
    }
}
