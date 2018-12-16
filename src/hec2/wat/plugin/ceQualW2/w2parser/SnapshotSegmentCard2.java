package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.List;

public class SnapshotSegmentCard2 extends W2Card2 {

    private List<String> identifiers;
    private List<List<String>> segments;

    /**
     * Primary constructor
     *
     * @param w2ControlFile   CE-QUAL-W2 control file object
     * @param numRecords      Number of records in card
     * @param numFieldsList   List of the number of fields in each record
     * @param valueFieldWidth
     */
    public SnapshotSegmentCard2(W2ControlFile w2ControlFile, int numRecords, List<Integer> numFieldsList, int valueFieldWidth) {
        super(w2ControlFile, "SNP SEG", numRecords, numFieldsList, valueFieldWidth);
        parseTable();
        identifiers = this.recordIdentifiers;
        segments = this.recordValues;
    }
}
