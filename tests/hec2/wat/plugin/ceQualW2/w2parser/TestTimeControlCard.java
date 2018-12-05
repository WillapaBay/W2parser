package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestTimeControlCard {

    @Test
    public void testTimeControlCard() throws IOException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        TimeControlCard timeControlCard = new TimeControlCard(w2con);
        double jdayMin = timeControlCard.getJdayMin();
        double jdayMax = timeControlCard.getJdayMax();
        int startYear = timeControlCard.getStartYear();
        System.out.println(timeControlCard);
    }
}
