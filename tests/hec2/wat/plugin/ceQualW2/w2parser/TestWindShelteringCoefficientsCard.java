package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;

public class TestWindShelteringCoefficientsCard {

    public void unitTest(String folder, String wscFilename) throws IOException {
        String infile = String.format("data/%s/w2_con.npt", folder);
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        WindShelteringCoefficientsFile wscFile = new WindShelteringCoefficientsFile(w2con, wscFilename, gridCard.getNumCrossSections());
    }

    @Test
    public void testParserBonneville() throws IOException {
        unitTest("BON", "BON_WSC.npt");
    }

    @Test
    public void testParserChiefJoseph() throws IOException {
        unitTest("CHJ", "CHJ_WSC.npt");
    }

    @Test
    public void testParserColumbiaSlough() throws IOException {
        unitTest("ColumbiaSlough", "wsc.npt");
    }

    @Test
    public void testParserDworshak() throws IOException {
        unitTest("DWR", "DWR_WSC.npt");
    }

    @Test
    public void testParserGrandCoulee() throws IOException {
        unitTest("GCL", "wsc.csv");
    }

    @Test
    public void testParserIceHarbor() throws IOException {
        unitTest("IHR", "IHR_WSC.npt");
    }

    @Test
    public void testParserJohnDay() throws IOException {
        unitTest("JDA", "JDA_WSC.npt");
    }

    @Test
    public void testParserLittleGoose() throws IOException {
        unitTest("LGS", "LGS_WSC.npt");
    }

    @Test
    public void testParserLowerMonumental() throws IOException {
        unitTest("LMN", "LMN_WSC.npt");
    }

    @Test
    public void testParserLowerGranite() throws IOException {
        unitTest("LWG", "LWG_WSC.npt");
    }

    @Test
    public void testParserMcNary() throws IOException {
        unitTest("MCN", "MCN_WSC.npt");
    }

    @Test
    public void testParserParticleTracking() throws IOException {
        unitTest("ParticleTracking", "wsc.csv");
    }

    @Test
    public void testParserSpokane() throws IOException {
        unitTest("Spokane", "wsc.npt");
    }

    @Test
    public void testParserTheDalles() throws IOException {
        unitTest("TDA", "TDA_WSC.npt");
    }
}
