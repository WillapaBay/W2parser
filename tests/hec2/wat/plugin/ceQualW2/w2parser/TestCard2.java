package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCard2 {

    @Test
    public void Test1() throws FileNotFoundException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        List<Integer> numFields = new ArrayList<>();
        for (int i = 0; i < numBranches; i++) {
            numFields.add(1);
        }
        FileCard2 qinCard = new FileCard2(w2con, CardNames.BranchInflowFilenames,
                numBranches, numFields);
    }
}
