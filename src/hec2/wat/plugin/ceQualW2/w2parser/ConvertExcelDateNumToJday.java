package hec2.wat.plugin.ceQualW2.w2parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ConvertExcelDateNumToJday {
    public static final int numHeaderLines = 3;
    private int referenceYear;

    public ConvertExcelDateNumToJday(String folder, int referenceYear) throws IOException {
        this.referenceYear = referenceYear;
        String w2ControlFileName = String.format("data/%s/w2_con.npt", folder);
        Path w2ControlFilePath = Paths.get(w2ControlFileName).toAbsolutePath();
        Path directoryPath = w2ControlFilePath.getParent();

        W2ControlFile w2con = new W2ControlFile(w2ControlFileName);
        W2Parser w2Parser = new W2Parser(w2con);
        w2Parser.readControlFile();

        // Create list of input files
        HashSet<String> inputFileNameSet = new HashSet();
        List<W2Parameter> inputW2Parameters = w2Parser.getInputW2Parameters();
        List<W2Parameter> metW2Parameters = w2Parser.getMeteorologyW2Parameters();
        for (W2Parameter param : inputW2Parameters) {
            inputFileNameSet.add(param.getFileName());
        }
        for (W2Parameter param : metW2Parameters) {
            inputFileNameSet.add(param.getFileName());
        }

        List<String> inputFileNames = new ArrayList<>();
        List<String> outputFileNames = new ArrayList<>();
        inputFileNames.addAll(inputFileNameSet);
        inputFileNames.forEach(item -> {
            outputFileNames.add("jday_" + item);
        });

        for (int i = 0; i < inputFileNames.size(); i++) {
            Path inputFilePath = Paths.get(directoryPath.toString() + "/" + inputFileNames.get(i));
            Path outputFilePath = Paths.get(directoryPath.toString() + "/" + outputFileNames.get(i));
            convert(inputFilePath, outputFilePath, referenceYear);
        }

    }

    public void convert(Path inputFilePath, Path outputFilePath, int referenceYear) throws IOException {
        boolean isNewFormat;
        List<String> lines = Files.readAllLines(inputFilePath);
        List<String> header = new ArrayList<>();

        if (lines.get(0).startsWith("$")) {
            isNewFormat = true;
        } else {
            isNewFormat = false;
        }

        // Get header
        for (int i = 0; i < numHeaderLines; i++) {
            header.add(lines.get(i));
        }

        // Get data
        for (int i = numHeaderLines; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (isNewFormat) {
                // Parse as a comma-delimited file

                // First, remove trailing commas
                while (line.endsWith("<")) {
                    line = line.substring(0, line.length() - 1);
                }
                line = line.trim();

                List<String> fields = Arrays.asList(line.split("\\s*,\\s*"));
                double excelDateNum = Double.parseDouble(fields.get(0));
            }
        }



    }

}
