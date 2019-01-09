package hec2.wat.plugin.ceQualW2.w2parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Wind Sheltering Coefficients
 *
 * This class reads the wind sheltering coefficients file utilizing static methods in the W2Card class.
 * The file includes an arbitrary number of records. The first field of each record is the Julian Day.
 * The other fields are the wind sheltering coefficients for each cross section. The number of cross
 * sections is specified in the IMX field in the Grid card. Each wind sheltering coefficient is in
 * effect from the Julian day of the current record to the Julian day of the next record.
 */
public class WindShelteringCoefficientsFile {
    private String inputFileName;
    private int numSegments;
    private List<Double> julianDays;
    private List<List<Double>> wscList;
    private boolean csvFile;

    public WindShelteringCoefficientsFile(W2ControlFile w2con, String wscFilename, int numSegments) throws IOException {
        this.inputFileName = inputFileName;
        this.numSegments = numSegments;
        julianDays = new ArrayList<>();
        wscList = new ArrayList<>();

        Path wscPath = w2con.getDirectoryPath().resolve(wscFilename);
        List<String> lines = Files.readAllLines(wscPath);
        List<String> header = subListCopy(lines, 0, 2);
        List<String> data = subListCopy(lines, 3, lines.size() - 1);

        // Remove any blank lines from the end of the data
        Iterator<String> iter = data.iterator();
        while (iter.hasNext()) {
            String str = iter.next();
            if (str.trim().equals("")) {
                iter.remove();
            }
        }

        if (header.get(0).startsWith("$")) {
            csvFile = true;
            parseCSV(data);
        } else {
            csvFile = false;
            parseFixedWidth(data);
        }
    }

    public void parseFixedWidth(List<String> data) {
        int numLinesPerRecord = W2Card.numLinesPerRecord(numSegments);
        int numRecords = W2Card.numRecords(data.size(), numLinesPerRecord);
        List<Integer> fieldWidths = Arrays.asList(8, 8, 8, 8, 8, 8, 8, 8, 8, 8);
        int lineNum = 0;
        for (int i = 0; i < numRecords; i++) {
            List<String> record = W2Card.parseRecordText(data, lineNum, numLinesPerRecord, fieldWidths);
            julianDays.add(Double.valueOf(record.get(0)));
            List<Double> fields = new ArrayList<>();
            for (String field : record.subList(1, record.size())) {
                if (!field.trim().equals(""))
                    fields.add(Double.valueOf(field));
            }
            wscList.add(fields);
            lineNum += numLinesPerRecord;
        }
    }

    public void parseCSV(List<String> data) {
        // TODO
    }

    private List<String> subListCopy(List<String> input, int firstIndex, int lastIndex) {
        ArrayList<String> output = new ArrayList<>();
        for (int i = firstIndex; i <= lastIndex; i++) {
            output.add(input.get(i));
        }
        return output;
    }
}
