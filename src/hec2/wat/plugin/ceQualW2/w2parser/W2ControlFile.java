package hec2.wat.plugin.ceQualW2.w2parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

/**
 * Container for the contents of a CE-QUAL-W2 control file
 */
public class W2ControlFile {
    private String w2ControlFilename;
    private List<String> w2ControlList;
    private Path directoryPath;
    private Path w2ControlInPath;
    private File graphFile;

    public W2ControlFile(String infile) throws FileNotFoundException {

        w2ControlFilename = infile;
        w2ControlInPath = Paths.get(infile).toAbsolutePath();
        directoryPath = w2ControlInPath.getParent();
        graphFile = new File(directoryPath.toString(), "graph.npt");
        load(infile);
        handleCardNamesThatVary();
    }

    public Path getDirectoryPath() { return directoryPath; }

    public String getW2ControlFilename() {
        return w2ControlFilename;
    }

    public String getGraphFilename() { return graphFile.toString(); }

    public String getLine(int i) {
        return w2ControlList.get(i);
    }

    public void setLine(int i, String line) {
        w2ControlList.set(i, line);
    }

    public int size() {
        return w2ControlList.size();
    }

    public void load(String infile) throws FileNotFoundException {
        try {
            w2ControlList = Files.readAllLines(w2ControlInPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void save(String outfile) {
        Path w2ControlOutPath = Paths.get(outfile).toAbsolutePath();
        try {
            Files.write(w2ControlOutPath, w2ControlList, Charset.forName("UTF-8"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
