package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles multiple line cards
 */
public class MultiLineCard extends W2Card_OLD {
    private List<List<String>> values;

    public MultiLineCard(W2ControlFile w2ControlFile, String cardName, int numRecordLines) {
        super(w2ControlFile, cardName, numRecordLines);
        parseTable();
    }

    public List<List<String>> getValues() {
        return values;
    }

    public void setValues(List<List<String>> values) {
        this.values = values;
        updateText();
    }

    public void addData(List<String> data) {
        values.add(data);
        updateText();
    }

    public String getValue(int row, int col) {
       return values.get(row).get(col);
    }

    public void setValue(int row, int col, String value) {
        List<String> line = values.get(row);
        line.set(col, value);
        values.set(row, line);
        updateText();
    }

    @Override
    public void parseTable() {
        values = new ArrayList<>();
        List<String> fields;
        for (int i = 0; i < numCardDataLines; i++) {
            fields = parseLine(table.get(i), 8, 1, 10);
            values.add(fields);
        }
    }

    @Override
    public void updateText() {
        table.clear();
        String line = "";
        for (int i = 0; i < numCardDataLines; i++) {
            for (int j = 0; i < values.size(); j++) {
                if (j == 0) {
                    line = String.format("%-8s", values.get(j));
                } else {
                    line += String.format("%8s", values.get(j));
                }
            }
            table.add(line);
        }
    }

}
