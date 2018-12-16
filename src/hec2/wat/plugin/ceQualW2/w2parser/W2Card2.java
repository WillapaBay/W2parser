package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

public abstract class W2Card2 {
    W2ControlFile w2ControlFile;
    private String cardName;
    private String titleLine;
    private int identifierFieldWidth = 8;
    private int valueFieldWidth;
    // Number of record lines. For most cards, this equals one.
    // For file cards, this is the number of branches or water bodies.
    private int numRecords;             // Number of records in the card
    private int numCardDataLines;       // The current number of lines in the W2Card
    private int numCardDataLinesInFile; // The number of lines in the card in the w2_con.npt file
    private List<Integer> numFieldsList;// Number of fields for each record
    private List<Integer> numLinesList; // Number of lines for each record
    private List<String> table;         // Table from card (list of lines of text)
    private List<List<String>> records; // Identifier and values for each record
    List<String> recordIdentifiers;     // Identifiers for each record
    List<List<String>> recordValuesList;// List of card values, as unformatted strings.
                                        // These are sorted by column (field) in the card, and
                                        // numeric fields can be parsed to numeric types.
    List<Integer> fieldWidths;          // Widths of all fields in a line, including the identifier

    /**
     * Primary constructor
     * @param w2ControlFile CE-QUAL-W2 control file object
     * @param cardName Name of current card
     * @param numRecords Number of records in card
     * @param numFieldsList List of the number of fields in each record
     */
    public W2Card2(W2ControlFile w2ControlFile, String cardName, int numRecords,
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
        recordValuesList = new ArrayList<>();
        table = new ArrayList<>();
        fieldWidths = new ArrayList<>();
        fieldWidths.add(identifierFieldWidth);
        for (int i = 0; i < 9; i++) {
            fieldWidths.add(valueFieldWidth);
        }

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
    public void updateW2ControlFileList() {
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
     * Compute number of lines per record
     * @param numFields Number of fields
     * @return Number of lines per record
     */
    private int numLinesPerRecord(int numFields) {
        return (int) Math.ceil(numFields/9.0);
    }

    /**
     * Parse the table (data) of a W2 control file card into a
     * jagged list of strings. These may then be converted to numeric types,
     * as needed, by the cards that inherit this class.
     */
    public void parseTable() {
        int lineIndex = 0;
        for (int jc = 0; jc < numRecords; jc++) {
            int numFields = numFieldsList.get(jc);

            List<String> values = parseRecordText(table, lineIndex, numLinesPerRecord(numFields));

            records.add(values);
            recordIdentifiers.add(values.get(0));
            recordValuesList.add(values.subList(1, values.size()));

            lineIndex += numLinesPerRecord(numFields);
        }
    }

    /**
     * Parse a line containing fields in fixed-width format
     * As of CE-QUAL-W2 version 4.1, the field width has always been eight characters
     * and each line contains 10 fields. Most cards leave the first field blank.
     *
     * @param line Line of text from file
     * @param startField First field to read (one-based)
     * @param endField Last field to read (one-based)
     * @return List of fields
     */
    public List<String> parseLine(String line, int startField, int endField) {
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
     * Parse a multi-line record. A record consists of all fields for a water body,
     * branch, constituent, etc.
     * @param table List of record lines from control file
     * @param startLine Index of line in table where record starts (zero-based)
     * @param numLinesPerRecord Number of lines in the current record
     * @return List of record values. The first item is the record identifier.
     */
    private List<String> parseRecordText(List<String> table, int startLine,
                                    int numLinesPerRecord) {
        int startField = 1;
        final int endField = 10;
        int endLine = startLine + numLinesPerRecord;
        List<String> values = new ArrayList<>();

        for (int i = startLine; i < endLine; i++) {
            List<String> fields = parseLine(table.get(i), startField, endField);
            values.addAll(fields);
            // After the first record line is read, skip the first field of subsequent lines
            startField = 2;
        }
        return values;
    }

    /**
     * Compose a multi-line record. A record consists of all fields for a water body,
     * branch, constituent, etc.
     * @param identifier Record identifier label
     * @param fields List of all fields of a multi-line record
     * @return List of record values. The first item is the record identifier.
     */
    private List<String> composeRecordText(String identifier, List<String> fields) {
        int startField = 1;
        final int endField = 10;
        List<String> recordText = new ArrayList<>();

        String identifierFormat = "-%" + fieldWidths.get(0) + "s";
        StringBuilder line = new StringBuilder(String.format(identifierFormat, identifier));
        int i = 1; // Starting with first field after the label

        for (String field : fields) {
            String fieldFormat = "%" + fieldWidths.get(i);
            line.append(String.format(fieldFormat, field));

            i++;
            if (i > 9) {
                // Append to record and prepare next line
                recordText.add(line.toString());
                i = 1;
                line = new StringBuilder(String.format(identifierFormat, ""));
            }
        }

        return recordText;
    }

    /**
     * Compose a line containing fields in fixed-width format.
     * This is the counterpart to the parseLine method.
     *
     * @param fields List of fields to write to a formatted string
     * @return Formatted string to write to a line of the W2 control file
     */
    public String composeLine(List<String> fields) {

        if (fields.size() != 10) {
            throw new IllegalArgumentException("Ten fields need to be provided.");
        }

        if (fieldWidths.size() != 10) {
            throw new IllegalArgumentException("Ten field widths need to be provided.");
        }

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            String format;
            if (i == 0) {
                format = "-%" + fieldWidths.get(i) + "s";
            } else {
                format = "%" + fieldWidths.get(i) + "s";
            }

            line.append(String.format(format, fields.get(i)));
        }

        return line.toString();
    }

    /**
     * Update the W2 control file text from the current variables
     */
    public void updateText() {
        table = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            List<String> record = records.get(i);
            String identifier = recordIdentifiers.get(i);
            List<String> recordText = composeRecordText(identifier, record);
            table.addAll(recordText);
        }
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

    public abstract void updateRecordValues();


}
