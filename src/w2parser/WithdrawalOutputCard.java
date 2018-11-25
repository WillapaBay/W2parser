package w2parser;

import java.util.List;

/**
 * Withdrawal Output Card
 */
public class WithdrawalOutputCard extends Card {
    private String WDOC;
    private int NWDO;
    private int NIWDO;
    private String identifier;

    public WithdrawalOutputCard(W2ControlFile w2ControlFile) {
        super(w2ControlFile, "WITH OUT", 1);
        parseTable();
    }

    public String getWDOC() {
        return WDOC;
    }

    public void setWDOC(String WDOC) {
        this.WDOC = WDOC;
        updateText();
    }

    public int getNWDO() {
        return NWDO;
    }

    public void setNWDO(int NWDO) {
        this.NWDO = NWDO;
        updateText();
    }

    public int getNIWDO() {
        return NIWDO;
    }

    public void setNIWDO(int NIWDO) {
        this.NIWDO = NIWDO;
        updateText();
    }

    @Override
    public void parseTable() {
        List<String> fields = parseLine(table.get(0), 8, 1, 10);
        identifier = fields.get(0);
        WDOC = fields.get(1);
        NWDO = Integer.parseInt(fields.get(2));
        NIWDO = Integer.parseInt(fields.get(3));
    }

    @Override
    public void updateText() {
        String str = String.format("%-8s%8s%8d%8d",
                identifier, WDOC, NWDO, NIWDO);
        table.set(0, str);
    }
}


