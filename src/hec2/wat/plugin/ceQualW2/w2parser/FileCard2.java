package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

class FileCard2 extends Card2 {
    private List<String> branches;
    private List<String> files;

    public FileCard2(W2ControlFile w2ControlFile, String cardName,
                     int numRecords, List<Integer> numFieldsList) {
        super(w2ControlFile, cardName, numRecords, numFieldsList, 50);
        parseTable();
        branches = this.recordIdentifiers;
        files = new ArrayList<>();
        this.recordValues.forEach(value -> files.add(value.get(0)));
    }

    @Override
    public void updateText() {}

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

}