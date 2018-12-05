package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestW2FileCard {

    @Test
    public void testReadWriteInfiles() throws IOException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        String outfile = "results/ColumbiaSlough/" +
                "w2_con.npt.testReadInfiles";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        int numWaterbodies = gridCard.getNumWaterBodies();
        W2FileCard qinCard = new W2FileCard(w2con, W2CardNames.BranchInflowFilenames, numBranches);
        W2FileCard tinCard = new W2FileCard(w2con, W2CardNames.BranchInflowTemperatureFilenames, numBranches);
        W2FileCard cinCard = new W2FileCard(w2con, W2CardNames.BranchInflowConcentrationFilenames, numBranches);
        W2FileCard bthCard = new W2FileCard(w2con, W2CardNames.BathymetryFilenames, numWaterbodies);
        System.out.println(qinCard);
        System.out.println(tinCard);
        System.out.println(cinCard);
        System.out.println(bthCard);
        System.out.println(qinCard.getFileNames());
        System.out.println(tinCard.getFileNames());
        System.out.println(cinCard.getFileNames());
        System.out.println(bthCard.getFileNames());
        qinCard.setFileName(0, "QinFile1");
        qinCard.setFileName(1, "QinFile2");
        qinCard.updateTable();
        tinCard.setFileName(0, "TinFile1");
        tinCard.setFileName(1, "TinFile2");
        tinCard.updateTable();
        cinCard.setFileName(0, "CinFile1");
        cinCard.setFileName(1, "CinFile2");
        cinCard.updateTable();
        bthCard.setFileName(0, "BthFile1");
        bthCard.updateTable();
        w2con.save(outfile);
    }
}
