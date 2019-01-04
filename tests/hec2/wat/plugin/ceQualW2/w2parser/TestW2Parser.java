package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.IOException;
import java.util.List;

public class TestW2Parser {

    /**
     * Test code
     * @param folder Input and output folder name
     * @return W2Parser object
     */
    public W2Parser unitTest(String folder) throws IOException {
        String infile = String.format("data/%s/w2_con.npt", folder);
        W2ControlFile w2con = new W2ControlFile(infile);
        W2Parser w2Parser = new W2Parser(w2con);
        w2Parser.readControlFile();

        // Get input parameters for flow, temperature, and constituents
        List<W2Parameter> inputW2Parameters = w2Parser.getInputW2Parameters();

        // Get input parameters for meteorology
        List<W2Parameter> metW2Parameters = w2Parser.getMeteorologyW2Parameters();

        // Get TSR output parameters for flow, temperature, and constituents
        List<W2Parameter> tsrOutputW2Parameters = w2Parser.getTsrOutputW2Parameters();

        // Get withdrawal output parameters for flow, temperature, and constituents
        List<W2Parameter> withdrawalOutputW2Parameters = w2Parser.getWithdrawalOutputW2Parameters();

        // Display tables
        System.out.println("\nTable of Flow, Temperature, and W2Constituent Inputs:\n");
        w2Parser.printTable(inputW2Parameters);
        System.out.println("\nTable of Meteorology Inputs:\n");
        w2Parser.printTable(metW2Parameters);
        System.out.println("\nTable of TSR Outputs:\n");
        w2Parser.printTable(tsrOutputW2Parameters);
        System.out.println("\nTable of Withdrawal Outputs:\n");
        w2Parser.printTable(withdrawalOutputW2Parameters);

        // Write tables to output files
        w2Parser.writeTable(inputW2Parameters,
                String.format("results/%s/W2_input_parameters.txt", folder));
        w2Parser.writeTable(metW2Parameters,
                String.format("results/%s/W2_met_parameters.txt", folder));
        w2Parser.writeTable(tsrOutputW2Parameters,
                String.format("results/%s/W2_TSR_output_parameters.txt", folder));
        w2Parser.writeTable(withdrawalOutputW2Parameters,
                String.format("results/%s/W2_Withdrawal_output_parameters.txt", folder));
        w2Parser.writeInitialWaterSurfaceElevations(
                String.format("results/%s/W2_WSEL.txt", folder));


        return w2Parser;
    }

    @Test
    public void testParserBonneville() throws IOException {
        W2Parser w2Parser = unitTest("BON");
    }

    @Test
    public void testParserChiefJoseph() throws IOException {
        W2Parser w2Parser = unitTest("CHJ");
    }

    @Test
    public void testParserColumbiaSlough() throws IOException {
        W2Parser w2Parser = unitTest("ColumbiaSlough");
    }

    @Test
    public void testParserDworshak() throws IOException {
        W2Parser w2Parser = unitTest("DWR");
    }

    @Test
    public void testParserGrandCoulee() throws IOException {
        W2Parser w2Parser = unitTest("GCL");
    }

    @Test
    public void testParserIceHarbor() throws IOException {
        W2Parser w2Parser = unitTest("IHR");
    }

    @Test
    public void testParserJohnDay() throws IOException {
        W2Parser w2Parser = unitTest("JDA");
    }

    @Test
    public void testParserLittleGoose() throws IOException {
        W2Parser w2Parser = unitTest("LGS");
    }

    @Test
    public void testParserLowerMonumental() throws IOException {
        W2Parser w2Parser = unitTest("LMN");
    }

    @Test
    public void testParserLowerGranite() throws IOException {
        W2Parser w2Parser = unitTest("LWG");
    }

    @Test
    public void testParserMcNary() throws IOException {
        W2Parser w2Parser = unitTest("MCN");
    }

    @Test
    public void testParserParticleTracking() throws IOException {
        W2Parser w2Parser = unitTest("ParticleTracking");
        // Update time window
        w2Parser.setJdayMin(1.5);
        w2Parser.setJdayMax(52.0);
        w2Parser.setStartYear(2019);

        // Get and revise initial water surface elevations
        List<List<Double>> WSEL = w2Parser.getInitialWaterSurfaceElevations();

        // Set model output timestep and granularity
        w2Parser.setOutputTimestep(2.0, W2Globals.Granularity.HOURS);
        w2Parser.saveControlFile("results/ParticleTracking/w2_con.npt");
    }

    @Test
    public void testParserSpokane() throws IOException {
        W2Parser w2Parser = unitTest("Spokane");
    }

    @Test
    public void testParserTheDalles() throws IOException {
        W2Parser w2Parser = unitTest("TDA");
    }

}
