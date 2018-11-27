package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;

public class TestConstituentComputationsCard {

    @Test
    public void testReadLIMC() throws FileNotFoundException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        ConstituentComputationsCard cstCompCard = new ConstituentComputationsCard(w2con);
        String limc = cstCompCard.getLIMC();
        System.out.println("LIMC = " + limc);
    }
}
