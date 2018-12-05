package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestW2W2Card2 {

    @Test
    public void Test1() throws IOException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        List<Integer> numFields = new ArrayList<>();
        for (int i = 0; i < numBranches; i++) {
            numFields.add(1);
        }
        W2FileCard2 qinCard = new W2FileCard2(w2con, W2CardNames.BranchInflowFilenames,
                numBranches, numFields);
    }
}
