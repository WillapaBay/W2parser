package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Record Repeating String Card
 *
 * This card is used to read cards containing one or more records that span multiple lines each.
 */
public class MultiRecordRepeatingStringCard extends W2Card_OLD {
    private List<String> names; // W2Constituent names
    private List<List<String>> values; // Specifies which constituents are on or off (values: ON or OFF)
    private int numFields; // Total number of fields -- spread over multiple lines, e.g., number of branches
    private int numRecords; // e.g. number of constituents
    private int numLinesPerRecord;
    private int numLinesPerCard;

    public MultiRecordRepeatingStringCard(W2ControlFile w2ControlFile, String cardName,
                                          int numRecords, int numFields) {
        super(w2ControlFile, cardName, ((int) (numRecords * Math.ceil(numFields/9.0))));
        this.numRecords = numRecords;
        this.numFields = numFields;
        this.numLinesPerRecord = (int) Math.ceil(numFields/9.0);
        this.numLinesPerCard = numLinesPerRecord * numRecords;
        parseTable();
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
        updateText();
    }

    public List<List<String>> getValues() {
        return values;
    }

    public void setValues(List<List<String>> values) {
        this.values = values;
        updateText();
    }

    @Override
    public void parseTable() {
        names = new ArrayList<>();
        values = new ArrayList<>();

        int lineNum = 0;
        for (int jr = 0; jr < numRecords; jr++) {
            List<String> recordData = new ArrayList<>();
            for (int jf = 0; jf < numLinesPerRecord; jf++) {
                List<String> fields = parseLine(table.get(lineNum), 8, 1, 10);
                for (int j = 1; j < fields.size(); j++) {
                    recordData.add(fields.get(j));
                }
                if (jf == 0) names.add(fields.get(0));
                lineNum++;
            }
            values.add(recordData);
        }
    }

    @Override
    public void updateText() {
        // TODO Not implemented
    }
}
