package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestMiscellCard {

    @Test
    public void testMiscellCard() throws IOException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        MiscellCard miscellCard = new MiscellCard(w2con);
        System.out.println(miscellCard);

        // Turn habitat volume on
        String state = miscellCard.getHABTATC();
        System.out.println("Habitat volume state: " + state);
        miscellCard.setHABTATC(W2Globals.ON);
        miscellCard.updateTable();

        // Write updated W2 control file
        String outpath = "results/ColumbiaSlough";
        String outfile = new File(outpath,
                "w2_con.npt.testMiscell").toString();
        w2con.save(outfile);
    }
}
