package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles cards containing a column of identifiers and a column of values
 * This is a more generalized version of the W2FileCard_OLD class and should replace it.
 */
public class ValuesCard extends W2Card_OLD {
    private List<String> identifiers; // Water body or branch names
    private List<String> values;

    public ValuesCard(W2ControlFile w2ControlFile, String cardName, int numRecordLines) {
        super(w2ControlFile, cardName, numRecordLines);
        parseTable();
    }

    public List<String> getValues() {
        return values;
    }

    public void clearIdentifiersAndValues() {
        identifiers.clear();
        values.clear();
        updateText();
    }

    public void addIdentifierAndValue(String identifier, String value) {
        identifiers.add(identifier);
        values.add(value);
        updateText();
    }

    public String getIdentifier(int i) {
        return identifiers.get(i);
    }

    public String getValue(int i) {
        return values.get(i);
    }

    public void setIdentifierAndValue(int i, String identifier, String value) {
        identifiers.set(i, identifier);
        values.set(i, value);
        updateText();
    }

    @Override
    public void parseTable() {
        String[] fields = new String[2];
        String line;
        identifiers = new ArrayList<>();
        values = new ArrayList<>();
        for (int i = 0; i < numCardDataLines; i++) {
            line = table.get(i).trim();
            fields[0] = line.substring(0, 7);
            fields[1] = line.substring(8);
            identifiers.add(fields[0].trim());
            values.add(fields[1].trim());
        }
    }

    @Override
    public void updateText() {
        table.clear();
        String str;
        for (int i = 0; i < numCardDataLines; i++) {
            str = String.format("%-8s%s", identifiers.get(i), values.get(i));
            table.add(str);
        }
    }

}
