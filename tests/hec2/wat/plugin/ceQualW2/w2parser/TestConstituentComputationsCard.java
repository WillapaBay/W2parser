package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;

public class TestConstituentComputationsCard {

    private void unitTest(String folder) throws IOException {
        String infile = String.format("data/%s/w2_con.npt", folder);
        W2ControlFile w2con = new W2ControlFile(infile);
        ConstituentComputationsCard card = new ConstituentComputationsCard(w2con);
        card.getCCC();
        card.getLIMC();
        card.getCUF();
        card.setCCC("ABC");
        card.setLIMC("DEF");
        card.setCUF(99);
        card.updateDataTable();
        card.updateW2ControlFileList();
        w2con.save(String.format("results/%s/w2_con.npt", folder));
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
