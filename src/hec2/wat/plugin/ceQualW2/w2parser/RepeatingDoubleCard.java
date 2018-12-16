package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

public class RepeatingDoubleCard extends W2Card_OLD {
    private List<Double> values;
    private int numFields;
    private String format;

    public RepeatingDoubleCard(W2ControlFile w2ControlFile, String cardName, int numFields, String format) {
        super(w2ControlFile, cardName, (int) Math.ceil(numFields/9.0));
        this.numFields = numFields;
        this.format = format;
        this.values = new ArrayList<>();
        parseTable();
    }

    public List<Double> getValues() {
        return values;
    }

    public void setValues(List<Double> values) {
        this.values = values;
        updateText();
    }

    public void clearData() {
        values.clear();
    }

    public void addData(Double data) {
        this.values.add(data);
        updateText();
    }


    @Override
    public void parseTable() {
        // numCardDataLines needs to be recomputed each time, as values changes size
        int numLines = (int) Math.ceil(numFields/9.0);
        for (int i = 0; i < numLines; i++) {
            String line = table.get(i);
            List<String> fields = parseLine(line, 8, 2, 10);
            fields.forEach(field -> {
                if (!field.equals(""))
                        values.add(Double.parseDouble(field));}
            );
        }
    }

    @Override
    public void updateText() {
        table.clear();
        StringBuilder str = new StringBuilder(String.format("%8s", ""));
        int line = 0;
        for (int i = 0; i < values.size(); i++) {
            double data = values.get(i);
            if ((i > 0 && i % 9 == 0)) {
//                table.set(line, str.toString());
                table.add(str.toString());
                str = new StringBuilder(String.format("%8s", ""));
                line++;
            }

            str.append(String.format(format, data));

            if (i == (values.size() - 1)) {
//                table.set(line, str.toString());
                table.add(str.toString());
            }
        }
    }
}

