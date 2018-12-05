package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestDltControlW2Card {

    @Test
    public void testDltControlCard() throws IOException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        TimestepControlCard dltControlCard = new TimestepControlCard(w2con);
        dltControlCard.setNdt(1);
        dltControlCard.setDltMin(0.0001);
        dltControlCard.setDltIntr(W2Globals.ON);
        dltControlCard.updateTable();

        // Write updated W2 control file
        String outpath = "results/ColumbiaSlough";
        String outfile = new File(outpath,
                "w2_con.npt.testDltControlCard").toString();
        w2con.save(outfile);
    }
}
