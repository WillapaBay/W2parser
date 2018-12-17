package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestInitialConditionsCard {

    public void unitTest(String folder) throws IOException {
        String infile = String.format("data/%s/w2_con.npt", folder);
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        InitialConditionsCard iCard = new InitialConditionsCard(w2con, gridCard.getNumWaterBodies());
        List<Double> T2I = iCard.getT2I();
        T2I.set(0, -99.0);
        iCard.setT2I(T2I);
        iCard.updateDataTable();
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
