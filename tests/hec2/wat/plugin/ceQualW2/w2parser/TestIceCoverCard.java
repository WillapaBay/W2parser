package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.util.List;

public class TestIceCoverCard {

    @Test
    public void Test1() throws FileNotFoundException {
        W2ControlFile w2con = new W2ControlFile("data/ParticleTracking/w2_con.npt");
        GridCard gridCard = new GridCard(w2con);
        IceCoverCard iCard = new IceCoverCard(w2con, gridCard.getNumWaterBodies());
        List<Double> ALBEDO = iCard.getALBEDO();
        ALBEDO.set(0, -99.0);
        iCard.setALBEDO(ALBEDO);
        iCard.updateTable();
    }
}
