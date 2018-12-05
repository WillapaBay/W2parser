package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

public abstract class W2Card {
    W2ControlFile w2ControlFile;
    private String cardName;
    private String titleLine;
    // Number of record lines. For most cards, this equals one.
    // For file cards, this is the number of branches or water bodies.
    int numCardDataLines; // The current number of lines in the W2Card
    int numCardDataLinesInFile; // The number of lines in the card in the w2_con.npt file
    // Table from card (list of lines of text)
    List<String> table = new ArrayList<>();

    public W2Card(W2ControlFile w2ControlFile, String cardName, int numCardDataLines) {
        this.w2ControlFile = w2ControlFile;
        this.cardName = cardName;
        this.numCardDataLines = numCardDataLines;
        this.numCardDataLinesInFile = numCardDataLines;
        fetchTable();
    }

    public W2Card(W2ControlFile w2ControlFile, int numCardDataLines) {
        this.w2ControlFile = w2ControlFile;
        this.numCardDataLines = numCardDataLines;
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
     * Retrieve card text from the W2ControlFile list
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
                for (int j = 0; j < numCardDataLines; j++) {
                    line = w2ControlFile.getLine(i + j + 1);
                    table.add(line);
                }
                break;
            }
        }
    }

    /**
     * Update card text in the W2ControlFile list
     */
    public void updateTable() {
        updateText();

        String line;
        for (int i = 0; i < w2ControlFile.size(); i++) {
            line = w2ControlFile.getLine(i).toUpperCase();
            if (line.startsWith(cardName)) {
                // Resize card in w2ControlFileList if necessary
                int difference = numCardDataLines - numCardDataLinesInFile;
                if (difference > 0) {
                    w2ControlFile.expandCard(i, numCardDataLinesInFile, difference);
                } else if (difference < 0) {
                    w2ControlFile.shrinkCard(i, numCardDataLinesInFile, Math.abs(difference));
                }
                w2ControlFile.setLine(i, this.titleLine);
                for (int j = 0; j < numCardDataLines; j++) {
                    w2ControlFile.setLine(i + j + 1, table.get(j));
                }
            }
        }
        numCardDataLinesInFile = numCardDataLines;
    }

    /**
     * Return text of card
     * @return Text of card
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(titleLine + "\n");
        for (String line : table) {
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
        List<String> fields = new ArrayList<>();
        for (int j = (startField - 1); j < endField; j++) {
            int start = Math.min(j * fieldWidth, line.length());
            int end = Math.min(j * fieldWidth + fieldWidth, line.length());
            String field = line.substring(start, end);
            if (!field.equals(""))
                fields.add(field.trim());
        }
        return fields;
    }

    /**
     * Parse a multi-line record. A record consists of all fields for a waterbody,
     * branch, constituent, etc.
     * @param table List of record lines from control file
     * @param recordIndex Index of record (zero-based)
     * @param numFields Number of fields
     * @return List of record values. The first item is the record identifier.
     */
    public List<String> parseRecord(List<String> table, int recordIndex, int numFields) {
        int numLinesPerRecord = (int) Math.ceil(numFields/9.0);
        int start = numLinesPerRecord * recordIndex;
        int end = start + numLinesPerRecord;
        int fieldWidth = 8;
        int startField = 1;
        int endField = 10;
        List<String> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            List<String> fields = parseLine(table.get(i), fieldWidth, startField, endField);
            values.addAll(fields);
            // After the first record line is read, skip the first field of subsequent lines
            startField = 2;
        }
        return values;
    }

    /**
     * Return card name
     * @return card name
     */
    public String getCardName() {
        return cardName;
    }

    /**
     * Return current number of data lines in W2Card
     * @return Current number of data lines in W2Card
     */
    public int getNumCardDataLines() {
        return numCardDataLines;
    }

    /**
     * Return number of data lines in card in the W2ControlFile list
     * @return Number of data lines in card in the W2ControlFile list
     */
    public int getNumCardDataLinesInFile() {
        return numCardDataLinesInFile;
    }

    /**
     * Return the card data table
     * @return W2Card data table
     */
    public List<String> getTable() {
        return table;
    }

}
