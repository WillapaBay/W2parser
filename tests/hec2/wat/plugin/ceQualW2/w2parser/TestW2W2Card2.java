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

    @Test
    public void Test2() throws IOException {
        String infile = "data/GCL/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numWaterBodies = gridCard.getNumWaterBodies();
        List<Integer> numFieldsList = new ArrayList<>();
        numFieldsList.add(18);
        numFieldsList.add(10);
        numFieldsList.add(10);
        numFieldsList.add(10);
        numFieldsList.add(10);
        numFieldsList.add(3);
        numFieldsList.add(3);
        numFieldsList.add(3);
        numFieldsList.add(3);
        numFieldsList.add(3);
        numFieldsList.add(3);
        numFieldsList.add(13);
        numFieldsList.add(27);
        numFieldsList.add(11);
        numFieldsList.add(15);
        numFieldsList.add(2);
        numFieldsList.add(2);
        numFieldsList.add(2);
        numFieldsList.add(2);
        numFieldsList.add(2);
        numFieldsList.add(2);
        numFieldsList.add(2);
        numFieldsList.add(14);
        numFieldsList.add(2);
        numFieldsList.add(9);

        SnapshotSegmentCard2 snpCard = new SnapshotSegmentCard2(w2con,
                numWaterBodies, numFieldsList, 8);
    }
}
