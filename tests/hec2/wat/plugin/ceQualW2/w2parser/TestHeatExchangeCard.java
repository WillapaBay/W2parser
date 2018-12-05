package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class TestHeatExchangeCard {

    @Test
    public void Test1() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        HeatExchangeCard hCard = new HeatExchangeCard(w2con, gridCard.getNumWaterBodies());
        List<String> SROC = hCard.getSROC();
        SROC.set(0, "Hello");
        hCard.setSROC(SROC);
        hCard.updateTable();
    }
}
