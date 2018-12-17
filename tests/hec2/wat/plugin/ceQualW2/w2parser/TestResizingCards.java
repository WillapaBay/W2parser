package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;

public class TestResizingCards {

    @Test
    public void TestExpandCard() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        W2FileCard qinCard = new W2FileCard(w2con, W2CardNames.BranchInflowFilenames, numBranches);
        w2con.expandCard(972, 6, 3);
    }

    @Test
    public void TestShrinkCard() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        W2FileCard qinCard = new W2FileCard(w2con, W2CardNames.BranchInflowFilenames, numBranches);
        w2con.shrinkCard(972, 6, 4);
    }
}
