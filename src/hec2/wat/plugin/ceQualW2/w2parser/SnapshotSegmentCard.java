package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshot Segment Card
 */
public class SnapshotSegmentCard extends W2Card_NEW {

    private List<List<Integer>> segmentsList;

    /**
     * Primary constructor
     *
     * @param w2ControlFile   CE-QUAL-W2 control file object
     * @param numRecords      Number of records in card
     * @param numFieldsList   List of the number of fields in each record
     * @param valueFieldWidth
     */
    public SnapshotSegmentCard(W2ControlFile w2ControlFile, int numRecords, List<Integer> numFieldsList, int valueFieldWidth) {
        super(w2ControlFile, "SNP SEG", numRecords, numFieldsList,
                valueFieldWidth, false);
        parseTable();
        init();
    }

    public void init() {
        segmentsList = new ArrayList<>();

        for (List<String> recordValues : recordValuesList) {
            List<Integer> segments = new ArrayList<>();
            for (String value : recordValues) {
                segments.add(Integer.parseInt(value));
            }
            segmentsList.add(segments);
        }
    }

    public List<List<Integer>> getSegmentsList() {
        return segmentsList;
    }

    public void setSegmentsList(List<List<Integer>> segmentsList) {
        this.segmentsList = segmentsList;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        // Convert all integers to unformatted strings
        // and store in a new copy of recordValuesList
        recordValuesList = new ArrayList<>();
        for (List<Integer> segments : segmentsList) {
            List<String> recordValues = new ArrayList<>();
            for (Integer segment : segments) {
                recordValues.add(String.valueOf(segment));
            }
            recordValuesList.add(recordValues);
        }
    }
}
