package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.util.List;

public class TestBranchGeometryCard {

    @Test
    public void testBranchGeometryCard() throws FileNotFoundException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        int numWaterbodies = gridCard.getNumWaterBodies();
        BranchGeometryCard branchGeometryCard = new BranchGeometryCard(w2con, numBranches);
        List<Integer> DS = branchGeometryCard.getDS();
        DS.set(0, 200);
        branchGeometryCard.updateTable();
        w2con.save("results/ParticleTracking/w2_con.npt");
    }
}
