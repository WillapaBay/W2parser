package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static hec2.wat.plugin.ceQualW2.w2parser.Globals.OFF;

public class TestNumberStructuresCard {
    private NumberStructuresCard nCard;

    private List<Integer> getNSTR(String infile) throws FileNotFoundException {
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        nCard = new NumberStructuresCard(w2con, numBranches);
        return nCard.getNSTR();
    }

    private List<String> getDYNELEV(String infile) throws FileNotFoundException {
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        NumberStructuresCard nCard = new NumberStructuresCard(w2con, numBranches);
        return nCard.getDYNELEV();
    }

    @Test
    public void test_Get_NSTR_ColumbiaSlough() throws FileNotFoundException {
        List<Integer> NSTR_expected = Arrays.asList(0, 0);
        List<Integer> NSTR_actual = getNSTR("data/ColumbiaSlough/w2_con.npt");
        assert NSTR_expected.equals(NSTR_actual);
    }

    @Test
    public void test_Get_NSTR_GrandCoulee() throws FileNotFoundException {
        List<Integer> NSTR_expected = Arrays.asList(
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        List<Integer> NSTR_actual = getNSTR("data/GCL/w2_con.npt");
        assert NSTR_expected.equals(NSTR_actual);
    }

    @Test
    public void test_Get_NSTR_ParticleTracking() throws FileNotFoundException {
        List<Integer> NSTR_actual = getNSTR("data/ParticleTracking/w2_con.npt");
        List<Integer> NSTR_expected = Arrays.asList(5, 0, 0, 0, 0, 0);
        assert NSTR_expected.equals(NSTR_actual);
    }

    @Test
    public void test_Get_NSTR_TheDalles() throws FileNotFoundException {
        List<Integer> NSTR_expected = Arrays.asList(
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        List<Integer> NSTR_actual = getNSTR("data/GCL/w2_con.npt");
        assert NSTR_expected.equals(NSTR_actual);
    }

    @Test
    public void test_Get_DYNELEV_ColumbiaSlough() throws FileNotFoundException {
        List<String> DYNELEV_expected = Arrays.asList(OFF, OFF);
        List<String> DYNELEV_actual = getDYNELEV("data/ColumbiaSlough/w2_con.npt");
        assert DYNELEV_expected.equals(DYNELEV_actual);
    }

    @Test
    public void test_Get_DYNELEV_GrandCoulee() throws FileNotFoundException {
        List<String> DYNELEV_expected = Arrays.asList(
                OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF,
                OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF);
        List<String> DYNELEV_actual = getDYNELEV("data/GCL/w2_con.npt");
        assert DYNELEV_expected.equals(DYNELEV_actual);
    }

    @Test
    public void test_Get_DYNELEV_ParticleTracking() throws FileNotFoundException {
        List<String> DYNELEV_expected = Arrays.asList(OFF, OFF, OFF, OFF, OFF, OFF);
        List<String> DYNELEV_actual = getDYNELEV("data/ParticleTracking/w2_con.npt");
        assert DYNELEV_expected.equals(DYNELEV_actual);
    }

    @Test
    public void test_Get_DYNELEV_TheDalles() throws FileNotFoundException {
        List<String> DYNELEV_expected = Collections.singletonList(OFF);
        List<String> DYNELEV_actual = getDYNELEV("data/TDA/w2_con.npt");
        assert DYNELEV_expected.equals(DYNELEV_actual);
    }

    @Test
    public void test_Set_NSTR_ParticleTracking() throws FileNotFoundException {
        List<Integer> NSTR = getNSTR("data/ParticleTracking/w2_con.npt");
        NSTR.set(0, 20);
        nCard.setNSTR(NSTR);
        nCard.updateTable();
    }
}