package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class TestBathymetry {

    @Test
    public void testBathymetryNewSingleWaterBody()
            throws IOException {
        W2ControlFile w2con = new W2ControlFile("data/TDA/w2_con.npt");
        GridCard gridCard = new GridCard(w2con);
        int numLayers = gridCard.setNumLayers();
        W2BathymetryFile w2BathymetryFile =
                new W2BathymetryFile(w2con, "TDA_NAVD88_BTH_020918_2011.csv",
                        numLayers); // Uses path from w2con
        W2BathymetryRecord<Integer> Segments = w2BathymetryFile.getSegments();
        W2BathymetryRecord<Double> ELWS = new W2BathymetryRecord<>("ELWS");
        for (int i = 0; i < Segments.size(); i++) {
            ELWS.add((double) i);
        }
        w2BathymetryFile.setELWS(ELWS);
        String outfile = "results/TDA/bathymetry.csv.testBathymetry";
        w2BathymetryFile.save(outfile);
        System.out.println(w2BathymetryFile);
    }

    private W2BathymetryRecord<Double> scaleData(W2BathymetryRecord<Double> record, double scale,
                                                 double offset) {
        for (int i = 0; i < record.size(); i++) {
            double value = record.get(i) * scale + offset;
            record.set(i, value);
        }
        return record;
    }

    @Test
    public void testBathymetryLegacyMultipleWaterBodies()
            throws IOException {
        // Prepare water surface elevation data to write to all bathymetry files
//        List<Double> DLX = new ArrayList<>();
//        List<Double> ELWS = new ArrayList<>();
//        List<Double> PHIO = new ArrayList<>();
//        List<Double> FRICT = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            DLX.add(50.0 + i);
//            ELWS.add(200.0 + i);
//            PHIO.add(i/2.0);
//            FRICT.add(i/100.0);
//        }

        W2ControlFile w2con = new W2ControlFile("data/GCL/w2_con.npt");
        GridCard gridCard = new GridCard(w2con);
        int numWaterBodies = gridCard.getNumWaterBodies();
        int numLayers = gridCard.setNumLayers();
        W2FileCard bathymetryFileCard = new W2FileCard(w2con,
                "BTH FILE", numWaterBodies);
        List<String> fileNames = bathymetryFileCard.getFileNames();
        fileNames.forEach(filename ->
                {
                    try {
                        W2BathymetryFile w2BathymetryFile =
                                new W2BathymetryFile(w2con, filename, numLayers);

                        // Scale the original values as a tracer
                        W2BathymetryRecord<Double> DLX = w2BathymetryFile.getDLX();
                        W2BathymetryRecord<Double> ELWS = w2BathymetryFile.getELWS();
                        W2BathymetryRecord<Double> PHIO = w2BathymetryFile.getPHIO();
                        W2BathymetryRecord<Double> FRICT = w2BathymetryFile.getFRICT();
                        DLX = scaleData(DLX, 1.0, 100.0);
                        ELWS = scaleData(ELWS, 1.0, 100.0);
                        PHIO = scaleData(PHIO, 1.0, 1.0);
                        FRICT = scaleData(FRICT, 1.0, 1.0);
                        w2BathymetryFile.setDLX(DLX);
                        w2BathymetryFile.setELWS(ELWS);
                        w2BathymetryFile.setPHIO(PHIO);
                        w2BathymetryFile.setFRICT(FRICT);
                        String outfile = "results/GCL/" + filename + "_NEW_ELWS";
                        w2BathymetryFile.save(outfile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
