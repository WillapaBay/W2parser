package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

public class SnapshotSegmentCard2 extends W2Card2 {

    private List<String> identifiers;
    private List<List<Integer>> segmentsList;

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
        segmentsList = new ArrayList<>();

        for (List<String> recordValues : recordValuesList) {
            List<Integer> segments = new ArrayList<>();
            for (String value : recordValues) {
               segments.add(Integer.parseInt(value));
            }
            segmentsList.add(segments);
        }
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public List<List<Integer>> getSegmentsList() {
        return segmentsList;
    }

    public void setSegmentsList(List<List<Integer>> segmentsList) {
        this.segmentsList = segmentsList;
    }

    @Override
    public void updateRecordValues() {

    }
}
