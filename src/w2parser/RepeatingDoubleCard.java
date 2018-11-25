package w2parser;

import java.util.ArrayList;
import java.util.List;

public class RepeatingDoubleCard extends Card {
    private List<Double> Data;
    private int numFields;
    private String format;

    public RepeatingDoubleCard(W2ControlFile w2ControlFile, String cardName, int numFields, String format) {
        super(w2ControlFile, cardName, (int) Math.ceil(numFields/9.0));
        this.numFields = numFields;
        this.format = format;
        this.Data = new ArrayList<>();
        parseTable();
    }

    public List<Double> getData() {
        return Data;
    }

    public void setData(List<Double> Data) {
        this.Data = Data;
        updateText();
    }

    public void clearData() {
        Data.clear();
    }

    public void addData(Double data) {
        this.Data.add(data);
        updateText();
    }


    @Override
    public void parseTable() {
        // numLines needs to be recomputed each time, as Data changes size
        int numLines = (int) Math.ceil(numFields/9.0);
        for (int i = 0; i < numLines; i++) {
            String line = recordLines.get(i);
            List<String> Fields = parseLine(line, 8, 2, 10);
            Fields.forEach(field -> {
                if (!field.equals(""))
                        Data.add(Double.parseDouble(field));}
            );
        }
    }

    @Override
    public void updateText() {
        StringBuilder str = new StringBuilder(String.format("%8s", ""));
        int line = 0;
        for (int i = 0; i < Data.size(); i++) {
            double data = Data.get(i);
            if ((i > 0 && i % 9 == 0)) {
                recordLines.set(line, str.toString());
                str = new StringBuilder(String.format("%8s", ""));
                line++;
            }

            str.append(String.format(format, data));

            if (i == (Data.size() - 1)) {
                recordLines.set(line, str.toString());
            }
        }
    }
}

