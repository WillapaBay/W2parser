package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic File Card
 */
public class FileCard extends Card {
    private List<String> identifiers; // Water body or branch names
    private List<String> fileNames;
    private MultiLineCard mCard;

    public FileCard(W2ControlFile w2ControlFile, String cardName, int numRecordLines) {
        super(w2ControlFile, cardName, numRecordLines);
        parseTable();
    }


    public List<String> getFileNames() {
        return fileNames;
    }

    public void clearFileNames() {
        identifiers.clear();
        fileNames.clear();
    }

    public void addFilename(String fileName, String identifier) {
        identifiers.add(identifier);
        fileNames.add(fileName);
    }

    public String getFileName(int i) {
        return fileNames.get(i);
    }

    public void setFileName(int i, String file) {
        fileNames.set(i, file);
        updateText();
    }

    @Override
    public void parseTable() {
        String[] records = new String[2];
        String line;
        identifiers = new ArrayList<>();
        fileNames = new ArrayList<>();
        for (int i = 0; i < numCardDataLines; i++) {
            line = table.get(i);
            records[0] = line.substring(0, 7);
            records[1] = line.substring(8);
            identifiers.add(records[0].trim());
            fileNames.add(records[1].trim());
        }
    }

    @Override
    public void updateText() {
        table.clear();
        String str;
        for (int i = 0; i < numCardDataLines; i++) {
            str = String.format("%-8s%s", identifiers.get(i), fileNames.get(i));
            table.add(str);
        }
    }
}
