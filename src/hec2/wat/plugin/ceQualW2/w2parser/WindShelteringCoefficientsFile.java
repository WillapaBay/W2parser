package hec2.wat.plugin.ceQualW2.w2parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class WindShelteringCoefficientsFile {
    private String inputFileName;
    private int numSegments;

    public WindShelteringCoefficientsFile(String inputFileName, int numSegments) throws IOException {
        this.inputFileName = inputFileName;
        this.numSegments = numSegments;
        List<String> lines = Files.readAllLines(Paths.get(inputFileName));
        List<String> header = lines.subList(0, 3);
        List<String> data = lines.subList(3, lines.size());
        if (header.get(0).startsWith("$")) {
            parseFixedWidth(data);
        } else {
            parseCSV(data);
        }
    }

    public void parseFixedWidth(List<String> data) {
        // TODO
        int numLinesPerRecord = (int) Math.ceil(numSegments/9);
        int numRecords = data.size() / numLinesPerRecord;
    }

    public void parseCSV(List<String> data) {
        // TODO
    }

}
