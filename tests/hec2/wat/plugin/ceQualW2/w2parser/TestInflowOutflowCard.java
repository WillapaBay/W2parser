package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;

public class TestInflowOutflowCard {

    public void unitTest(String folder) throws IOException {
        String infile = String.format("data/%s/w2_con.npt", folder);
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        InflowOutflowCard card = new InflowOutflowCard(w2con);
        card.getNumTributaries();       // NTR
        card.getNumStructures();        // NST
        card.getNumInternalWeirs();     // NIW
        card.getNumWithdrawals();       // NWD
        card.getNumGates();             // NGT
        card.getNumSpillways();         // NSP
        card.getNumPipes();             // NPI
        card.getNumPumps();             // NPU

        card.setNumTributaries(991);    // NTR
        card.setNumStructures(992);     // NST
        card.setNumInternalWeirs(993);  // NIW
        card.setNumWithdrawals(994);    // NWD
        card.setNumGates(995);          // NGT
        card.setNumSpillways(996);      // NSP
        card.setNumPipes(997);          // NPI
        card.setNumPumps(998);          // NPU
        card.updateDataTable();
        card.updateW2ControlFileList();
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
