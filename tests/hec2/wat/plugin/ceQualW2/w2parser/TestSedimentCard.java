package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestSedimentCard {

    @Test
    public void Test1() throws IOException {
        W2ControlFile w2con = new W2ControlFile("data/ParticleTracking/w2_con.npt");
        GridCard gridCard = new GridCard(w2con);
        SedimentCard sCard = new SedimentCard(w2con, gridCard.getNumWaterBodies());
        List<String> SEDC = sCard.getSEDC();
        SEDC.set(0, "Hello");
        sCard.setSEDC(SEDC);
        sCard.updateTable();
    }
}
