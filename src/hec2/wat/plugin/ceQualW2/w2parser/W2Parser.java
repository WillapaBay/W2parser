package hec2.wat.plugin.ceQualW2.w2parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class W2Parser {
    private W2ControlFile w2con;
    private int startYear; // Simulation start year
    private double jdayMin; // Simulation start time (Julian days)
    private double jdayMax; // Simulation end time (Julian days)

    // Approx 195 total cards in a W2 control file....
    // 37 / 195 cards = 19%
    private TimeControlCard timeControlCard;
    private GridCard gridCard;
    private BranchGeometryCard branchGeometryCard;
    private InflowOutflowCard inOutflowCard;
    private W2GraphFile w2GraphFile;
    private TsrPlotCard tsrPlotCard;
    private ActiveConstituentsCard activeConstituentsCard;
    private ActiveDerivedConstituentsCard activeDerivedConstituentsCard;
    private ActiveConstituentFluxesCard activeConstituentFluxesCard;
    private WithdrawalOutputCard withdrawalOutputCard;
    private IceCoverCard iceCoverCard;
    private InitialConditionsCard initCondCard;
    private ConstituentDimensionsCard constituentDimensionsCard;
    private ConstituentComputationsCard constituentComputationsCard;
    private DistributedTributariesCard dstTribCard;
    private CalculationsCard calculationsCard;
    private LocationCard locationCard;
    private NumberStructuresCard numberStructuresCard;
    private SedimentCard sedimentCard;
    private HeatExchangeCard heatExchangeCard;

    private RepeatingIntegerCard tsrSegCard;
    private RepeatingIntegerCard wdSegmentCard; // "WD SEG"
    private RepeatingIntegerCard withdrawalOutputSegmentCard; // "WITH SEG"

    private RepeatingDoubleCard withdrawalFrequencyCard;
    private RepeatingDoubleCard tsrLayerCard;

    private MultiRecordRepeatingDoubleCard constituentICcard;

    private BranchConstituentsCard branchConstituentsCard;
    private DistributedTributaryConstituentsCard distributedTributaryConstituentsCard;
    private PrecipConstituentsCard precipConstituentsCard;
    private TributaryConstituentsCard tributaryConstituentsCard;
    private TributarySegmentCard tributarySegmentCard;

    private W2FileCard tsrFilenameCard;
    private W2FileCard qinCard;
    private W2FileCard tinCard;
    private W2FileCard cinCard;
    private W2FileCard qdtCard;
    private W2FileCard tdtCard;
    private W2FileCard qtrCard;
    private W2FileCard ttrCard;
    private W2FileCard qwdCard;
    private W2FileCard qotCard;
    private W2FileCard cdtCard;
    private W2FileCard ctrCard;
    private W2FileCard cprCard;
    private W2FileCard metCard;
    private W2FileCard bathyFileCard;

    private int NBR; // Number of branches
    private int NWB; // Number of water bodies
    private int IMX; // Number of segments
    private int KMX; // Number of layers
    private int NTR; // Number of tributaries
    private int NST; // Number of structures
    private int NWD; // Number of withdrawals
    private int NGT; // Number of gates
    private int numConstituents; // Number of constituents (total, NCT)
    private int numDerivedConstituents; // Number of constituents (total, NCT)
    private int NIWDO;

    private List<Integer> UHS; // Upstream Head Segment (upstream BC)
    private List<Integer> DHS; // Downstream Head Segment (downstream BC)
    private List<Integer> US;  // Branch upstream segment
    private List<Integer> DS;  // Branch downstream segment
    private List<Integer> BS;  // Starting branch of waterbody
    private List<Integer> BE;  // Ending branch of waterbody
    private List<String> DTRC; // Distributed Tributary Option (ON/OFF)
    private List<Integer> IWD;
    private List<W2Constituent> graphFileW2Constituents;
    private List<W2Constituent> graphFileDerivedW2Constituents;
    private List<W2Parameter> inputW2Parameters;
    private List<W2Parameter> meteorologyW2Parameters;
    private List<W2Parameter> tsrOutputW2Parameters;
    private List<W2Parameter> withdrawalOutputW2Parameters;
    private List<List<Double>> initialWaterSurfaceElevations;
    private List<Double> initialTemperatures;
    private List<String> constituentNames;
    private List<String> derivedConstituentNames;
    private List<List<Double>> initialConcentrations;
    private List<Integer> IWDO;

    private boolean CONSTITUENTS = false;
    private boolean DERIVED_CALC = false;
    private boolean TIME_SERIES = false;
    private boolean DOWNSTREAM_OUTFLOW = false;
    private static final int NUM_FLUXES = 73;   // Currently, this must be hard-coded.
    // Version 4.1 has 73 fluxes.
    private List<Boolean> SEDIMENT_CALC;

    /**
     * Handles reading and updating the CE-QUAL-W2 control file and
     * associated bathymetry files
     *
     * @param w2con CE-QUAL-W2 control file object
     */
    public W2Parser(W2ControlFile w2con) {
        this.w2con = w2con;
    }

    /**
     * Handles reading and updating the CE-QUAL-W2 control file and
     * associated bathymetry files
     *
     * @param inPath Path to CE-QUAL-W2 control file
     */
    public W2Parser(String inPath) throws IOException {
        W2ControlFile w2con = new W2ControlFile(inPath);
    }

    /**
     * Read the CE-QUAL-W2 control file
     */
    public void readControlFile() {
        // Initialize time window
        timeControlCard = new TimeControlCard(w2con);
        jdayMin = timeControlCard.getStartDay();
        jdayMax = timeControlCard.getEndDay();
        startYear = timeControlCard.getStartYear();

        // Initialize grid info
        gridCard = new GridCard(w2con);
        NWB = gridCard.getNumWaterBodies();
        NBR = gridCard.getNumBranches();
        IMX = gridCard.getNumCrossSections();
        KMX = gridCard.setNumLayers();

        // Initialize branch geometry info
        branchGeometryCard = new BranchGeometryCard(w2con, NBR);
        UHS = branchGeometryCard.getUHS();
        DHS = branchGeometryCard.getDHS();
        US = branchGeometryCard.getUS();
        DS = branchGeometryCard.getDS();

        // Get lists of starting and ending branches for each waterbody
        locationCard = new LocationCard(w2con, NWB);
        BS = locationCard.getBS();
        BE = locationCard.getBE();

        // Initialize Inflow/Outflow info
        inOutflowCard = new InflowOutflowCard(w2con);
        NTR = inOutflowCard.getNumTributaries();
        NST = inOutflowCard.getNumStructures();
        NWD = inOutflowCard.getNumWithdrawals();
        NGT = inOutflowCard.getNumGates();

        // Initialize withdrawal output info
        wdSegmentCard = new RepeatingIntegerCard(w2con,
                W2CardNames.WithdrawalSegment, NWD, "%8d");
        IWD = wdSegmentCard.getValues();
        withdrawalOutputCard = new WithdrawalOutputCard(w2con);
        NIWDO = withdrawalOutputCard.getNIWDO();
        String WDOC = withdrawalOutputCard.getWDOC();
        RepeatingIntegerCard withdrawalOutputSegmentCard = new RepeatingIntegerCard(w2con,
                "WITH SEG", NIWDO, "%8d");
        IWDO = withdrawalOutputSegmentCard.getValues();
        // Note: the following is different from the wdSegmentCard ("WD SEG") above
        if (isOn(WDOC)) DOWNSTREAM_OUTFLOW = true;

        // Initialize constituent info, including total number of constituents
        // and number of active constituents
        String graphFilename = w2con.getGraphFilename();
        w2GraphFile = new W2GraphFile(graphFilename);
        graphFileW2Constituents = w2GraphFile.getW2Constituents();
        numConstituents = graphFileW2Constituents.size();
        //numConstituents = computeNumberOfConstituents();
        activeConstituentsCard = new ActiveConstituentsCard(w2con, numConstituents);
        constituentComputationsCard = new ConstituentComputationsCard(w2con);
        String CCC = constituentComputationsCard.getCCC();
        if (isOn(CCC)) CONSTITUENTS = true;
        constituentDimensionsCard = new ConstituentDimensionsCard(w2con);

        // Initialized derived constituent info
        graphFileDerivedW2Constituents = w2GraphFile.getDerivedW2Constituents();
        numDerivedConstituents = graphFileDerivedW2Constituents.size();
        activeDerivedConstituentsCard = new ActiveDerivedConstituentsCard(w2con,
                numDerivedConstituents, NWB);
        activeConstituentFluxesCard =
                new ActiveConstituentFluxesCard(w2con, NUM_FLUXES, NWB);
        List<List<String>> CDWBC = activeDerivedConstituentsCard.getStates();

        List<Boolean> derivedIsOn = new ArrayList<>();
        for (List<String> values : CDWBC) {
            values.forEach(value -> derivedIsOn.add(isOn(value)));
        }
        if (any(derivedIsOn) && CONSTITUENTS) DERIVED_CALC = true;

        // TSR output
        tsrPlotCard = new TsrPlotCard(w2con);
        String TSRC = tsrPlotCard.getTSRC();
        TIME_SERIES = isOn(TSRC);

        // Sediment diagenesis
        sedimentCard = new SedimentCard(w2con, NWB);
        List<String> SEDC = sedimentCard.getSEDC();
        SEDIMENT_CALC = new ArrayList<>();
        SEDC.forEach(sedc -> {
            if (isOn(sedc) && CONSTITUENTS) SEDIMENT_CALC.add(true);
            else SEDIMENT_CALC.add(false);
        });

        tributarySegmentCard = new TributarySegmentCard(w2con, NTR, 8);

        /*
         * Fetch model inputs and outputs. For each location, flow type, and
         * parameter, a W2Parameter object will be created containing the location,
         * short name, long name, units, filename (input or output), and
         * indicators for inflow or outflow and model input or output. The location
         * is a combination of location and flow type, e.g., Branch 1 Distributed
         * Tributary. The short name of a constituent is specified in the control
         * file. The long name and units are obtained from the graph.npt file.
         */
        // Flow, temperature, and constituent model inputs (inflows & outflows)
        inputW2Parameters = fetchInputParameters();
        // W2Constituent names from graph.npt file
        constituentNames = fetchConstituentNames();
        // Initial temperatures by waterbody
        initialTemperatures = fetchInitialTemperatures();
        // Initial concentrations for each constituent by waterbody
        initialConcentrations = fetchInitialConcentrations();
        // Meteorology parameters
        meteorologyW2Parameters = fetchMeteorologyInputs();
        // Initial water surface elevations by waterbody
        initialWaterSurfaceElevations = fetchInitialWaterSurfaceElevations();

        // Time series files
        if (TIME_SERIES)
            tsrOutputW2Parameters = fetchTsrOutputParameters();
        if (DOWNSTREAM_OUTFLOW)
            withdrawalOutputW2Parameters = fetchWithdrawalOutflowParameters();

        handleVerticalProfiles();  // TODO Add support for vertical profile files, using -1 in initialConcentrations
        handleLongitudinalProfiles();  // TODO Add support for longitudinal profile files, using -2 in initialConcentrations
        handleHabitatVolume(); // TODO Add support for habitat volume
    }

    /**
     * Create withdrawal Parameters
     * <p>
     * Outputs include:
     * QWD: Withdrawal output flow
     *
     * @return List of withdrawal output parameters
     */
    private List<W2Parameter> fetchWithdrawalOutflowParameters() {
        List<W2Parameter> w2Parameters = new ArrayList<>();
        List<Integer> NSTR = numberStructuresCard.getNSTR();
        W2Parameter w2Parameter;
        int outputWaterbody;
        int outputSegment;
        String outputFilename;
        for (int jwd = 0; jwd < NIWDO; jwd++) {
            outputSegment = IWDO.get(jwd);
            outputWaterbody = getOutputWaterbody(outputSegment, US, DS, BS, BE, NWB); // 1/12/19

            // Withdrawal flow, QWD (qwo)
            int icol = 1; // Every qwo file contains at least one data column for the combined outflow
            outputFilename = String.format("qwo_%d.opt", outputSegment);
            String location = String.format("Seg %d Withdrawal", outputSegment);
            String ioName = "wd_" + jwd + "_seg" + outputSegment;

            // Number of columns equals 1 for the combined flow plus one column for each structure
            int ncol = 1;
            for (int jstr = 0; jstr < NSTR.get(outputWaterbody - 1); jstr++) {
                ncol++;
            }

            w2Parameter = new W2Parameter(location, "Flow-QWD",
                    "Withdrawal Flow", "QWD", "m^3/s", icol, ncol,
                    outputFilename, "outflow", "output", ioName,
                    "", "flow");
            w2Parameter.setSegment(outputSegment);
            w2Parameters.add(w2Parameter);
            icol++;

            for (int jstr = 0; jstr < NSTR.get(outputWaterbody - 1); jstr++) {
                int outlet = jstr + 1;
                w2Parameter = new W2Parameter(location,
                        String.format("Flow-QWD_outlet_%d", outlet),
                        String.format("Withdrawal Flow - Outlet %d", outlet),
                        "QWD", "m^3/s", icol, ncol, outputFilename,
                        "outflow", "output", ioName,
                        "", "flow");
                w2Parameter.setSegment(outputSegment);
                w2Parameters.add(w2Parameter);
                icol++;
            }

            // Withdrawal temperature, TWO
            icol = 1; // Every qwo file contains at least one data column for the combined outflow
            outputFilename = String.format("two_%d.opt", outputSegment);
            location = String.format("Seg %d Withdrawal", outputSegment);

            // Number of columns equals 1 for the combined flow plus one column for each structure
            ncol = 1;
            for (int jstr = 0; jstr < NSTR.get(outputWaterbody - 1); jstr++) {
                ncol++;
            }

            w2Parameter = new W2Parameter(location, "Temp-TWO",
                    "Withdrawal Temperature", "T", "C",
                    icol, ncol, outputFilename, "outflow",
                    "output", ioName, "", "temperature");
            w2Parameter.setSegment(outputSegment);
            w2Parameters.add(w2Parameter);
            icol++;

            for (int jstr = 0; jstr < NSTR.get(outputWaterbody - 1); jstr++) {
                int outlet = jstr + 1;
                w2Parameter = new W2Parameter(location,
                        String.format("Temp-TWO_outlet_%d", outlet),
                        String.format("Withdrawal Temperature - Outlet %d", outlet),
                        "T", "C", icol, ncol, outputFilename,
                        "outflow", "output", "",
                        "", "temperature");
                w2Parameter.setSegment(outputSegment);
                w2Parameters.add(w2Parameter);
                icol++;
            }

            // Constituents (iterate over all constituents, but only output active constituents
            if (CONSTITUENTS) {
                icol = 1; // Every qwo file contains at least one data column for the combined outflow
                List<W2Parameter> constituentW2Parameters = new ArrayList<>();
                outputFilename = String.format("cwo_%d.opt", outputSegment);
                location = String.format("Seg %d Withdrawal", outputSegment);
                constituentNames = activeConstituentsCard.getConstituentNames();
                List<String> constituentSettings = activeConstituentsCard.getCAC(); // Active/inactive status of constituents (ON/OFF)
                for (int jc = 0; jc < numConstituents; jc++) {
                    if (isOn(constituentSettings.get(jc))) {
                        W2Constituent w2Constituent = graphFileW2Constituents.get(jc);
                        w2Parameter = new W2Parameter(location, constituentNames.get(jc),
                                w2Constituent.getLongName(), constituentNames.get(jc),
                                w2Constituent.getUnits(), icol, 0, outputFilename,
                                "outflow", "output", "",
                                "", "constituent");
                        w2Parameter.setWaterBody(outputWaterbody);
                        w2Parameter.setSegment(outputSegment);
                        constituentW2Parameters.add(w2Parameter);
                        icol++;
                    }
                }

                final int numConstituentColumns = constituentW2Parameters.size();
                constituentW2Parameters.forEach(param -> param.setNumColumns(numConstituentColumns));
                w2Parameters.addAll(constituentW2Parameters);
            }

            if (DERIVED_CALC) {
                icol = 1;
                List<W2Parameter> derivedConstituentW2Parameters = new ArrayList<>();
                outputFilename = String.format("dwo_%d.opt", outputSegment);
                location = String.format("Seg %d Withdrawal", outputSegment);
                constituentNames = activeConstituentsCard.getConstituentNames();
                derivedConstituentNames = activeDerivedConstituentsCard.getConstituentNames();
//                List<String> derivedConstituentSettings = activeConstituentsCard.getCAC(); // Active/inactive status of constituents (ON/OFF)
                List<List<String>> derivedConstituentSettings = activeDerivedConstituentsCard.getStates();
                List<List<String>> states = activeDerivedConstituentsCard.getStates();
                for (int jc = 0; jc < numDerivedConstituents; jc++) {
                    if (isOn(derivedConstituentSettings.get(jc).get(outputWaterbody - 1))) {
                        W2Constituent w2Constituent = graphFileDerivedW2Constituents.get(jc);
                        w2Parameter = new W2Parameter(location, derivedConstituentNames.get(jc),
                                w2Constituent.getLongName(), derivedConstituentNames.get(jc),
                                w2Constituent.getUnits(), icol, 0, outputFilename,
                                "outflow", "output", "",
                                "", "derived constituent");
                        w2Parameter.setWaterBody(outputWaterbody);
                        w2Parameter.setSegment(outputSegment);
                        derivedConstituentW2Parameters.add(w2Parameter);
                        icol++;
                    }
                }

                final int numConstituentColumns = derivedConstituentW2Parameters.size();
                derivedConstituentW2Parameters.forEach(param -> param.setNumColumns(numConstituentColumns));
                w2Parameters.addAll(derivedConstituentW2Parameters);
            }

        }

        return w2Parameters;
    }


    /**
     * Determine output waterbody for the given output segment
     *
     * @param outputSegment  Output segment number
     * @param US  List of upstream segment for each branch
     * @param DS  List of downstream segment for each branch
     * @param BS  List of start branch for each waterbody
     * @param BE  List of end branch for each waterbody
     * @param numWaterbodies Number of waterbodies
     * @return Output waterbody index (one-based)
     */
    private int getOutputWaterbody(int outputSegment, List<Integer> US, List<Integer> DS,
                                   List<Integer> BS, List<Integer> BE, int numWaterbodies) {
        int outputWaterbody = 1;
        for (int i = 0; i < numWaterbodies; i++) {
            int bs = BS.get(i); // start branch of waterbody
            int be = BE.get(i); // end branch of waterbody
            int us = US.get(bs - 1); // upstream segment of start branch of waterbody
            int ds = DS.get(be - 1); // downstream segment of end branch of waterbody
            if (us <= outputSegment && outputSegment <= ds) {
                return outputWaterbody;
            }
            else {
                outputWaterbody++;
            }
        }
        return -99;
    }

    /**
     * Set the model output timestep
     *
     * @param interval    Output time interval
     * @param granularity Output granularity (days, hours, or seconds)
     */
    public void setOutputTimestep(double interval, W2Globals.Granularity granularity) {
        int NWDO = withdrawalOutputCard.getNWDO(); // Get number of withdrawal output dates
        withdrawalFrequencyCard = new RepeatingDoubleCard(w2con, "WITH FRE",
                NWDO, "%8.5f");
        String WDOC = "";
        List<Double> WDOF = new ArrayList<>();
        WDOF.add(interval);

        if (granularity == W2Globals.Granularity.DAYS) {
            WDOC = W2Globals.ON;
        }
        else if (granularity == W2Globals.Granularity.HOURS) {
            WDOC = W2Globals.ONH;
        }
        else if (granularity == W2Globals.Granularity.SECONDS) {
            WDOC = W2Globals.ONS;
        }
        withdrawalOutputCard.setWDOC(WDOC);
        withdrawalOutputCard.updateDataTable();
        withdrawalOutputCard.updateW2ControlFileList();
        withdrawalFrequencyCard.setValues(WDOF);
        withdrawalFrequencyCard.updateTable();
    }

    /**
     * Create output time series (TSR) Parameters
     * <p>
     * Outputs include:
     * DLT: Current time step (s)
     * ELWS: Water surface elevation for the cell's segment location (m)
     * T2: Temperature (Celsius)
     * U: Velocity (m/s) at the layer and segment specified
     * Q: Total (vertically integrated) flow rate through the entire segment (m3/s)
     * SRON: Net short wave solar radiation incident on the water surface (W/m^2, reflection is not included)
     * EXT: Light extinction coefficient (m^-1)
     * DEPTH: Depth from water surface to channel bottom (m)
     * WIDTH: Surface width (m)*
     * SHADE: Shade (shade factor multiplied by SRON)
     * if SHADE = 1 then no shade
     * if shade = 0 then no short wave solar reaches the water surface
     * ICETH: Ice thickness (if ice calculations are turned on) -- This isn't listed in output in the user's manual
     * Tvolavg: Vertically volume-weighted temperature at the specified model segment
     * NetRad: Net radiation at surface of segment (W/m^2)
     * SWSolar: Net short wave solar radiation at surface (W/m^2)
     * LWRad: Net long wave radiation at surface (W/m2)
     * BackRad: Back radiation at surface (W/m2)
     * EvapF: Evaporative heat flux at surface (W/m2)
     * ConducF: Conductive heat flux at surface (W/m2)
     * Active constituent concentrations (multiple columns)
     * Derived constituent concentrations (multiple columns)
     * Instantaneous kinetic flux rates (kg/day)
     * Instantaneous algae growth rate limitation fractions for P, N, and light [0 to 1] for each algal group
     * (if LIMC is turned on under the CST COMP card)
     *
     * @return List of TSR output parameters
     */
    private List<W2Parameter> fetchTsrOutputParameters() {
        List<W2Parameter> w2Parameters = new ArrayList<>();

        int NITSR = tsrPlotCard.getNITSR();
        tsrSegCard = new RepeatingIntegerCard(w2con, "TSR SEG", NITSR, "%8d");
        List<Integer> outputSegments = tsrSegCard.getValues();

        // Determine TSR filename prefix from the TSR filename card
        tsrFilenameCard = new W2FileCard(w2con, "TSR FILE", 1);
        String tsrBaseFilename = tsrFilenameCard.getFileNames(0);
        String tsrPrefix = tsrBaseFilename.substring(0, tsrBaseFilename.lastIndexOf('.'));

        tsrLayerCard = new RepeatingDoubleCard(w2con, "TSR LAYE", NITSR, "%8.5f");
        List<Double> ETSR = tsrLayerCard.getValues();

        // Get ice cover w2Parameters
        iceCoverCard = new IceCoverCard(w2con, NWB);
        List<String> ICEC = iceCoverCard.getICEC();

        W2Parameter w2Parameter;

        int paramIndex = 0;
        for (int i = 0; i < NITSR; i++) {
            int outputColumn = 1; // Output column
            int filenum = i + 1;
            int segment = outputSegments.get(i);
            String verticalLocation;
            double etsr = ETSR.get(i);

            if (etsr < 0) {
                verticalLocation = String.format("Layer %d", (int) Math.abs(etsr));
            }
            else {
                verticalLocation = String.format("Depth %.2f", etsr);
            }

            String tsrLocation = "TSR Seg " + segment + " " + verticalLocation;
            String tsrFilename = "tsr_" + filenum + "_seg" + segment + ".csv";

            // DLT: Current time step (s)
            w2Parameter = new W2Parameter(tsrLocation, "DLT", "Time Step",
                    "DLT", "s", outputColumn, 0, tsrFilename,
                    "outflow", "output", "", "",
                    "time");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // ELWS: Water surface elevation for the cell's segment location (m)
            w2Parameter = new W2Parameter(tsrLocation, "Elev",
                    "Water Surface Elevation", "ELWS", "m",
                    outputColumn, 0, tsrFilename, "outflow",
                    "output", "", "", "elevation");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // T2: Temperature (Celsius)
            // Note: this will be labeled Temp to ensure proper handling in DSS
            w2Parameter = new W2Parameter(tsrLocation, "Temp-Water",
                    "Water Temperature", "T2", "C", outputColumn,
                    0, tsrFilename, "outflow", "output", "",
                    "", "temperature");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // U: Velocity (m/s) at the layer and segment specified
            w2Parameter = new W2Parameter(tsrLocation, "U", "Velocity",
                    "U", "m/s", outputColumn, 0, tsrFilename, "outflow",
                    "output", "", "", "velocity");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // Q: Total (vertically integrated) flow rate through the entire segment (m3/s)
            // Note: this will be labeled Flow-Total to ensure proper handling in DSS
            w2Parameter = new W2Parameter(tsrLocation, "Flow-Total",
                    "Total Vertically Integrated Flow", "Q", "m^3/s",
                    outputColumn, 0, tsrFilename, "outflow",
                    "output", "", "", "flow");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // SRON: Net short wave solar radiation incident on the water surface (W/m^2, reflection is not included)
            w2Parameter = new W2Parameter(tsrLocation, "Rad-Shortwave",
                    "Net Shortwave Solar Radiation", "SRON", "W/m^2", outputColumn,
                    0, tsrFilename, "outflow", "output", "", "",
                    "meteorology");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // EXT: Light extinction coefficient (1/m)
            w2Parameter = new W2Parameter(tsrLocation, "Ext Coeff",
                    "Light Extinction Coefficient", "EXT", "1/m",
                    outputColumn, 0, tsrFilename, "outflow",
                    "output", "", "", "coefficient");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // DEPTH: Depth from water surface to channel bottom (m)
            // Note: this will be labeled Stage
            w2Parameter = new W2Parameter(tsrLocation, "Stage", "Depth",
                    "DEPTH", "m", outputColumn, 0, tsrFilename,
                    "outflow", "output", "", "", "depth");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // WIDTH: Surface width (m)*
            w2Parameter = new W2Parameter(tsrLocation, "Surface Width", "Surface Width",
                    "WIDTH", "m", outputColumn, 0, tsrFilename,
                    "outflow", "output", "", "", "width");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // SHADE: Shade (shade factor multiplied by SRON)
            //   if SHADE = 1 then no shade
            //   if shade = 0 then no short wave solar reaches the water surface
            w2Parameter = new W2Parameter(tsrLocation, "Shade", "Shade",
                    "SHADE", "", outputColumn, 0, tsrFilename,
                    "outflow", "output", "", "", "shade");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // Ice thickness, optional
            if (any(isOn(ICEC))) {
                w2Parameter = new W2Parameter(tsrLocation, "Ice Thickness",
                        "Ice Thickness", "ICETH", "m", outputColumn,
                        0, tsrFilename, "outflow", "output", "",
                        "", "thickness");
                w2Parameter.setVerticalLocation(verticalLocation);
                w2Parameter.setSegment(segment);
                w2Parameters.add(w2Parameter);
                outputColumn++;
                paramIndex++;
            }

            // Tvolavg: Vertically volume-weighted temperature at the specified model segment
            // TODO Check why parser.exe program has W/m^2 for the units
            w2Parameter = new W2Parameter(tsrLocation, "Temp-VolAvg",
                    "Vertically Volume-Weighted Temperature", "Tvolavg",
                    "C", outputColumn, 0, tsrFilename,
                    "outflow", "output", "", "",
                    "temperature");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // NetRad: Net radiation at surface of segment (W/m^2)
            w2Parameter = new W2Parameter(tsrLocation, "Rad-Net",
                    "Net Radiation", "NetRad", "C", outputColumn,
                    0, tsrFilename, "outflow", "output",
                    "", "", "meteorology");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // SWSolar: Net short wave solar radiation at surface (W/m^2)
            w2Parameter = new W2Parameter(tsrLocation, "Rad-Shortwave",
                    "Net Shortwave Solar Radiation", "SWSolar", "W/m^2",
                    outputColumn, 0, tsrFilename, "outflow",
                    "output", "", "", "meteorology");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // LWRad: Net long wave radiation at surface (W/m2)
            w2Parameter = new W2Parameter(tsrLocation, "Rad-Longwave",
                    "Net Longwave Radiation", "LWRad", "W/m^2", outputColumn,
                    0, tsrFilename, "outflow", "output", "",
                    "", "meteorology");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // BackRad: Back radiation at surface (W/m2)
            w2Parameter = new W2Parameter(tsrLocation, "Rad-Back",
                    "Back Radiation", "BackRad", "W/m^2", outputColumn,
                    0, tsrFilename, "outflow", "output",
                    "", "", "meteorology");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // EvapF: Evaporative heat flux at surface (W/m2)
            w2Parameter = new W2Parameter(tsrLocation, "Rad-Evaporative",
                    "Evaporative Heat Flux", "EvapF", "W/m^2",
                    outputColumn, 0, tsrFilename, "outflow",
                    "output", "", "", "meteorology");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // ConducF: Conductive heat flux at surface (W/m2)
            w2Parameter = new W2Parameter(tsrLocation, "Rad-Conductive",
                    "Conductive Heat Flux", "ConducF", "W/m^2",
                    outputColumn, 0, tsrFilename, "outflow",
                    "output", "", "", "meteorology");
            w2Parameter.setVerticalLocation(verticalLocation);
            w2Parameter.setSegment(segment);
            w2Parameters.add(w2Parameter);
            outputColumn++;
            paramIndex++;

            // TODO: Verify this code
            // Determine the waterbody where outputs will be created
            // Note: this logic is from PSU's parser.exe code. No comments
            // were provided, so it's possible this may be incorrect.
            int outputSegment = outputSegments.get(i); // outputSegment is the ITSR variable in TSR SEG card
            int outputWaterbody = 1;
            for (int jw = 0; jw < NWB; jw++) {
                // Get start and end branches of waterbody
                int startBranch = BS.get(jw);
                int endBranch = BE.get(jw);
                // Get upstream/downstream downstream segments of start/end branches of
                // the waterbody, respectively. Subtract one from branch numbers due to
                // zero-based indexing.
                int upstreamSegment = US.get(startBranch - 1);
                int downstreamSegment = DS.get(endBranch - 1);
                if (outputSegment >= upstreamSegment && outputSegment <= downstreamSegment) {
                    break;
                }
                outputWaterbody++;
            }

            // Constituents (iterate over all constituents, but only output active constituents
            constituentNames = activeConstituentsCard.getConstituentNames();
            // Active/inactive status of constituents (ON/OFF)
            List<String> constituentSettings = activeConstituentsCard.getCAC();
            //numActiveConstituents = count(isOn(constituentSettings));
            for (int jc = 0; jc < numConstituents; jc++) {
                if (isOn(constituentSettings.get(jc))) {
                    W2Constituent w2Constituent = graphFileW2Constituents.get(jc);
                    w2Parameter = new W2Parameter(tsrLocation, constituentNames.get(jc),
                            w2Constituent.getLongName(), constituentNames.get(jc),
                            w2Constituent.getUnits(), outputColumn, 0, tsrFilename,
                            "outflow", "output", "", "",
                            "constituent");
                    w2Parameter.setWaterBody(outputWaterbody);
                    w2Parameter.setVerticalLocation(verticalLocation);
                    w2Parameter.setSegment(segment);
                    w2Parameters.add(w2Parameter);
                    outputColumn++;
                    paramIndex++;
                }
            }

            // Epiphyton
            int numEpiphytonGroups = constituentDimensionsCard.getNEP();
            for (int je = 0; je < numEpiphytonGroups; je++) {
                int epiGroup = je + 1;
                String shortName = "EPI" + epiGroup;
                w2Parameter = new W2Parameter(tsrLocation, shortName, "Epiphython " + epiGroup,
                        shortName, "g/m^3", outputColumn, 0, tsrFilename,
                        "outflow", "output", "", "",
                        "epiphyton");
                w2Parameter.setWaterBody(outputWaterbody);
                w2Parameter.setVerticalLocation(verticalLocation);
                w2Parameter.setSegment(segment);
                w2Parameters.add(w2Parameter);
                outputColumn++;
                paramIndex++;
            }

            // Macrophytes
            int numMacrophytes = constituentDimensionsCard.getNMC();
            for (int jm = 0; jm < numMacrophytes; jm++) {
                int macroGroup = jm + 1;
                String shortName = "MAC" + macroGroup;
                w2Parameter = new W2Parameter(tsrLocation, shortName,
                        "Macrophytes" + macroGroup, shortName, "g/m^3", outputColumn,
                        0, tsrFilename, "outflow", "output",
                        "", "", "macrophytes");
                w2Parameter.setWaterBody(outputWaterbody);
                w2Parameter.setVerticalLocation(verticalLocation);
                w2Parameter.setSegment(segment);
                w2Parameters.add(w2Parameter);
                outputColumn++;
                paramIndex++;
            }

            // Sediment - if turned on
            if (SEDIMENT_CALC.get(outputWaterbody - 1)) {
                // SED: Sediment?? (g/m^3)
                w2Parameter = new W2Parameter(tsrLocation, "SED", "Sediment",
                        "Sediment", "g/m^3", outputColumn, 0, tsrFilename,
                        "outflow", "output", "", "",
                        "sediment-flow");
                w2Parameter.setVerticalLocation(verticalLocation);
                w2Parameter.setSegment(segment);
                w2Parameters.add(w2Parameter);
                outputColumn++;
                paramIndex++;

                // SEDP: Sediment Phosphorus (g/m^3)
                w2Parameter = new W2Parameter(tsrLocation, "SEDP", "Sediment Phosphorus",
                        "SEDP", "g/m^3", outputColumn, 0, tsrFilename,
                        "outflow", "output", "", "",
                        "sediment-constituent");
                w2Parameter.setVerticalLocation(verticalLocation);
                w2Parameter.setSegment(segment);
                w2Parameters.add(w2Parameter);
                outputColumn++;
                paramIndex++;

                // SEDN: Sediment Nitrogen (g/m^3)
                w2Parameter = new W2Parameter(tsrLocation, "SEDN", "Sediment Nitrogen",
                        "SEDN", "g/m^3", outputColumn, 0, tsrFilename,
                        "outflow", "output", "", "",
                        "sediment-constituent");
                w2Parameter.setVerticalLocation(verticalLocation);
                w2Parameter.setSegment(segment);
                w2Parameters.add(w2Parameter);
                outputColumn++;
                paramIndex++;

                // SEDC: Sediment Carbon (g/m^3)
                w2Parameter = new W2Parameter(tsrLocation, "SEDC", "Sediment Carbon",
                        "SEDC", "g/m^3", outputColumn, 0, tsrFilename,
                        "outflow", "output", "", "",
                        "sediment-constituent");
                w2Parameter.setVerticalLocation(verticalLocation);
                w2Parameter.setSegment(segment);
                w2Parameters.add(w2Parameter);
                outputColumn++;
                paramIndex++;
            }

            // Derived constituents (iterate over all derived constituents, but only output active constituents
            derivedConstituentNames = activeDerivedConstituentsCard.getConstituentNames();
            List<List<String>> derivedConstituentSettings = activeDerivedConstituentsCard.getStates();
            for (int jc = 0; jc < numDerivedConstituents; jc++) {
                if (isOn(derivedConstituentSettings.get(jc).get(outputWaterbody - 1))) {
                    W2Constituent w2Constituent = graphFileDerivedW2Constituents.get(jc);
                    w2Parameter = new W2Parameter(tsrLocation, derivedConstituentNames.get(jc),
                            w2Constituent.getLongName(), derivedConstituentNames.get(jc),
                            w2Constituent.getUnits(), outputColumn, 0, tsrFilename,
                            "outflow", "output", "", "",
                            "derived constituent");
                    w2Parameter.setWaterBody(outputWaterbody);
                    w2Parameter.setVerticalLocation(verticalLocation);
                    w2Parameter.setSegment(segment);
                    w2Parameters.add(w2Parameter);
                    outputColumn++;
                    paramIndex++;
                }
            }

            // Fluxes (iterate over all fluxes, but only output active fluxes
            List<String> fluxNames = activeConstituentFluxesCard.getConstituentNames();
//            List<List<String>> fluxSettings = activeConstituentFluxesCard.getValues();
            List<List<String>> fluxSettings = activeConstituentFluxesCard.getStates();
            for (int jc = 0; jc < NUM_FLUXES; jc++) {
                if (isOn(fluxSettings.get(jc).get(outputWaterbody - 1))) {
                    w2Parameter = new W2Parameter(tsrLocation, fluxNames.get(jc),
                            fluxNames.get(jc), fluxNames.get(jc), "kg/d", outputColumn,
                            0, tsrFilename, "outflow",
                            "output", "", "",
                            "flux");
                    w2Parameter.setWaterBody(outputWaterbody);
                    w2Parameter.setVerticalLocation(verticalLocation);
                    w2Parameter.setSegment(segment);
                    w2Parameters.add(w2Parameter);
                    outputColumn++;
                    paramIndex++;
                }
            }

//            // Add CO2GASX flux manually - I don't really understand this in the parser.exe source code
//            w2Parameter = new W2Parameter(tsrLocation, "CO2GASX", "CO2GASX", "kg/d",
//                    outputColumn, 0, tsrFilename, "outflow", "output");
//            w2Parameter.setWaterBody(outputWaterbody);
//            w2Parameter.setVerticalLocation(verticalLocation);
//            w2Parameter.setSegment(segment);
//            w2Parameters.add(w2Parameter);
//            outputColumn++;
//            paramIndex++;

            // Set total number of columns
            outputColumn -= 1; // correct for the extra increment in the flux loop above
            int numColumns = outputColumn;
            int istart = paramIndex - numColumns;
            int iend = paramIndex;
            for (int ii = istart; ii < iend; ii++) {
                w2Parameters.get(ii).setNumColumns(numColumns);
            }
        }
        return w2Parameters;
    }

    private void handleVerticalProfiles() {
        // TODO Not implemented
    }

    private void handleLongitudinalProfiles() {
        // TODO Not implemented
    }

    private void handleHabitatVolume() {
        // TODO Not implemented
    }

    /**
     * Save the changes to the CE-QUAL-W2 file
     *
     * @param outfile CE-QUAL-W2 control filename
     */
    public void saveControlFile(String outfile) {
        w2con.save(outfile);
    }

    /**
     * Read initial water temperatures from the initial conditions card
     *
     * @return List of initial water temperatures, one per water body
     */
    private List<Double> fetchInitialTemperatures() {
        initCondCard = new InitialConditionsCard(w2con, NWB);
        return initCondCard.getT2I();
    }

    /**
     * Read constituent names obtained from the graph.npt flie
     *
     * @return List of constituent names (i.e., "long" names)
     */
    private List<String> fetchConstituentNames() {
        List<String> ConstituentNames = new ArrayList<>();
        for (W2Constituent c : graphFileW2Constituents) {
            ConstituentNames.add(c.getLongName());
        }
        return ConstituentNames;
    }

    /**
     * Read initial constituent concentrations from the CST ICON card
     *
     * @return List of initial constituent concentrations, one list per water body
     */
    private List<List<Double>> fetchInitialConcentrations() {
        constituentICcard = new MultiRecordRepeatingDoubleCard(w2con,
                "CST ICON", numConstituents, NWB);
        return constituentICcard.getValues();
    }

    /**
     * Write initial water surface elevations fetched from the bathymetery file(s)
     * to a table in a separate output file, e.g., a log file.
     *
     * @param outfileName Output filename
     */
    public void writeInitialWaterSurfaceElevations(String outfileName) {
        List<String> lines = new ArrayList<>();
        for (int jwb = 0; jwb < NWB; jwb++) {
            List<Double> Elevations = initialWaterSurfaceElevations.get(jwb);
            StringBuilder line = new StringBuilder(String.format("%-8s", "WB" + (jwb + 1)));
            for (Double Elevation : Elevations) {
                line.append(String.format("%-8.3f", Elevation));
            }
            lines.add(line.toString());
        }

        Path file = Paths.get(outfileName);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Helper methods to create Location Names. If a location needs to be renamed,
    // it should be renamed in using the functions below.

    private String branchInflowLocationName(int branch) {
        return "Branch " + branch + " Inflow";
    }

    private String tributaryLocationName(int tributary, int segment) {
        return "Tributary " + tributary + " Segment " + segment;
    }

    private String latWithdrawalLocationName(int segment, int withdrawal) {
        return "Segment " + segment + " Lat Withdrawal " + withdrawal;
    }

    private String precipLocationName(int branch) {
        return "Branch " + branch + " Precip";
    }

    private String metLocationName(int waterbody) {
        return "WB " + waterbody + " Met";
    }

    /**
     * Create list of input parameters
     */
    private List<W2Parameter> fetchInputParameters() {
        // Assemble lists of input and output w2Parameters. Each parameter will contain:
        // location, parameter name (short name), description (long name), units
        // column number, total columns in file, and filename
        List<W2Parameter> w2Parameters = new ArrayList<>();

        // ------------------------------------------------------------------------------------
        // Assemble list of input w2Parameters (inflows and outflows)
        // ------------------------------------------------------------------------------------

        // Branch Inflows, QIN
        qinCard = new W2FileCard(w2con, W2CardNames.BranchInflowFilenames, NBR);
        for (int jbr = 0; jbr < NBR; jbr++) {
            if (UHS.get(jbr) == 0) {
                int branch = jbr + 1;
                String ioName = "qin_br" + branch;
                String location = "Branch " + branch + " Inflow";
                W2Parameter w2Parameter = new W2Parameter(location,
                        "Flow-QIN", "Upstream Branch Inflow",
                        "QIN", "m^3/s", 1, 1,
                        qinCard.getFileNames().get(jbr),
                        "inflow", "input",
                        ioName, "upstream branch inflow", "flow");
                w2Parameters.add(w2Parameter);
            }
        }

        // Branch Inflow Temperature, TIN
        tinCard = new W2FileCard(w2con, W2CardNames.BranchInflowTemperatureFilenames, NBR);
        for (int jbr = 0; jbr < NBR; jbr++) {
            if (UHS.get(jbr) == 0) {
                int branch = jbr + 1;
                String ioName = "tin_br" + branch;
                String location = branchInflowLocationName(branch);
                W2Parameter w2Parameter = new W2Parameter(location, "Temp-TIN",
                        "Temperature", "TIN", "C", 1,
                        1, tinCard.getFileNames().get(jbr), "inflow",
                        "input", ioName, "distributed tributary", "temperature");
                w2Parameters.add(w2Parameter);
            }
        }

        // Distributed Tributary Inflows, QDT
        dstTribCard = new DistributedTributariesCard(w2con, NBR);
        DTRC = dstTribCard.getStates();
        qdtCard = new W2FileCard(w2con, W2CardNames.DistributedTributaryInflowFilenames, NBR);
        for (int jbr = 0; jbr < NBR; jbr++) {
            if (isOn(DTRC.get(jbr))) {
                int branch = jbr + 1;
                String ioName = "qdt_br" + branch;
                String location = "Branch " + branch + " Distr Trib";
                W2Parameter w2Parameter = new W2Parameter(location, "Flow-QDT",
                        "Distributed Tributary Inflow", "QDT", "m^3/s",
                        1, 1, qdtCard.getFileNames().get(jbr),
                        "inflow","input", ioName,
                        "distributed tributary", "flow");
                w2Parameters.add(w2Parameter);
            }
        }

        // Distributed Tributary Inflow Temperature, TDT
        tdtCard = new W2FileCard(w2con,
                W2CardNames.DistributedTributaryInflowTemperatureFilenames, NBR);
        for (int jbr = 0; jbr < NBR; jbr++) {
            if (isOn(DTRC.get(jbr))) {
                int branch = jbr + 1;
                String ioName = "tdt_br" + branch;
                String location = "Branch " + branch + " Distr Trib";
                W2Parameter w2Parameter = new W2Parameter(location, "Temp-TDT",
                        "Temperature", "TDT", "C", 1,
                        1, tdtCard.getFileNames().get(jbr), "inflow",
                        "input", ioName, "distributed tributary",
                        "temperature");
                w2Parameters.add(w2Parameter);
            }
        }

        // Tributary Inflows, QTR
//        YYYYYYYYYYYYYY;
        qtrCard = new W2FileCard(w2con, W2CardNames.TributaryInflowFilenames, NTR);
        for (int jtr = 0; jtr < NTR; jtr++) {
            int tributary = jtr + 1;
            String location = tributaryLocationName(tributary, tributarySegmentCard.getSegments().get(jtr));
            String ioName = String.format("qtr_%d_seg%d", tributary, tributarySegmentCard.getSegments().get(jtr));
            W2Parameter w2Parameter = new W2Parameter(location,
                    "Flow-QTR", "Tributary Inflow", "QTR", "m^3/s",
                    1, 1, qtrCard.getFileNames().get(jtr),
                    "inflow", "input", ioName, "tributary",
                    "flow");
            w2Parameters.add(w2Parameter);
        }

        // Tributary Inflow Temperature, TTR
        ttrCard = new W2FileCard(w2con, W2CardNames.TributaryInflowTemperatureFilenames, NTR);
        for (int jtr = 0; jtr < NTR; jtr++) {
            int tributary = jtr + 1;
            int segment = tributarySegmentCard.getSegments().get(jtr);
            String ioName = String.format("ttr_%d_seg%d", tributary, segment);
            String location = tributaryLocationName(tributary, segment);
            W2Parameter w2Parameter = new W2Parameter(location, "Temp-TTR",
                    "Temperature", "TTR", "C", 1,
                    1, ttrCard.getFileNames().get(jtr), "inflow",
                    "input", ioName, "tributary", "temperature");
            w2Parameters.add(w2Parameter);
        }

        // Lateral Withdrawals (outflows), QWD
        qwdCard = new W2FileCard(w2con, W2CardNames.WithdrawalFilenames, 1);
        String qwdFileName = qwdCard.getFileNames().get(0);
        for (int jwd = 0; jwd < NWD; jwd++) {
            int segment = IWD.get(jwd);
            int withdrawal = jwd + 1;
            String ioName = String.format("wd_%d_seg%d", withdrawal, segment);
            W2Parameter w2Parameter = new W2Parameter(latWithdrawalLocationName(segment, withdrawal),
                    "Flow-QWD", "Lateral Withdrawal", "QWD", "m^3/s",
                    withdrawal, NWD, qwdFileName, "outflow", "input", ioName,
                    "lateral withdrawal", "flow");
            w2Parameters.add(w2Parameter);
        }

        // Precipitation (inflow)
        calculationsCard = new CalculationsCard(w2con, NWB);
        List<String> PRC = calculationsCard.getPRC();
        for (int jwb = 0; jwb < NWB; jwb++) {
            if (isOn(PRC.get(jwb))) {
                int waterbody = jwb + 1;
                for (int branch = BS.get(jwb); branch <= BE.get(jwb); branch++) {
                    String ioName = String.format("precip_br%d", branch);
                    String location = precipLocationName(branch);
                    W2Parameter w2Parameter = new W2Parameter(location, "Precip",
                            "Precipitation", "precipitation", "m/s", branch,
                            NWD, qwdFileName, "outflow", "input", ioName,
                            "precipitation", "precip-flow");
                    w2Parameters.add(w2Parameter);
                }
            }
        }

        // Precipitation Temperature (inflow)
        // Iterate over water bodies
        for (int jwb = 0; jwb < NWB; jwb++) {
            if (isOn(PRC.get(jwb))) {
                int waterbody = jwb + 1;
                for (int branch = BS.get(jwb); branch <= BE.get(jwb); branch++) {
                    String ioName = String.format("tprecip_br%d", branch);
                    String location = precipLocationName(branch);
                    W2Parameter w2Parameter = new W2Parameter(location, "Temp-TPR",
                            "Precipitation Temperature", "temperature", "C",
                            branch, NWD, qwdFileName, "outflow", "input",
                            ioName, "precipitation temperature", "precip-temperature");
                    w2Parameters.add(w2Parameter);
                }
            }
        }

        // Structural Withdrawals (outflows), QOT
        qotCard = new W2FileCard(w2con, W2CardNames.StructuralWithdrawalFilenames, NBR);
        numberStructuresCard = new NumberStructuresCard(w2con, NBR);
        List<Integer> NSTR = numberStructuresCard.getNSTR();
        for (int jbr = 0; jbr < NBR; jbr++) {
            int branch = jbr + 1;
            String qotFileName = qotCard.getFileNames().get(jbr);
            for (int structure = 1; structure <= NSTR.get(jbr); structure++) {
                String ioName = String.format("str_br%d_%d", branch, structure);
                String location = "Branch " + branch + " Struct Withdrawal " + structure;
                W2Parameter w2Parameter = new W2Parameter(location, "Flow-QOT",
                        "Structural Withdrawal", "QOT", "m^3/s",
                        structure, NSTR.get(jbr), qotFileName, "outflow",
                        "input", ioName, "structural withdrawal",
                        "flow");
                w2Parameters.add(w2Parameter);
            }
        }

        //-----------------------------------------------------------------------------------------------------
        // Constituents
        //-----------------------------------------------------------------------------------------------------

        cinCard = new W2FileCard(w2con, W2CardNames.BranchInflowConcentrationFilenames, NBR);

        // Branch Inflow Concentrations
        branchConstituentsCard = new BranchConstituentsCard(w2con, numConstituents, NBR);
        List<String> branchInflowConstituentNames = branchConstituentsCard.getConstituentNames();
        List<List<String>> CINBRC = branchConstituentsCard.getStates();

        if (CONSTITUENTS) {
            // Iterate over branches
            for (int jbr = 0; jbr < NBR; jbr++) {
                if (UHS.get(jbr) == 0) {
                    int numColumns = computeNumberConstituentColumns(CINBRC, jbr, numConstituents);
                    int branch = jbr + 1;

                    int icol = 1;
                    for (int jc = 0; jc < numConstituents; jc++) {
                        if (isOn(CINBRC.get(jc).get(jbr))) {
                            W2Constituent graphFileW2Constituent = graphFileW2Constituents.get(jc);
                            String location = branchInflowLocationName(branch);
                            String shortName = branchInflowConstituentNames.get(jc) + "-CIN";
                            String w2Name = branchInflowConstituentNames.get(jc);
                            String ioName = "";
                            W2Parameter w2Parameter = new W2Parameter(location, shortName,
                                    graphFileW2Constituent.getLongName(), w2Name,
                                    graphFileW2Constituent.getUnits(), icol, numColumns,
                                    cinCard.getFileNames().get(jbr), "inflow",
                                    "input", ioName, "", "constituent");
                            w2Parameters.add(w2Parameter);
                            icol++;
                        }
                    }
                }
            }
        }

        // Distributed Tributary Inflow Concentrations
        distributedTributaryConstituentsCard = new DistributedTributaryConstituentsCard(w2con, numConstituents, NBR);
        List<String> distributedTributaryConstituentNames = distributedTributaryConstituentsCard.getConstituentNames();
        List<List<String>> CDTBRC = distributedTributaryConstituentsCard.getStates();
        cdtCard = new W2FileCard(w2con,
                W2CardNames.DistributedTributaryInflowConcentrationFilenames, NBR);
        if (CONSTITUENTS) {
            // Iterate over branches
            for (int jbr = 0; jbr < NBR; jbr++) {
                if (isOn(DTRC.get(jbr))) {
                    int numColumns = computeNumberConstituentColumns(CDTBRC, jbr,
                            numConstituents);
                    int branch = jbr + 1;

                    int icol = 1;
                    for (int jc = 0; jc < numConstituents; jc++) {
                        if (isOn(CDTBRC.get(jc).get(jbr))) {
                            String location = "Branch " + branch + " Distr Trib";
                            String shortName = distributedTributaryConstituentNames.get(jc) + "-CDT";
                            String w2Name = distributedTributaryConstituentNames.get(jc);
                            String ioName = "";
                            W2Constituent graphFileW2Constituent = graphFileW2Constituents.get(jc);
                            W2Parameter w2Parameter = new W2Parameter(location, shortName,
                                    graphFileW2Constituent.getLongName(), w2Name,
                                    graphFileW2Constituent.getUnits(), icol, numColumns,
                                    cdtCard.getFileNames().get(jbr), "inflow", "input",
                                    ioName, "", "constituent");
                            w2Parameters.add(w2Parameter);
                            icol++;
                        }
                    }
                }
            }
        }

        // Tributary Inflow Concentrations
        if (CONSTITUENTS && NTR > 0) {
            tributaryConstituentsCard = new TributaryConstituentsCard(w2con, numConstituents, NTR);
            List<String> tributaryConstituentNames = tributaryConstituentsCard.getConstituentNames();
            List<List<String>> CTRBRC = tributaryConstituentsCard.getStates();
            ctrCard = new W2FileCard(w2con,
                    W2CardNames.TributaryInflowConcentrationFilenames, NTR);
            // Iterate over tributaries
            for (int jtr = 0; jtr < NTR; jtr++) {
                int numColumns = computeNumberConstituentColumns(CTRBRC, jtr, numConstituents);
                int tributary = jtr + 1;
                int icol = 1;
                for (int jc = 0; jc < numConstituents; jc++) {
                    if (isOn(CTRBRC.get(jc).get(jtr))) {
                        int segment = tributarySegmentCard.getSegments().get(jtr);
                        String ioName = String.format("ctr_%d_seg%d", tributary, segment);
                        String location = tributaryLocationName(tributary, segment);
                        String shortName = tributaryConstituentNames.get(jc) + "-CTR";
                        String w2Name = tributaryConstituentNames.get(jc);
                        W2Constituent graphFileW2Constituent = graphFileW2Constituents.get(jc);
                        W2Parameter w2Parameter =
                                new W2Parameter(location,
                                        shortName,
                                        graphFileW2Constituent.getLongName(),
                                        w2Name, graphFileW2Constituent.getUnits(), icol, numColumns,
                                        ctrCard.getFileNames().get(jtr),
                                        "inflow", "input", ioName, "tributary",
                                        "constituent");
                        w2Parameters.add(w2Parameter);
                        icol++;
                    }
                }
            }
        }

        // Precipitation Inflow Concentration
        precipConstituentsCard = new PrecipConstituentsCard(w2con, numConstituents, NBR);
        List<String> precipConstituentNames = precipConstituentsCard.getConstituentNames();
        List<List<String>> CPRBRC = precipConstituentsCard.getStates();
        cprCard = new W2FileCard(w2con, "CPR FILE", NBR);
        if (CONSTITUENTS) {
            for (int jwb = 0; jwb < NWB; jwb++) {
                if (isOn(PRC.get(jwb))) {
                    int waterbody = jwb + 1;
                    for (int branch = BS.get(jwb); branch <= BE.get(jwb); branch++) {
                        int numColumns = computeNumberConstituentColumns(CPRBRC, jwb,
                                numConstituents);
                        int icol = 1;
                        for (int jc = 0; jc < numConstituents; jc++) {
                            if (isOn(CPRBRC.get(jc).get(jwb))) {
                                String ioName = String.format("cprecip_br_%d", branch);
                                String location = precipLocationName(branch);
                                String shortName = precipConstituentNames.get(jc) + "-CPR";
                                String w2Name = precipConstituentNames.get(jc);
                                W2Constituent graphFileW2Constituent =
                                        graphFileW2Constituents.get(jc);
                                W2Parameter w2Parameter = new W2Parameter(location, shortName,
                                        graphFileW2Constituent.getLongName(), w2Name,
                                        graphFileW2Constituent.getUnits(), icol, numColumns,
                                        cprCard.getFileNames().get(branch - 1),
                                        "inflow", "input", ioName,
                                        "precip", "precip-flow");
                                w2Parameters.add(w2Parameter);
                                icol++;
                            }
                        }
                    }
                }
            }
        }
        return w2Parameters;
    }

    /**
     * Retrieve meteorology input parameters
     */
    private List<W2Parameter> fetchMeteorologyInputs() {
        List<W2Parameter> w2Parameters = new ArrayList<>();
        heatExchangeCard = new HeatExchangeCard(w2con, NWB);
        List<String> SROC = heatExchangeCard.getSROC();

        metCard = new W2FileCard(w2con, "MET FILE", NWB);
        for (int jw = 0; jw < NWB; jw++) {
            int waterbody = jw + 1;
            String sroc = SROC.get(jw);
            int nColumns;
            if (isOn(sroc)) {
                nColumns = 6;
            }
            else {
                nColumns = 5;
            }
            String location =  "WB " + waterbody + " Met";
            String ioName = String.format("met_wb%d", waterbody);
            String ioType = "meteorology";
            W2Parameter w2Parameter = new W2Parameter(location, "TAIR",
                    "Air Temperature", "air temperature",
                    "C", 1, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input", ioName, ioType, "meteorology");
            w2Parameters.add(w2Parameter);
            w2Parameter = new W2Parameter(location, "TDEW",
                    "Dew Point Temperature", "dew point temperature",
                    "C", 2, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input", ioName, ioType, "meteorology");
            w2Parameters.add(w2Parameter);
            w2Parameter = new W2Parameter(location, "WS",
                    "Wind Speed", "wind speed", "m/s",
                    3, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input", ioName, ioType, "meteorology");
            w2Parameters.add(w2Parameter);
            w2Parameter = new W2Parameter(location, "PHI",
                    "Wind Direction", "wind direction", "radians",
                    4, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input", ioName, ioType, "meteorology");
            w2Parameters.add(w2Parameter);
            w2Parameter = new W2Parameter(location, "Cloud",
                    "Cloud Cover", "cloud cover", "0-10",
                    5, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input", ioName, ioType, "meteorology");
            w2Parameters.add(w2Parameter);
            if (isOn(sroc)) {
                w2Parameter = new W2Parameter(location, "SRO",
                        "Shortwave Solar Radiation", "short wave solar radiation", "W/m^2",
                        6, nColumns, metCard.getFileNames().get(jw),
                        "inflow", "input", ioName, ioType, "meteorology");
                w2Parameters.add(w2Parameter);
            }
        }
        return w2Parameters;
    }

//    /**
//     * Compute number of constituents
//     *
//     * This method encapsulates the calculations from PSU's original parser.exe program for HEC-WAT.
//     * However, it is not currently needed to compute the number of constituents, NCT. The new parser
//     * uses the number of constituents in the graph.npt file.
//     */
//    private int computeNumberOfConstituents() {
//        InflowOutflowCard inOutflowCard = new InflowOutflowCard(w2con);
//        ConstituentDimensionsCard cdCard = new ConstituentDimensionsCard(w2con);
//        int NSP = inOutflowCard.getNumSpillways();
//        int NPI = inOutflowCard.getNumPipes();
//        int NPU = inOutflowCard.getNumPumps();
//        int NGC = cdCard.getNGC();
//        int NSS = cdCard.getNSS();
//        int NAL = cdCard.getNAL();
//        int NEP = cdCard.getNEP();
//        int NBOD = cdCard.getNBOD();
//        int NMC = cdCard.getNMC();
//        int NZP = cdCard.getNZP();
//
//        // Defined below
//        int NBODNS;
//        int NBODNE;
//        int NBODPS;
//        int NBODPE;
//        int NBODCS;
//        int NBODCE;
//
//        int NTDS  = 1;
//        int NGCS  = 2;
//        int NGCE  = NGCS + NGC - 1;
//        int NSSS  = NGCE + 1;
//        int NSSE  = NSSS + NSS - 1;
//        int NPO4  = NSSE + 1;
//        int NNH4  = NPO4 + 1;
//        int NNO3  = NNH4 + 1;
//        int NDSI  = NNO3 + 1;
//        int NPSI  = NDSI + 1;
//        int NFE   = NPSI + 1;
//        int NLDOM = NFE + 1;
//        int NRDOM = NLDOM + 1;
//        int NLPOM = NRDOM + 1;
//        int NRPOM = NLPOM + 1;
//        int NBODS = NRPOM + 1;
//
//        // Variable stoichiometry for CBOD
//        if (NBOD > 0) {
//            List<Integer> NBODC = new ArrayList<>();
//            List<Integer> NBODP = new ArrayList<>();
//            List<Integer> NBODN = new ArrayList<>();
//            int IBOD = NBODS;
//            NBODCS = IBOD;
//            for (int jcb = 0; jcb < NBOD; jcb++) {
//                NBODC.set(jcb, IBOD);
//                IBOD += 1;
//            }
//
//            NBODCE = IBOD - 1;
//            NBODPS = IBOD;
//
//            for (int jcb = 0; jcb < NBOD; jcb++) {
//                NBODP.set(jcb, IBOD);
//                IBOD += 1;
//            }
//
//            NBODPE = IBOD - 1;
//            NBODNS = IBOD;
//
//            for (int jcb = 0; jcb < NBOD; jcb++) {
//                NBODN.set(jcb, IBOD);
//                IBOD += 1;
//            }
//
//            NBODNE = IBOD - 1;
//        } else {
//            NBODNS = 1;
//            NBODNE = 1;
//            NBODPS = 1;
//            NBODPE = 1;
//            NBODCS = 1;
//            NBODCE = 1;
//        }
//
//        int NBODE = NBODS + NBOD * 3 - 1;
//        int NAS   = NBODE + 1;
//        int NAE   = NAS + NAL - 1;
//        int NDO   = NAE + 1;
//        int NTIC  = NDO + 1;
//        int NALK  = NTIC + 1;
//
//        // v3.5 start
//        int NZOOS = NALK + 1;
//        int NZOOE = NZOOS + NZP - 1;
//        int NLDOMP = NZOOE + 1;
//        int NRDOMP = NLDOMP + 1;
//        int NLPOMP = NRDOMP + 1;
//        int NRPOMP = NLPOMP + 1;
//        int NLDOMN = NRPOMP + 1;
//        int NRDOMN = NLDOMN + 1;
//        int NLPOMN = NRDOMN + 1;
//        int NRPOMN = NLPOMN + 1;
//        int NCT = NRPOMN; // ****** This is the key value we need: the number of constituents
//        // v3.5 end
//
//        // W2Constituent, tributary, and withdrawal totals
//        int NTRT = NTR + NGT + NSP + NPI + NPU;
//        int NWDT = NWD + NGT + NSP + NPI + NPU;
//        int NEPT = Math.max(NEP, 1);
//        int NMCT = Math.max(NMC, 1);
//        int NZPT = Math.max(NZP, 1);
//
//        return NCT;
//    }

    /**
     * Update the CE-QUAL-W2 simulation time window
     *
     * @param jdayMin   Start time
     * @param jdayMax   End time
     * @param startYear Start year
     */
    public void setTimeWindow(double jdayMin, double jdayMax, int startYear) {
        this.jdayMin = jdayMin;
        this.jdayMax = jdayMax;
        this.startYear = startYear;
    }

    /**
     * Create a formatted table of w2Parameters for writing to file or console
     *
     * @param w2Parameters List of w2Parameters
     * @return List of formatted output lines
     */
    private List<String> createTable(List<W2Parameter> w2Parameters) {
        List<String> outputList = new ArrayList<>();
        String headerFormat = "%-28s%-17s%-42s%-10s%-8s%-10s%-46s%-15s%-13s%-15s%-25s%-22s%-22s";
        String dataFormat = "%-28s%-17s%-42s%-10s%-8d%-10d%-46s%-15s%-13s%-15s%-25s%-22s%-22s";
        String header = String.format(headerFormat, "Location", "Short Name",
                "Long Name", "Units", "Column", "#Columns", "Filename", "Inflow/Outflow",
                "Input/Output", "IO Name", "IO Type", "W2 Name", "Param Type");
        outputList.add(header);
        StringBuilder horizontalBar = new StringBuilder();
        for (int i = 0; i < header.length(); i++) {
            horizontalBar.append("-");
        }
        outputList.add(horizontalBar.toString());

        for (W2Parameter p : w2Parameters) {
            String line = String.format(dataFormat, p.getLocation(), p.getShortName(),
                    p.getLongName(), p.getUnits(), p.getColumnNumber(), p.getNumColumns(),
                    p.getFileName(), p.getInflowOutflow(), p.getInputOutput(),
                    p.getIoName(), p.getIoType(), p.getW2Name(), p.getParamType());
            outputList.add(line);
        }
        return outputList;
    }

    /**
     * Print a table of w2Parameters to the console
     *
     * @param w2Parameters List of w2Parameters
     */
    public void printTable(List<W2Parameter> w2Parameters) {
        for (String line : createTable(w2Parameters)) {
            System.out.println(line);
        }
    }

    /**
     * Write a table of w2Parameters
     *
     * @param w2Parameters List of w2Parameters
     * @param outfileName  Output filename
     */
    public void writeTable(List<W2Parameter> w2Parameters, String outfileName) {
        List<String> lines = createTable(w2Parameters);
        Path file = Paths.get(outfileName);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compute number of columns in a constituent file
     *
     * @param constituentSettings Specifies if a parameter is turned on or off
     * @param column              Column in the settings record in the control file. This typically corresponds to a branch.
     * @param numConstituents     Number of constituents in the model
     * @return Number of columns in the constituent file for the given column (e.g., branch)
     */
    private int computeNumberConstituentColumns(List<List<String>> constituentSettings,
                                                int column, int numConstituents) {
        // Iterate over constituents then over the branches, tributaries, etc.
        int numColumns = 0;
        for (int i = 0; i < numConstituents; i++) {
            if (isOn(constituentSettings.get(i).get(column))) {
                numColumns++;
            }
        }
        return numColumns;
    }

    /**
     * Retrieve initial water surface elevations from the bathymetry file
     * TODO Add support for multiple water bodies
     */
    private List<List<Double>> fetchInitialWaterSurfaceElevations() {
        bathyFileCard = new W2FileCard(w2con, "BTH FILE", NWB);
        List<List<Double>> waterSurfaceElevations = new ArrayList<>();
        for (int jwb = 0; jwb < NWB; jwb++) {
            String bathyFilename = bathyFileCard.getFileNames(jwb);
            try {
                W2BathymetryFile bathyFile = new W2BathymetryFile(w2con, bathyFilename, KMX);
                List<Double> ELWS = bathyFile.getELWS().getValues();
                waterSurfaceElevations.add(ELWS);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return waterSurfaceElevations;
    }

    /**
     * Return a list of model input parameters
     *
     * @return List of model input parameters
     */
    public List<W2Parameter> getInputW2Parameters() {
        return this.inputW2Parameters;
    }

    /**
     * Return a list of meteorology parameters
     *
     * @return List of meteorology parameters
     */
    public List<W2Parameter> getMeteorologyW2Parameters() {
        return this.meteorologyW2Parameters;
    }

    /**
     * Return a list of TSR (time series) output parameters
     *
     * @return List of TSR output parameters
     */
    public List<W2Parameter> getTsrOutputW2Parameters() {
        return tsrOutputW2Parameters;
    }

    /**
     * Return a list of withdrawal output parameters
     *
     * @return List of withdrawal output parameters
     */
    public List<W2Parameter> getWithdrawalOutputW2Parameters() {
        return withdrawalOutputW2Parameters;
    }

    /**
     * Return a list of initial water temperatures
     *
     * @return List of initial water temperatures, one list per water body
     */
    public List<Double> getInitialTemperatures() {
        return initialTemperatures;
    }

    /**
     * Return a list of initial concentrations
     *
     * @return List of initial concentrations, one list per water body
     */
    public List<List<Double>> getInitialConcentrations() {
        return initialConcentrations;
    }

    /**
     * Return a list of constituent names, as they appear in the graph.npt file
     *
     * @return List of constituent names
     */
    public List<String> getConstituentNames() {
        return constituentNames;
    }

    /**
     * Return a list of initial water surface elevations
     *
     * @return List of water surface elevations. The list
     * contains a list of water surface elevations
     * for each waterbody.
     */
    public List<List<Double>> getInitialWaterSurfaceElevations() {
        return initialWaterSurfaceElevations;
    }

    /**
     * Get Julian start day of simulation
     *
     * @return Julian start day
     */
    public double getJdayMin() {
        return jdayMin;
    }

    /**
     * Set Julian start day of simulation
     *
     * @param jdayMin Julian start day
     */
    public void setJdayMin(double jdayMin) {
        this.jdayMin = jdayMin;
        timeControlCard.setStartDay(jdayMin);
        timeControlCard.updateDataTable();
        timeControlCard.updateW2ControlFileList();
    }

    /**
     * Get Julian end day of simulation
     *
     * @return Julian end day
     */
    public double getJdayMax() {
        return jdayMax;
    }

    /**
     * Set Julian end day of simulation
     *
     * @param jdayMax Julian end day
     */
    public void setJdayMax(double jdayMax) {
        this.jdayMax = jdayMax;
        timeControlCard.setEndDay(jdayMax);
        timeControlCard.updateDataTable();
        timeControlCard.updateW2ControlFileList();
    }

    /**
     * Get start year of simulation
     *
     * @return Start year of simulation
     */
    public int getStartYear() {
        return startYear;
    }

    /**
     * Set start year of simulation
     *
     * @param startYear Start year of simulation
     */
    public void setStartYear(int startYear) {
        this.startYear = startYear;
        timeControlCard.setStartYear(startYear);
        timeControlCard.updateDataTable();
        timeControlCard.updateW2ControlFileList();
    }

    /**
     * Return number of branches
     *
     * @return Number of branches
     */
    public int getNumBranches() {
        return NBR;
    }

    /**
     * Return number of water bodies
     *
     * @return Number of water bodies
     */
    public int getNumWaterbodies() {
        return NWB;
    }

    /**
     * Return number of segments
     *
     * @return Number of segments
     */
    public int getNumSegments() {
        return IMX;
    }

    /**
     * Return number of layers
     *
     * @return Number of layers
     */
    public int getNumLayers() {
        return KMX;
    }

    /**
     * Return number of tributaries
     *
     * @return Number of tributaries
     */
    public int getNumTributaries() {
        return NTR;
    }

    /**
     * Return number of structures
     *
     * @return Number of structures
     */
    public int getNumStructures() {
        return NST;
    }

    /**
     * Return number of withdrawals
     *
     * @return Number of withdrawals
     */
    public int getNumWithdrawals() {
        return NWD;
    }

    /**
     * Return number of gates
     *
     * @return Number of gates
     */
    public int getNumGates() {
        return NGT;
    }

    /**
     * Return number of constituents
     *
     * @return Number of constituents
     */
    public int getNumConstituents() {
        return numConstituents;
    }

    /**
     * Determine if value is "ON"
     *
     * @param value "ON" or "OFF"
     * @return True if value is "ON"
     */
    private boolean isOn(String value) {
        if (value.equalsIgnoreCase(W2Globals.ON) ||
                value.equalsIgnoreCase(W2Globals.ONH) ||
                value.equalsIgnoreCase(W2Globals.ONS)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determine if each value in a list is "ON"
     *
     * @param values List of values ("ON" or "OFF")
     * @return List of booleans: True if a value is "ON"
     */
    private List<Boolean> isOn(List<String> values) {
        List<Boolean> result = new ArrayList<>();
        values.forEach(value -> result.add(isOn(value)));
        return result;
    }

    /**
     * Determine if value is "OFF"
     *
     * @param value "ON" or "OFF"
     * @return True if value is "OFF"
     */
    private boolean isOff(String value) {
        return value.equalsIgnoreCase(W2Globals.OFF);
    }

    /**
     * Determine if a value is "OFF"
     *
     * @param values List of values ("ON" or "OFF")
     * @return List of booleans: True if value is "OFF"
     */
    private List<Boolean> isOff(List<String> values) {
        List<Boolean> result = new ArrayList<>();
        values.forEach(value -> result.add(isOff(value)));
        return result;
    }

    /**
     * Determine if any value in the list is true
     *
     * @param values List of boolean values
     * @return True if any value is true; otherwise false
     */
    private boolean any(List<Boolean> values) {
        return values.stream().anyMatch(item -> item);
    }

    /**
     * Count number of true values
     *
     * @param values List of boolean values
     * @return Number of true values
     */
    private int count(List<Boolean> values) {
        int n = 0;
        for (boolean value : values) {
            if (value) n++;

        }
        return n;
    }
}
