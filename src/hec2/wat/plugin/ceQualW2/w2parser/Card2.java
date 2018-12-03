package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

public abstract class Card2 {
    W2ControlFile w2ControlFile;
    String cardName;
    String titleLine;
    int identifierFieldWidth = 8;
    int valueFieldWidth;
    // Number of record lines. For most cards, this equals one.
    // For file cards, this is the number of branches or water bodies.
    int numRecords;              // Number of records in the card
    int numCardDataLines;        // The current number of lines in the Card
    int numCardDataLinesInFile;  // The number of lines in the card in the w2_con.npt file
    List<Integer> numFieldsList; // Number of fields for each record
    List<Integer> numLinesList;     // Number of lines for each record
    List<String> table;              // Table from card (list of lines of text)
    List<List<String>> records;      // Identifier and values for each record
    List<String> recordIdentifiers;  // Identifiers for each record
    List<List<String>> recordValues; // List of card values, string type.
                                             // These are sorted by column (field) in the card and
                                             // can be parsed later, as needed, to numeric types.

    /**
     * Primary constructor
     * @param w2ControlFile CE-QUAL-W2 control file object
     * @param cardName Name of current card
     * @param numRecords Number of records in card
     * @param numFieldsList List of the number of fields in each record
     */
    public Card2(W2ControlFile w2ControlFile, String cardName, int numRecords,
                 List<Integer> numFieldsList, int valueFieldWidth) {
        this.w2ControlFile = w2ControlFile;
        this.cardName = cardName;
        this.numRecords = numRecords;
        this.numFieldsList = numFieldsList;
        this.numLinesList = new ArrayList<>();
        this.numCardDataLines = 0;
        this.valueFieldWidth = valueFieldWidth;
        for (int numFields : numFieldsList) {
            int numDataLines = (int) Math.ceil(numFields/9.0);
            numLinesList.add(numDataLines);
            this.numCardDataLines += numDataLines;
        }
        this.numCardDataLinesInFile = this.numCardDataLines;

        records = new ArrayList<>();
        recordIdentifiers = new ArrayList<>();
        recordValues = new ArrayList<>();
        table = new ArrayList<>();

        fetchTable();
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
     * Parse the W2 control file text
     */
    public void parseTable() {

        for (int jc = 0; jc < numRecords; jc++) {
            int numFields = numFieldsList.get(jc);
            List<Integer> fieldWidths = new ArrayList<>();
            fieldWidths.add(identifierFieldWidth);
            for (int i = 0; i < 9; i++) {
                fieldWidths.add(valueFieldWidth);
            }
            List<String> values = parseRecord(table, jc, numFields, fieldWidths);
            records.add(values);
            recordIdentifiers.add(values.get(0));
            recordValues.add(values.subList(1, values.size()));
        }
    }

    /**
     * Update the W2 control file text from the current variables
     */
    public abstract void updateText();

    /**
     * Parse a line containing fields in fixed-width format
     * As of CE-QUAL-W2 version 4.1, the field width has always been eight characters
     * and each line contains 10 fields. Most cards leave the first field blank.
     *
     * @param line Line of text from file
     * @param fieldWidths Field widths of all ten fields, in characters
     * @param startField First field to read (one-based)
     * @param endField Last field to read (one-based)
     * @return List of fields
     */
    public List<String> parseLine(String line, List<Integer> fieldWidths,
                                  int startField, int endField) {
        if (fieldWidths.size() != 10) {
            throw new IllegalArgumentException("Ten field widths need to be provided.");
        }
        List<String> fields = new ArrayList<>();
        int start = 0;
        int end;
        int startCol = startField - 1;
        for (int col = 0; col < startCol; col++) {
            int fieldWidth = fieldWidths.get(col);
            start += fieldWidth;
        }

        for (int col = (startField - 1); col < endField; col++) {
            int fieldWidth = fieldWidths.get(col);
            end = Math.min(start + fieldWidth, line.length());
            String field = line.substring(start, end);
            if (!field.equals(""))
                fields.add(field.trim());
            start = Math.min(end, line.length());
        }
        return fields;
    }

    /**
     * Parse a line containing fields in fixed-width format
     * As of CE-QUAL-W2 version 4.1, the field width has always been eight characters
     * and each line contains 10 fields. Most cards leave the first field blank.
     *
     * This version uses the default eight character field width for all fields
     *
     * @param line Line of text from file
     * @param startField First field to read (one-based)
     * @param endField Last field to read (one-based)
     * @return List of fields
     */
    public List<String> parseLine(String line, int startField, int endField) {
        List<Integer> fieldWidths = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            fieldWidths.add(8);
        }
        return parseLine(line, fieldWidths, startField, endField);
    }

    /**
     * Parse a multi-line record. A record consists of all fields for a waterbody,
     * branch, constituent, etc.
     * @param table List of record lines from control file
     * @param recordIndex Index of record (zero-based)
     * @param numFields Number of fields
     * @return List of record values. The first item is the record identifier.
     */
    public List<String> parseRecord(List<String> table, int recordIndex, int numFields,
                                    List<Integer> fieldWidths) {
        int numLinesPerRecord = (int) Math.ceil(numFields/9.0);
        int start = numLinesPerRecord * recordIndex;
        int end = start + numLinesPerRecord;
        int startField = 1;
        int endField = 10;
        List<String> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            List<String> fields = parseLine(table.get(i), fieldWidths, startField, endField);
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
     * Return current number of data lines in Card
     * @return Current number of data lines in Card
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
     * @return Card data table
     */
    public List<String> getTable() {
        return table;
    }

}
