package hec2.wat.plugin.ceQualW2.w2parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class ConvertExcelDateNumToJday {
    public static final int numHeaderLines = 3;
    private int referenceYear;
    private TimeControlCard timeControlCard;

    public ConvertExcelDateNumToJday(String folderPath, int referenceYear) throws IOException {
        this.referenceYear = referenceYear;
        String w2ControlFileName = String.format("%s/%s", folderPath, "w2_con.npt");
        Path w2ControlFilePath = Paths.get(w2ControlFileName).toAbsolutePath();
        Path directoryPath = w2ControlFilePath.getParent();
        System.out.println("Processing " + w2ControlFilePath.toString());

        W2ControlFile w2con = new W2ControlFile(w2ControlFileName);
        W2Parser w2Parser = new W2Parser(w2con);
        w2Parser.readControlFile();

        // Convert time window in control file to Julian day convention
        timeControlCard = new TimeControlCard(w2con);
        JulianDate julianDateStart = excelDateNumToJulianDay(timeControlCard.getStartDay());
        JulianDate julianDateEnd = excelDateNumToJulianDay(timeControlCard.getEndDay());
        double excelDayStart = timeControlCard.getStartDay();
        double excelDayEnd = timeControlCard.getEndDay();
        double jdayStart = julianDateStart.jday;
        double jdayEnd = excelDayEnd - excelDayStart + jdayStart;
        timeControlCard.setStartDay(jdayStart);
        timeControlCard.setEndDay(jdayEnd);
        timeControlCard.setStartYear(julianDateStart.year);
        timeControlCard.updateDataTable();
        timeControlCard.updateW2ControlFileList();
        w2con.save(w2con.getW2ControlInPath().toString() + ".jday.npt");

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
            List<String> output = convert(inputFilePath, outputFilePath, referenceYear);
            Files.write(outputFilePath, output);
        }
    }

    public List<String> convert(Path inputFilePath, Path outputFilePath, int referenceYear) throws IOException {
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
        List<String> data = new ArrayList<>();
        boolean firstLine = true;
        double excelDateNum1 = 0.0;
        double jday1 = 0.0;
        double jday = 0.0;
        for (int i = numHeaderLines; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (isNewFormat) {
                // Parse as a comma-delimited file

                // First, remove trailing commas
                while (line.endsWith(",")) {
                    line = line.substring(0, line.length() - 1);
                }
                line = line.trim();

                List<String> fields = Arrays.asList(line.split("\\s*,\\s*"));
                double excelDateNum = Double.parseDouble(fields.get(0));

                if (firstLine) {
                    JulianDate julianDate = excelDateNumToJulianDay(excelDateNum);
                    excelDateNum1 = excelDateNum;
                    jday1 = julianDate.jday;
                    firstLine = false;
                }

                jday = excelDateNum - excelDateNum1 + jday1;

                StringBuilder outLine = new StringBuilder();
                fields.set(0, String.format("%.6f", jday));
                for (String field : fields) {
                    outLine.append(String.format("%s,", field));
                }
                outLine = new StringBuilder(outLine.substring(0, outLine.length() - 1));
                data.add(outLine.toString());
            } else {
                // Parse as fixed-width format
                line = line.trim();
                List<String> fields = parseLine(line, 1, 20, 8);
                double excelDateNum = Double.parseDouble(fields.get(0));

                if (firstLine) {
                    JulianDate julianDate = excelDateNumToJulianDay(excelDateNum);
                    excelDateNum1 = excelDateNum;
                    jday1 = julianDate.jday;
                    firstLine = false;
                }

                jday = excelDateNum - excelDateNum1 + jday1;

                StringBuilder outLine = new StringBuilder();
                fields.set(0, String.format("%8.3f", jday));
                for (String field : fields) {
                    outLine.append(String.format("%8s", field));
                }
                data.add(outLine.toString());
            }
        }

        List<String> output = new ArrayList<>();
        output.addAll(header);
        output.addAll(data);
        return output;
    }

    /**
     * Julian Date Container
     */
    class JulianDate {
        double jday; // Julian Day
        int year;    // Year

        public JulianDate(double jday, int year) {
            this.jday = jday;
            this.year = year;
        }
    }

    /**
     * Convert Excel date number to Java Date
     * The value from Microsoft Excel is the number of days since
     * the epoch reference of 1900-01-01 in UTC. Internally, the actual
     * reference date is December 30, 1899 as documented on this Wikipedia page:
     * https://en.wikipedia.org/wiki/Epoch_(reference_date)#Notable_epoch_dates_in_computing
     * @param excelDateNum Excel date number
     */
    public JulianDate excelDateNumToJulianDay(double excelDateNum) {
        // Compute the date-only value, without time-of-day and without time zone
        LocalDate javaDate = LocalDate.of(1899, Month.DECEMBER, 30).plusDays((long) excelDateNum);
        int year = javaDate.getYear();
        int dayOfYear = javaDate.getDayOfYear();
        double frac = excelDateNum - (int) excelDateNum;
        double jday = dayOfYear + frac;
        return new JulianDate(jday, year);
    }

    /**
     * Trim white space off the right end of a string
     * @param str String
     * @return Trimmed string
     */
    private String trimEnd(String str) {
        while (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * Parse a line of a W2 ASCII file
     * @param line Line of data
     * @param startField Start field on line
     * @param endField End field on line
     * @return
     */
    private List<String> parseLine(String line, int startField, int endField, int fieldWidth) {
        line = trimEnd(line);
        List<String> fields = new ArrayList<>();
        int start = 0;
        int end;
        int startCol = startField - 1;
        for (int col = 0; col < startCol; col++) {
            start += fieldWidth;
        }

        for (int col = (startField - 1); col < endField; col++) {
            end = Math.min(start + fieldWidth, line.length());
            String field = line.substring(start, end);
            if (!field.equals(""))
                fields.add(field.trim());
            start = Math.min(end, line.length());
        }
        return fields;
    }
}
