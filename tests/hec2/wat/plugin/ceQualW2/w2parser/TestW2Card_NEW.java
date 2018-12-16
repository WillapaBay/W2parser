package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestW2Card_NEW {

    @Test
    public void Test1() throws IOException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        List<Integer> numFieldsList = new ArrayList<>();
        for (int i = 0; i < numBranches; i++) {
            numFieldsList.add(1);
        }
        W2FileCard qinCard = new W2FileCard(w2con, W2CardNames.BranchInflowFilenames,
                numBranches, numFieldsList);
        List<String> files = qinCard.getFileNames();
        List<String> branches = qinCard.getBranches();
        branches.add("BR 99");
        files.add("newfile99.txt");
        qinCard.setBranches(branches);
        qinCard.setFileNames(files);
        qinCard.updateDataTable();
        qinCard.updateW2ControlFileList();
        w2con.save("results/GCL/w2_con.npt");
    }

    @Test
    public void Test2() throws IOException {
        String infile = "data/GCL/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numWaterBodies = gridCard.getNumWaterBodies();

        Integer[] numSegments = {18, 10, 10, 10, 10, 3, 3, 3, 3, 3, 3,
        13, 27, 11, 15, 2, 2, 2, 2, 2, 2, 2, 14, 2, 9};
        List<Integer> numSegmentsList = Arrays.asList(numSegments);

        SnapshotSegmentCard snpCard = new SnapshotSegmentCard(w2con,
                numWaterBodies, numSegmentsList, 8);

        List<List<Integer>> segmentsList = snpCard.getSegmentsList();
        List<String> identifiers = snpCard.getRecordIdentifiers();

        // Add some data
        identifiers.add("WB 99");
        List<Integer> segments = new ArrayList<>(
                Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12));
        segmentsList.add(segments);
        snpCard.setRecordIdentifiers(identifiers);
        snpCard.setSegmentsList(segmentsList);
        snpCard.updateDataTable();
        snpCard.updateW2ControlFileList();
        w2con.save("results/GCL/w2_con.npt");
    }
}
