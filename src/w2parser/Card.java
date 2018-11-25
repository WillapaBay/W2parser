package w2parser;

import java.util.ArrayList;
import java.util.List;

public abstract class Card {
    W2ControlFile w2ControlFile;
    private String cardName;
    private String titleLine;
    // Number of record lines. For most cards, this equals one.
    // For file cards, this is the number of branches or water bodies.
    int numLines;
    List<String> recordLines = new ArrayList<>();

    public Card(W2ControlFile w2ControlFile, String cardName, int numLines) {
        this.w2ControlFile = w2ControlFile;
        this.cardName = cardName;
        this.numLines = numLines;
        fetchTable();
    }

    public Card(W2ControlFile w2ControlFile, int numLines) {
        this.w2ControlFile = w2ControlFile;
        this.numLines = numLines;
        fetchTable();
        parseTable();
    }

    public void setTitleLine(String titleLine) {
        this.titleLine = titleLine;
    }

    public String getTitleLine() {
        return this.titleLine;
    }

    /**
     * Retrieve card text from the w2parser.W2ControlFile list
     */
    private void fetchTable() {
        String line;
        for (int i = 0; i < w2ControlFile.size(); i++) {
            // Only check 1st 8 characters
            String cardNameShort;
            if (cardName.length() > 8) {
                cardNameShort = cardName.substring(0,8);
            } else {
                cardNameShort = cardName;
            }

            line = w2ControlFile.getLine(i).toUpperCase();
            if (line.startsWith(cardNameShort)) {
                this.titleLine = w2ControlFile.getLine(i);
                for (int j = 0; j < numLines; j++) {
                    line = w2ControlFile.getLine(i + j + 1);
                    recordLines.add(line);
                }
                break;
            }
        }
    }

    /**
     * Update card text in the w2parser.W2ControlFile list
     */
    public void updateTable() {
        updateText();
        String line;
        for (int i = 0; i < w2ControlFile.size(); i++) {
            line = w2ControlFile.getLine(i).toUpperCase();
            if (line.startsWith(cardName)) {
                w2ControlFile.setLine(i, this.titleLine);
                for (int j = 0; j < numLines; j++) {
                    w2ControlFile.setLine(i + j + 1, recordLines.get(j));
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(titleLine + "\n");
        for (String line : recordLines) {
            str.append(line).append("\n");
        }
       return str.toString();
    }

    /**
     * Parse the W2 control file text to individual variables
     */
    public abstract void parseTable();

    /**
     * Update the W2 control file text from the current variables
     */
    public abstract void updateText();

    /**
     * Parse a line containing fields in fixed-width format
     * As of CE-QUAL-W2 version 4.1, the field width has always been eight characters
     * and have 10 fields. Most cards leave the first field blank.
     *
     * The most common usage is:
     * Fields = parseLine(line, 8, 2, 10)
     *
     * @param line Line of text from file
     * @param fieldWidth Field width in characters
     * @param startField First field to read (one-based)
     * @param endField Last field to read (one-based)
     * @return List of fields
     */
    public List<String> parseLine(String line, int fieldWidth, int startField, int endField) {
        List<String> Fields = new ArrayList<>();
        for (int j = (startField - 1); j < endField; j++) {
            int start = Math.min(j * fieldWidth, line.length());
            int end = Math.min(j * fieldWidth + fieldWidth, line.length());
            String field = line.substring(start, end);
            if (!field.equals(""))
                Fields.add(field.trim());
        }
        return Fields;
    }

    /**
     * Parse a multi-line record. A record consists of all fields for a waterbody,
     * branch, constituent, etc.
     * @param recordLines List of record lines from control file
     * @param recordIndex Index of record (zero-based)
     * @param numFields Number of fields
     * @return List of record values. The first item is the record identifier.
     */
    public List<String> parseRecord(List<String> recordLines, int recordIndex, int numFields) {
        int numLinesPerRecord = (int) Math.ceil(numFields/9.0);
        int start = numLinesPerRecord * recordIndex;
        int end = start + numLinesPerRecord;
        int fieldWidth = 8;
        int startField = 1;
        int endField = 10;
        List<String> Values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            List<String> Fields = parseLine(recordLines.get(i), fieldWidth, startField, endField);
            Values.addAll(Fields);
            // After the first record line is read, skip the first field of subsequent lines
            startField = 2;
        }
        return Values;
    }

}
