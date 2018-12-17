package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static hec2.wat.plugin.ceQualW2.w2parser.W2Globals.OFF;

public class TestNumberStructuresCard {

    @Test
    public void test_Get_NSTR_ColumbiaSlough() throws IOException {
        String infile = String.format("data/%s/w2_con.npt", "ColumbiaSlough");
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        NumberStructuresCard card = new NumberStructuresCard(w2con, gridCard.getNumBranches());
        List<Integer> NSTR_actual = card.getNSTR();
        List<Integer> NSTR_expected = Arrays.asList(0, 0);
        assert NSTR_expected.equals(NSTR_actual);
    }

    @Test
    public void test_Get_NSTR_GrandCoulee() throws IOException {
        String infile = String.format("data/%s/w2_con.npt", "GCL");
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        NumberStructuresCard card = new NumberStructuresCard(w2con, gridCard.getNumBranches());
        List<Integer> NSTR_actual = card.getNSTR();
        List<Integer> NSTR_expected = Arrays.asList(
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        assert NSTR_expected.equals(NSTR_actual);
    }

    @Test
    public void test_Get_NSTR_ParticleTracking() throws IOException {
        String infile = String.format("data/%s/w2_con.npt", "ParticleTracking");
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        NumberStructuresCard card = new NumberStructuresCard(w2con, gridCard.getNumBranches());
        List<Integer> NSTR_actual = card.getNSTR();
        List<Integer> NSTR_expected = Arrays.asList(5, 0, 0, 0, 0, 0);
        assert NSTR_expected.equals(NSTR_actual);
    }

    @Test
    public void test_Get_NSTR_TheDalles() throws IOException {
        String infile = String.format("data/%s/w2_con.npt", "TDA");
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        NumberStructuresCard card = new NumberStructuresCard(w2con, gridCard.getNumBranches());
        List<Integer> NSTR_actual = card.getNSTR();
        List<Integer> NSTR_expected = Arrays.asList(0);
//        List<Integer> NSTR_expected = Arrays.asList(
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0);
        assert NSTR_expected.equals(NSTR_actual);
    }

    @Test
    public void test_Get_DYNELEV_ColumbiaSlough() throws IOException {
        String infile = String.format("data/%s/w2_con.npt", "ColumbiaSlough");
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        NumberStructuresCard card = new NumberStructuresCard(w2con, gridCard.getNumBranches());
        List<String> DYNELEV_actual = card.getDYNELEV();
        List<String> DYNELEV_expected = Arrays.asList(OFF, OFF);
        assert DYNELEV_expected.equals(DYNELEV_actual);
    }

    @Test
    public void test_Get_DYNELEV_GrandCoulee() throws IOException {
        String infile = String.format("data/%s/w2_con.npt", "GCL");
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        NumberStructuresCard card = new NumberStructuresCard(w2con, gridCard.getNumBranches());
        List<String> DYNELEV_actual = card.getDYNELEV();
        List<String> DYNELEV_expected = Arrays.asList(
                OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF,
                OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF);
        assert DYNELEV_expected.equals(DYNELEV_actual);
    }

    @Test
    public void test_Get_DYNELEV_ParticleTracking() throws IOException {
        String infile = String.format("data/%s/w2_con.npt", "ParticleTracking");
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        NumberStructuresCard card = new NumberStructuresCard(w2con, gridCard.getNumBranches());
        List<String> DYNELEV_actual = card.getDYNELEV();
        List<String> DYNELEV_expected = Arrays.asList(OFF, OFF, OFF, OFF, OFF, OFF);
        assert DYNELEV_expected.equals(DYNELEV_actual);
    }

    @Test
    public void test_Get_DYNELEV_TheDalles() throws IOException {
        String infile = String.format("data/%s/w2_con.npt", "TDA");
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        NumberStructuresCard card = new NumberStructuresCard(w2con, gridCard.getNumBranches());
        List<String> DYNELEV_actual = card.getDYNELEV();
        List<String> DYNELEV_expected = Collections.singletonList(OFF);
        assert DYNELEV_expected.equals(DYNELEV_actual);
    }

    public void unitTest(String folder) throws IOException {
        String infile = String.format("data/%s/w2_con.npt", folder);
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        NumberStructuresCard card = new NumberStructuresCard(w2con, gridCard.getNumBranches());
        List<Integer> NSTR = card.getNSTR();
        List<String> DYNELEV = card.getDYNELEV();
        for (int i = 0; i < gridCard.getNumBranches(); i++) {
            NSTR.set(i, 990 + i);
            DYNELEV.set(i, "Test " + i);
        }
        card.updateDataTable();
        card.updateW2ControlFileList();
        w2con.save(String.format("results/%s/TestNumberStructuresCard_w2_con.npt", folder));
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
