package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;

public class TestRestartCard {

    public void unitTest(String folder) throws IOException {
        String infile = String.format("data/%s/w2_con.npt", folder);
        W2ControlFile w2con = new W2ControlFile(infile);
        RestartCard restartCard = new RestartCard(w2con);
        int nrso = restartCard.getNRSO();
        RepeatingDoubleCard rsoDateCard = new RepeatingDoubleCard(w2con,
                W2CardNames.RestartDate, nrso, "%8.2f");
        RepeatingDoubleCard rsoFreqCard = new RepeatingDoubleCard(w2con,
                W2CardNames.RestartFrequency, nrso, "%8.3f");

        // Turn Restart output on
        restartCard.setRSOC(W2Globals.ON);
        restartCard.setRSIC(W2Globals.OFF);
        restartCard.setNRSO(2);

        // Clear existing RSO dates
        rsoDateCard.clearData();

        // Add a single RSOD at Julian day 1
        rsoDateCard.addData(40.0);
        rsoDateCard.addData(50.0);

        // Clear existing RSO frequencies
        rsoFreqCard.clearData();

        // Add a single frequency (to be specified by UI)
        rsoFreqCard.addData(100.0);
        rsoFreqCard.addData(200.0);

        // Update Restart filename
        W2FileCard rsiFileCard = new W2FileCard(w2con,"RSI FILE", 1);
        rsiFileCard.clearFileNames();

        // Turn on restart file....

        // Update records (commit to w2 control file array in memory)
        restartCard.updateDataTable();
        rsoDateCard.updateTable();
        rsoFreqCard.updateTable();
        rsiFileCard.updateDataTable();

        // Write updated W2 control file
        String outfile = "results/ColumbiaSlough/w2_con.npt.testRestart";
        w2con.save(outfile);
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
