package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;

public class TestGridCard {

    @Test
    public void testGridCard() throws FileNotFoundException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        System.out.println(gridCard);
        gridCard.setNumBranches(500);
        gridCard.setNumWaterBodies(1000);
        gridCard.updateTable();
    }
}
