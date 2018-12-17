package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestInitialConditionsCard {

    @Test
    public void Test1() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        InitialConditionsCard iCard = new InitialConditionsCard(w2con, gridCard.getNumWaterBodies());
        List<Double> T2I = iCard.getT2I();
        T2I.set(0, -99.0);
        iCard.setT2I(T2I);
        iCard.updateTable();
    }
}
