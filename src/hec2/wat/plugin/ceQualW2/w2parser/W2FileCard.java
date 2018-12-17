package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class W2FileCard extends W2Card_NEW {
    private List<String> branches;
    private List<String> fileNames;

    public W2FileCard(W2ControlFile w2ControlFile, String cardName,
                      int numRecords, List<Integer> numFieldsList) {
        super(w2ControlFile, cardName, numRecords, numFieldsList,
                50, true);
        parseTable();
        branches = this.recordIdentifiers;
        fileNames = new ArrayList<>();
        for (List<String> values : this.recordValuesList) {
            if (values.size() > 0) {
                fileNames.add(values.get(0));
            } else {
                fileNames.add("");
            }
        }
    }

    public List<Integer> computeNumFields() {
        return new ArrayList<Integer>();
    }

    public void clearFileNames() {
        this.fileNames = new ArrayList<>();
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
        this.recordIdentifiers = branches;
    }

    /**
     * Get list of filenames
     * @return List of filenames
     */
    public List<String> getFileNames() {
        return fileNames;
    }

    /**
     * Get filename
     * @param index Index of filename in list
     * @return filename
     */
    public String getFileNames(int index) {
        return fileNames.get(index);
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
        updateRecordValuesList();
    }

    public void setFileName(int index, String fileName) {
        this.fileNames.set(index, fileName);
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        recordValuesList = new ArrayList<>();
        for (String file : fileNames) {
           List<String> record = new ArrayList<>(Arrays.asList(file));
           recordValuesList.add(record);
        }
    }
}