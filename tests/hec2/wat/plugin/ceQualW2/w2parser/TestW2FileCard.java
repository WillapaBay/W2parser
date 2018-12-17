package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.IOException;

public class TestW2FileCard {

    public void unitTest(String folder) throws IOException {
        String infile = String.format("data/%s/w2_con.npt", folder);
        String outfile = String.format("results/%s/w2_con.npt.testReadInfiles", folder);
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
        qinCard.updateDataTable();
        tinCard.setFileName(0, "TinFile1");
        tinCard.updateDataTable();
        cinCard.setFileName(0, "CinFile1");
        cinCard.updateDataTable();
        bthCard.setFileName(0, "BthFile1");
        bthCard.updateDataTable();
        w2con.save(outfile);
    }

    @Test
    public void testParserBonneville() throws IOException {
        unitTest("BON");
    }

    @Test
    public void testParserChiefJoseph() throws IOException {
        unitTest("CHJ");
    }

    @Test
    public void testParserColumbiaSlough() throws IOException {
        unitTest("ColumbiaSlough");
    }

    @Test
    public void testParserDworshak() throws IOException {
        unitTest("DWR");
    }

    @Test
    public void testParserGrandCoulee() throws IOException {
        unitTest("GCL");
    }

    @Test
    public void testParserIceHarbor() throws IOException {
        unitTest("IHR");
    }

    @Test
    public void testParserJohnDay() throws IOException {
        unitTest("JDA");
    }

    @Test
    public void testParserLittleGoose() throws IOException {
        unitTest("LGS");
    }

    @Test
    public void testParserLowerMonumental() throws IOException {
        unitTest("LMN");
    }

    @Test
    public void testParserLowerGranite() throws IOException {
        unitTest("LWG");
    }

    @Test
    public void testParserMcNary() throws IOException {
        unitTest("MCN");
    }

    @Test
    public void testParserParticleTracking() throws IOException {
        unitTest("ParticleTracking");
    }

    @Test
    public void testParserSpokane() throws IOException {
        unitTest("Spokane");
    }

    @Test
    public void testParserTheDalles() throws IOException {
        unitTest("TDA");
    }
}
