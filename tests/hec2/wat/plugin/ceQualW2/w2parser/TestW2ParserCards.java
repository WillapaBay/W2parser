package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class TestW2ParserCards {

    @Test
    public void testCreateOutputFlows() throws IOException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        String outfile = "results/ColumbiaSlough/" +
                "w2_con.npt.testReadInfiles";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        int numWaterbodies = gridCard.getNumWaterBodies();

        ArrayList<String> outFiles = new ArrayList<>();

        for (int branch = 1; branch <= numBranches; branch++) {
            outFiles.add("qwo_br" + branch + ".opt");
        }
    }

}
