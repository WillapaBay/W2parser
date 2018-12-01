package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.FileNotFoundException;

public class TestResizingCards {

    @Test
    public void TestExpandCard() throws FileNotFoundException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        FileCard qinCard = new FileCard(w2con, CardNames.BranchInflowFilenames, numBranches);
        w2con.expandCard(972, 6, 3);
    }

    @Test
    public void TestShrinkCard() throws FileNotFoundException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        FileCard qinCard = new FileCard(w2con, CardNames.BranchInflowFilenames, numBranches);
        w2con.shrinkCard(972, 6, 4);
    }
}
