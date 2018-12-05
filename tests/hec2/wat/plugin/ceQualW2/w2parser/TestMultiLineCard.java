package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestMultiLineCard {

    @Test
    public void testMultiLineCard() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        MultiLineCard mCard = new MultiLineCard(w2con,
                W2CardNames.InflowActiveConstituentControl, 39);
    }
}
