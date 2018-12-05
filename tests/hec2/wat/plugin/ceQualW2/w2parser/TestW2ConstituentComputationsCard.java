package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestW2ConstituentComputationsCard {

    @Test
    public void testReadLIMC() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        ConstituentComputationsCard cstCompCard = new ConstituentComputationsCard(w2con);
        String limc = cstCompCard.getLIMC();
        System.out.println("LIMC = " + limc);
    }
}
