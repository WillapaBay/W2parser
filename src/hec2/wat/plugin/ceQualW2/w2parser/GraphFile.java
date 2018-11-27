package hec2.wat.plugin.ceQualW2.w2parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Graph File, to handle graph.npt
 */
public class GraphFile {
    private List<Constituent> constituents = new ArrayList<>();
    private List<Constituent> derivedConstituents = new ArrayList<>();

    public GraphFile(String infile) {
        File file = new File(infile);
        ArrayList<String> lines = new ArrayList<>();
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }

        boolean readCname = false;
        boolean readCDname = false;
        int columnNumber = 1;

        for (String line : lines) {
            if (line.trim().equals("")) {
                readCname = false;
                readCDname = false;
                columnNumber = 1;
            }

            if (readCname || readCDname) {
                String nameAndUnits = line.substring(0,43).trim();
                String[] records = nameAndUnits.trim().split(",");
                String longName = records[0].trim();
                String shortName = longName;
                String units = "";
                if (records.length > 1) units = records[1].trim();
                Constituent c = new Constituent(shortName, longName, units, columnNumber);
                if (readCname) {
                    constituents.add(c);
                }
                else if (readCDname) {
                    derivedConstituents.add(c);
                }
                columnNumber++;
            }

            if (line.toUpperCase().contains("CNAME")) readCname = true ;
            if (line.toUpperCase().contains("CDNAME")) readCDname = true ;

        }

    }

    public List<Constituent> getConstituents() {
        return constituents;
    }

    public List<Constituent> getDerivedConstituents() {
        return derivedConstituents;
    }

}
