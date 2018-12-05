package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestW2Parser {

    @Test
    public void testParserParticleTracking() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
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
                "results/ParticleTracking/W2_input_parameters.txt");
        w2Parser.writeTable(metW2Parameters,
                "results/ParticleTracking/W2_met_parameters.txt");
        w2Parser.writeTable(tsrOutputW2Parameters,
                "results/ParticleTracking/W2_TSR_output_parameters.txt");
        w2Parser.writeTable(withdrawalOutputW2Parameters,
                "results/ParticleTracking/W2_Withdrawal_output_parameters.txt");
        w2Parser.writeInitialWaterSurfaceElevations(
                "results/ParticleTracking/W2_WSEL.txt");

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
    public void testParserGrandCoulee() throws IOException {
        String infile = "data/GCL/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        W2Parser w2Parser = new W2Parser(w2con);
        w2Parser.readControlFile();

        // Get flow, temperature, and constituent input parameters
        List<W2Parameter> inputW2Parameters = w2Parser.getInputW2Parameters();

        // Get meteorology input parameters
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
                "results/GCL/W2_input_parameters.txt");
        w2Parser.writeTable(metW2Parameters,
                "results/GCL/W2_met_parameters.txt");
        w2Parser.writeTable(tsrOutputW2Parameters,
                "results/ParticleTracking/W2_TSR_output_parameters.txt");
        w2Parser.writeTable(withdrawalOutputW2Parameters,
                "results/ParticleTracking/W2_Withdrawal_output_parameters.txt");
        w2Parser.writeInitialWaterSurfaceElevations(
                "results/GCL/W2_WSEL.txt");

        w2Parser.saveControlFile("results/GCL/w2_con.npt");
    }
}
