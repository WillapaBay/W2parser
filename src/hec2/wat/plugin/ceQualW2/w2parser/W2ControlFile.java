package hec2.wat.plugin.ceQualW2.w2parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for the contents of a CE-QUAL-W2 control file
 */
public final class W2ControlFile {
    private final String w2ControlFilename;
    private final List<String> w2ControlList = new ArrayList<>();
    private final Path directoryPath;
    private final Path w2ControlInPath;
    private final File graphFile;

    public W2ControlFile(String infile) throws IOException {

        w2ControlFilename = infile;
        w2ControlInPath = Paths.get(infile).toAbsolutePath();
        directoryPath = w2ControlInPath.getParent();
        graphFile = new File(directoryPath.toString(), "graph.npt");
        load();
        handleCardNamesThatVary();
    }

    Path getDirectoryPath() { return directoryPath; }

    String getGraphFilename() { return graphFile.toString(); }

    String getLine(int i) {
        return w2ControlList.get(i);
    }

    void setLine(int i, String line) {
        w2ControlList.set(i, line);
    }

    public int size() {
        return w2ControlList.size();
    }

    private void load() throws IOException {
        w2ControlList.clear();
        w2ControlList.addAll(Files.readAllLines(w2ControlInPath));
    }

    /**
     * Some card names may vary from one control file to another
     * Replace these with a specified card name
     */
    private void handleCardNamesThatVary() {
        for (int i = 0; i < w2ControlList.size(); i++) {
            String line = w2ControlList.get(i).toUpperCase();
            // Handle TSR Layer / TSR Depth card
            if (line.startsWith("TSR") && line.contains("ETSR")) {
                String newLine = "TSR LAYE" + line.substring(8,line.length());
                w2ControlList.set(i, newLine);
            }
        }
    }

    /**
     * Save w2ControlList to w2_con.npt file. This may be a new file, or it may replace the existing file.
     * @param outFilename w2_con.npt output filename
     */
    public void save(String outFilename) {
        Path w2ControlOutPath = Paths.get(outFilename).toAbsolutePath();
        try {
            Files.write(w2ControlOutPath, w2ControlList, Charset.forName("UTF-8"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Expand card in w2ControlList to accommodate a larger W2Card
     * @param cardHeaderLineNumber Line number of card header in w2ControlList
     * @param numDataLinesInCard Number of data lines of card in w2ControlList
     * @param numLinesToAdd Number of lines to add
     */
    public void expandCard(int cardHeaderLineNumber, int numDataLinesInCard, int numLinesToAdd) {
        int indexToAdd = cardHeaderLineNumber + numDataLinesInCard + 1;
        for (int i = 0; i < numLinesToAdd; i++) {
            w2ControlList.add(indexToAdd, "");
        }
    }

    /**
     * Shrink card in w2ControlList to adjust for a smaller W2Card
     * @param cardHeaderLineNumber Line number of card header in w2ControlList
     * @param numDataLinesInCard Number of data lines of card in w2ControlList
     * @param numLinesToRemove Number of lines to remove
     */
    public void shrinkCard(int cardHeaderLineNumber, int numDataLinesInCard, int numLinesToRemove) {
        int indexToRemove = cardHeaderLineNumber + numDataLinesInCard;
        for (int i = 0; i < numLinesToRemove; i++) {
            w2ControlList.remove(indexToRemove);
            indexToRemove--;
        }
    }
}
