package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class TestActiveDerivedConstituentsCard {

    @Test
    public void Test1() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        String graphFilename = w2con.getGraphFilename();
        W2GraphFile w2GraphFile = new W2GraphFile(graphFilename);
        List<W2Constituent> graphFileW2Constituents = w2GraphFile.getDerivedW2Constituents();
        int NDT = graphFileW2Constituents.size();
        GridCard gridCard = new GridCard(w2con);
        int NWB = gridCard.getNumWaterBodies();
        ActiveDerivedConstituentsCard card = new ActiveDerivedConstituentsCard(w2con, NDT, NWB);
    }
}
