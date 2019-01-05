package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tributary Segment Card
 */
public class TributarySegmentCard extends W2Card {

    private List<Integer> segments;

    /**
     * Primary constructor
     *
     * @param w2ControlFile CE-QUAL-W2 control file object
     * @param numSegments Number of segments
     * @param valueFieldWidth
     */
    public TributarySegmentCard(W2ControlFile w2ControlFile, int numSegments, int valueFieldWidth) {
        super(w2ControlFile, "TRIB SEG", 1, Arrays.asList(numSegments),
                valueFieldWidth, false);
        parseTable();
        init();
    }

    public void init() {
        segments = new ArrayList<>();
        if (recordValuesList.size() > 0) {
            for (String value : recordValuesList.get(0)) {
                segments.add(Integer.parseInt(value));
            }
        }
    }

    public List<Integer> getSegments() {
        return segments;
    }

    public void setSegments(List<Integer> segments) {
        this.segments= segments;
        updateRecordValuesList();
    }

    @Override
    public void updateRecordValuesList() {
        // Convert all integers to unformatted strings
        // and store in a new copy of recordValuesList
        List<String> recordValues = new ArrayList<>();
        for (Integer segment : segments) {
            recordValues.add(String.valueOf(segment));
        }

        recordValuesList.add(recordValues);
    }

}
