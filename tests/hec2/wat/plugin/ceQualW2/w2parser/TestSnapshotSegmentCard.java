package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSnapshotSegmentCard {

    public void unitTest(String folder) throws IOException {
        String infile = String.format("data/%s/w2_con.npt", folder);
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numWaterBodies = gridCard.getNumWaterBodies();

        SnapshotPrintCard card = new SnapshotPrintCard(w2con, numWaterBodies);
        List<Integer> numSegmentsList = card.getNISNP(); // Number of segments

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
        w2con.save(String.format("results/%s/w2_con.npt", folder));
    }

    @Test
    public void testParserBonneville() throws IOException {
        unitTest("BON");
    }

    @Test
    public void testParserChiefJoseph() throws IOException {
        unitTest("CHJ");
    }

    @Test
    public void testParserColumbiaSlough() throws IOException {
        unitTest("ColumbiaSlough");
    }

    @Test
    public void testParserDworshak() throws IOException {
        unitTest("DWR");
    }

    @Test
    public void testParserGrandCoulee() throws IOException {
        unitTest("GCL");
    }

    @Test
    public void testParserIceHarbor() throws IOException {
        unitTest("IHR");
    }

    @Test
    public void testParserJohnDay() throws IOException {
        unitTest("JDA");
    }

    @Test
    public void testParserLittleGoose() throws IOException {
        unitTest("LGS");
    }

    @Test
    public void testParserLowerMonumental() throws IOException {
        unitTest("LMN");
    }

    @Test
    public void testParserLowerGranite() throws IOException {
        unitTest("LWG");
    }

    @Test
    public void testParserMcNary() throws IOException {
        unitTest("MCN");
    }

    @Test
    public void testParserParticleTracking() throws IOException {
        unitTest("ParticleTracking");
    }

    @Test
    public void testParserSpokane() throws IOException {
        unitTest("Spokane");
    }

    @Test
    public void testParserTheDalles() throws IOException {
        unitTest("TDA");
    }
}
