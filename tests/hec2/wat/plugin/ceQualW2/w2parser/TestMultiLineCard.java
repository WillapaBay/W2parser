package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;

public class TestMultiLineCard {

    @Test
    public void testMultiLineCard() throws FileNotFoundException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        MultiLineCard mCard = new MultiLineCard(w2con,
                CardNames.InflowActiveConstituentControl, 39);
    }
}
