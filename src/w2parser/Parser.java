package w2parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private W2ControlFile w2con;
    private int startYear; // Simulation start year
    private double jdayMin; // Simulation start time (Julian days)
    private double jdayMax; // Simulation end time (Julian days)

    // Approx 195 total cards in a W2 control file....
    // 37 / 195 cards = 19%
    private TimeControlCard timeControlCard;
    private GridCard gridCard;
    private BranchGeometryCard branchGeometryCard;
    private InOutflowCard inOutflowCard;
    private GraphFile graphFile;
    private TsrPlotCard tsrPlotCard;
    private RepeatingIntegerCard wdSegmentCard; // "WD SEG"
    private RepeatingIntegerCard withdrawalOutputSegmentCard; // "WITH SEG"
    private ActiveConstituentsCard activeConstituentsCard;
    private ActiveDerivedConstituentsCard activeDerivedConstituentsCard;
    private ActiveConstituentFluxesCard activeConstituentFluxesCard;
    private WithdrawalOutputCard withdrawalOutputCard;
    private RepeatingDoubleCard withdrawalFrequencyCard;
    private RepeatingIntegerCard tsrSegCard;
    private FileCard tsrFilenameCard;
    private RepeatingDoubleCard tsrLayerCard;
    private IceCoverCard iceCoverCard;
    private InitialConditionsCard initCondCard;
    private MultiRecordRepeatingDoubleCard constituentICcard;
    private ConstituentDimensionsCard constituentDimensionsCard;
    private ConstituentComputationsCard constituentComputationsCard;
    private MultiRecordRepeatingStringCard branchConstituentsCard;
    private FileCard qinCard;
    private FileCard tinCard;
    private FileCard cinCard;
    private ValuesCard dstTribCard;
    private FileCard qdtCard;
    private FileCard tdtCard;
    private FileCard qtrCard;
    private FileCard ttrCard;
    private FileCard qwdCard;
    private CalculationsCard calculationsCard;
    private LocationCard locationCard;
    private FileCard qotCard;
    private NumberStructuresCard numberStructuresCard;
    private MultiRecordRepeatingStringCard distributedTributaryConstituentsCard;
    private FileCard cdtCard;
    private FileCard ctrCard;
    private MultiRecordRepeatingStringCard precipConstituentsCard;
    private SedimentCard sedimentCard;

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
    private List<Constituent> graphFileConstituents;
    private List<Constituent> graphFileDerivedConstituents;
    private List<Parameter> inputParameters;
    private List<Parameter> meteorologyParameters;
    private List<Parameter> tsrOutputParameters;
    private List<Parameter> withdrawalOutputParameters;
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
    private static final int NUM_FLUXES = 73; // Currently, this must be hard-coded. Version 4.1 has 73 fluxes.
    private List<Boolean> SEDIMENT_CALC;

    /**
     * Handles reading and updating the CE-QUAL-W2 control file and associated bathymetry files
     * @param w2con CE-QUAL-W2 control file object
     */
    public Parser(W2ControlFile w2con) {
        this.w2con = w2con;
    }

    /**
     * Read the CE-QUAL-W2 control file
     */
    public void readControlFile() {
        // Initialize time window
        timeControlCard = new TimeControlCard(w2con);
        jdayMin = timeControlCard.getJdayMin();
        jdayMax = timeControlCard.getJdayMax();
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

        // Initialize Inflow/Outflow info
        inOutflowCard = new InOutflowCard(w2con);
        NTR = inOutflowCard.getNumTributaries();
        NST = inOutflowCard.getNumStructures();
        NWD = inOutflowCard.getNumWithdrawals();
        NGT = inOutflowCard.getNumGates();

        // Initialize withdrawal output info
        wdSegmentCard = new RepeatingIntegerCard(w2con, CardNames.WithdrawalSegment, NWD, "%8d");
        IWD = wdSegmentCard.getValues();
        withdrawalOutputCard = new WithdrawalOutputCard(w2con);
        NIWDO = withdrawalOutputCard.getNIWDO();
        String WDOC = withdrawalOutputCard.getWDOC();
        RepeatingIntegerCard withdrawalOutputSegmentCard = new RepeatingIntegerCard(w2con, "WITH SEG", NIWDO, "%8d");
        IWDO = withdrawalOutputSegmentCard.getValues();
        // Note: the following is different from the wdSegmentCard ("WD SEG") above
        if (isOn(WDOC)) DOWNSTREAM_OUTFLOW = true;

        // Initialize constituent info, including total number of constituents and number of active constituents
        String graphFilename = w2con.getGraphFilename();
        graphFile = new GraphFile(graphFilename);
        graphFileConstituents = graphFile.getConstituents();
        numConstituents = graphFileConstituents.size();
        //numConstituents = computeNumberOfConstituents();
        activeConstituentsCard = new ActiveConstituentsCard(w2con, numConstituents);
        constituentComputationsCard = new ConstituentComputationsCard(w2con);
        String CCC = constituentComputationsCard.getCCC();
        if (isOn(CCC)) CONSTITUENTS = true;
        constituentDimensionsCard = new ConstituentDimensionsCard(w2con);


        // Initialized derived constituent info
        graphFileDerivedConstituents = graphFile.getDerivedConstituents();
        numDerivedConstituents = graphFileDerivedConstituents.size();
        activeDerivedConstituentsCard = new ActiveDerivedConstituentsCard(w2con, numDerivedConstituents, NWB);
        activeConstituentFluxesCard = new ActiveConstituentFluxesCard(w2con, NUM_FLUXES, NWB);
        List<List<String>> CDWBC = activeDerivedConstituentsCard.getValues();

        List<Boolean> derivedIsOn = new ArrayList<>();
        for (List<String> values: CDWBC) {
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


        /*
         * Fetch model inputs and outputs. For each location, flow type, and parameter, a Parameter object
         * will be created containing the location, short name, long name, units, filename (input or output),
         * and indicators for inflow or outflow and model input or output. The location is a combination of
         * location and flow type, e.g., Branch 1 Distributed Tributary. The short name of a constituent is
         * specified in the control file. The long name and units are obtained from the graph.npt file.
         */
        inputParameters = fetchInputParameters(); // Flow, temperature, and constituent model inputs (inflows & outflows)
        constituentNames = fetchConstituentNames(); // Constituent names from graph.npt file
        initialTemperatures = fetchInitialTemperatures(); // Initial temperatures by waterbody
        initialConcentrations = fetchInitialConcentrations(); // Initial concentrations for each constituent by waterbody

        meteorologyParameters = fetchMeteorologyInputs(); // Meteorology parameters
        initialWaterSurfaceElevations = fetchInitialWaterSurfaceElevations(); // Initial water surface elevations by waterbody

        // Time series files
        if (TIME_SERIES)
            tsrOutputParameters = fetchTsrOutputParameters();
        if (DOWNSTREAM_OUTFLOW)
            withdrawalOutputParameters = fetchWithdrawalOutflowParameters();

        handleVerticalProfiles();  // TODO Add support for vertical profile files, using -1 in initialConcentrations
        handleLongitudinalProfiles();  // TODO Add support for longitudinal profile files, using -2 in initialConcentrations
        handleHabitatVolume(); // TODO Add support for habitat volume
    }

    /**
     * Create withdrawal Parameters
     *
     * Outputs include:
     * QWD: Withdrawal output flow
     *
     * @return List of withdrawal output parameters
     */
    private List<Parameter> fetchWithdrawalOutflowParameters() {
        List<Parameter> parameters = new ArrayList<>();
        List<Integer> NSTR = numberStructuresCard.getNSTR();
        Parameter parameter;
        int outputWaterbody;
        int outputSegment;
        String outputFilename;
        for (int jwd = 0; jwd < NIWDO; jwd++) {
            outputSegment = IWDO.get(jwd);

            // Withdrawal flow, QWD (qwo)
            int icol = 1; // Every qwo file contains at least one data column for the combined outflow
            outputFilename = String.format("qwo_%d.opt", outputSegment);
            String locationName = String.format("Seg %d Withdrawal", outputSegment);
            outputWaterbody = getOutputWaterbody(outputSegment, US, DS, NBR);

            // Number of columns equals 1 for the combined flow plus one column for each structure
            int ncol = 1;
            for (int jstr = 0; jstr < NSTR.get(outputWaterbody - 1); jstr++) {
                ncol++;
            }

            parameter = new Parameter(locationName, "Flow-QWD", "Withdrawal Flow",
                    "m^3/s", icol, ncol, outputFilename, "outflow", "output");
            parameter.setSegment(outputSegment);
            parameters.add(parameter);
            icol++;

            for (int jstr = 0; jstr < NSTR.get(outputWaterbody - 1); jstr++) {
                int outlet = jstr + 1;
                parameter = new Parameter(locationName, String.format("Flow-QWD_outlet_%d", outlet),
                        String.format("Withdrawal Flow - Outlet %d", outlet),
                        "m^3/s", icol, ncol, outputFilename, "outflow", "output");
                parameter.setSegment(outputSegment);
                parameters.add(parameter);
                icol++;
            }

            // Withdrawal temperature, TWO
            icol = 1; // Every qwo file contains at least one data column for the combined outflow
            outputFilename = String.format("two_%d.opt", outputSegment);
            locationName = String.format("Seg %d Withdrawal", outputSegment);
            outputWaterbody = getOutputWaterbody(outputSegment, US, DS, NBR);

            // Number of columns equals 1 for the combined flow plus one column for each structure
            ncol = 1;
            for (int jstr = 0; jstr < NSTR.get(outputWaterbody - 1); jstr++) {
                ncol++;
            }

            parameter = new Parameter(locationName, "Temp-TWO", "Withdrawal Temperature",
                    "C", icol, ncol, outputFilename, "outflow", "output");
            parameter.setSegment(outputSegment);
            parameters.add(parameter);
            icol++;

            for (int jstr = 0; jstr < NSTR.get(outputWaterbody - 1); jstr++) {
                int outlet = jstr + 1;
                parameter = new Parameter(locationName, String.format("Temp-TWO_outlet_%d", outlet),
                        String.format("Withdrawal Temperature - Outlet %d", outlet),
                        "C", icol, ncol, outputFilename, "outflow", "output");
                parameter.setSegment(outputSegment);
                parameters.add(parameter);
                icol++;
            }

            // Constituents (iterate over all constituents, but only output active constituents
            if (CONSTITUENTS) {
                icol = 1; // Every qwo file contains at least one data column for the combined outflow
                List<Parameter> constituentParameters = new ArrayList<>();
                outputFilename = String.format("cwo_%d.opt", outputSegment);
                locationName = String.format("Seg %d Withdrawal", outputSegment);
                outputWaterbody = getOutputWaterbody(outputSegment, US, DS, NBR);
                constituentNames = activeConstituentsCard.getConstituentNames();
                List<String> constituentSettings = activeConstituentsCard.getCAC(); // Active/inactive status of constituents (ON/OFF)
                for (int jc = 0; jc < numConstituents; jc++) {
                    if (isOn(constituentSettings.get(jc))) {
                        Constituent constituent = graphFileConstituents.get(jc);
                        parameter = new Parameter(locationName, constituentNames.get(jc),
                                constituent.getLongName(), constituent.getUnits(),
                                icol, 0, outputFilename,
                                "outflow", "output");
                        parameter.setWaterBody(outputWaterbody);
                        parameter.setSegment(outputSegment);
                        constituentParameters.add(parameter);
                        icol++;
                    }
                }

                final int numConstituentColumns = constituentParameters.size();
                constituentParameters.forEach(param -> param.setNumColumns(numConstituentColumns));
                parameters.addAll(constituentParameters);
            }

            if (DERIVED_CALC) {
                icol = 1; // Every qwo file contains at least one data column for the combined outflow
                List<Parameter> derivedConstituentParameters = new ArrayList<>();
                outputFilename = String.format("dwo_%d.opt", outputSegment);
                locationName = String.format("Seg %d Withdrawal", outputSegment);
//                outputWaterbody = getOutputWaterbody(outputSegment, US, DS, BS, BE, NWB);
                derivedConstituentNames = activeDerivedConstituentsCard.getConstituentNames();
                List<String> derivedConstituentSettings = activeConstituentsCard.getCAC(); // Active/inactive status of constituents (ON/OFF)
                for (int jc = 0; jc < numDerivedConstituents; jc++) {
                    if (isOn(derivedConstituentSettings.get(jc))) {
                        Constituent constituent = graphFileDerivedConstituents.get(jc);
                        parameter = new Parameter(locationName, derivedConstituentNames.get(jc),
                                constituent.getLongName(), constituent.getUnits(),
                                icol, 0, outputFilename,
                                "outflow", "output");
                        parameter.setWaterBody(outputWaterbody);
                        parameter.setSegment(outputSegment);
                        derivedConstituentParameters.add(parameter);
                        icol++;
                    }
                }

                final int numConstituentColumns = derivedConstituentParameters.size();
                derivedConstituentParameters.forEach(param -> param.setNumColumns(numConstituentColumns));
                parameters.addAll(derivedConstituentParameters);
            }

        }

        return parameters;
    }

    /**
     * Determine number of output waterbody based on DS and US parameters
     * @param numBranches Number of branches
     * @param outputSegment Output segment number
     * @return Output waterbody (one-based)
     */
    private int getOutputWaterbody(int outputSegment, List<Integer> US,
                                   List<Integer> DS, int numBranches) {
        int outputWaterbody = 1;
        for (int jb = 0; jb < numBranches; jb++) {
            if (outputSegment >= US.get(jb) && outputSegment <= DS.get(jb)) {
                return outputWaterbody;
            } else {
                outputWaterbody++;
            }
        }
        return -99;
    }

    /**
     * Determine number of output waterbody based on DS, US, BS, and BE parameters
     * @param numWaterbodies Number of waterbodies
     * @param outputSegment Output segment number
     * @return Output waterbody (one-based)
     */
    private int getOutputWaterbody(int outputSegment, List<Integer> US, List<Integer> DS,
                                   List<Integer> BS, List<Integer> BE, int numWaterbodies) {
        int outputWaterbody = 1;
        for (int jwb = 0; jwb < numWaterbodies; jwb++) {
            if (outputSegment >= US.get(BS.get(jwb)) && outputSegment <= DS.get(BE.get(jwb))) {
                return outputWaterbody;
            } else {
                outputWaterbody++;
            }
        }
        return -99;
    }

    /**
     * Set the model output timestep
     * @param interval Output time interval
     * @param granularity Output granularity (days, hours, or seconds)
     */
    public void setOutputTimestep(double interval, Globals.Granularity granularity) {
        int NWDO = withdrawalOutputCard.getNWDO(); // Get number of withdrawal output dates
        withdrawalFrequencyCard = new RepeatingDoubleCard(w2con, "WITH FRE",
                NWDO, "%8.5f");
        String WDOC = "";
        List<Double> WDOF = new ArrayList<>();
        WDOF.add(interval);

        if (granularity == Globals.Granularity.DAYS) {
            WDOC = Globals.ON;
        } else if (granularity == Globals.Granularity.HOURS) {
            WDOC = Globals.ONH;
        } else if (granularity == Globals.Granularity.SECONDS) {
            WDOC = Globals.ONS;
        }
        withdrawalOutputCard.setWDOC(WDOC);
        withdrawalOutputCard.updateTable();
        withdrawalFrequencyCard.setValues(WDOF);
        withdrawalFrequencyCard.updateTable();
    }

    /**
     * Create output time series (TSR) Parameters
     *
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
     *   if SHADE = 1 then no shade
     *   if shade = 0 then no short wave solar reaches the water surface
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
     *   (if LIMC is turned on under the CST COMP card)
     *
     * @return List of TSR output parameters
     */
    private List<Parameter> fetchTsrOutputParameters() {
        List<Parameter> parameters = new ArrayList<>();

        int NITSR = tsrPlotCard.getNITSR();
        tsrSegCard = new RepeatingIntegerCard(w2con, "TSR SEG", NITSR, "%8d");
        List<Integer> outputSegments = tsrSegCard.getValues();

        // Determine TSR filename prefix from the TSR filename card
        tsrFilenameCard = new FileCard(w2con, "TSR FILE", 1);
        String tsrBaseFilename = tsrFilenameCard.getFileName(0);
        String tsrPrefix = tsrBaseFilename.substring(0, tsrBaseFilename.lastIndexOf('.'));

        tsrLayerCard = new RepeatingDoubleCard(w2con, "TSR LAYE", NITSR, "%8.5f");
        List<Double> ETSR = tsrLayerCard.getValues();

        // Get ice cover parameters
        iceCoverCard = new IceCoverCard(w2con, NWB);
        List<String> ICEC = iceCoverCard.getICEC();

        Parameter parameter;

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
            parameter = new Parameter(tsrLocation, "DLT", "Time Step",
                    "s", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // ELWS: Water surface elevation for the cell's segment location (m)
            parameter = new Parameter(tsrLocation, "Elev", "Water Surface Elevation",
                    "m", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // T2: Temperature (Celsius)
            // Note: this will be labeled Temp to ensure proper handling in DSS
            parameter = new Parameter(tsrLocation, "Temp-Water", "Water Temperature",
                    "C", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // U: Velocity (m/s) at the layer and segment specified
            parameter = new Parameter(tsrLocation, "U", "Velocity",
                    "m/s", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // Q: Total (vertically integrated) flow rate through the entire segment (m3/s)
            // Note: this will be labeled Flow-Total to ensure proper handling in DSS
            parameter = new Parameter(tsrLocation, "Flow-Total", "Total Vertically Integrated Flow",
                    "m^3/s", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // SRON: Net short wave solar radiation incident on the water surface (W/m^2, reflection is not included)
            parameter = new Parameter(tsrLocation, "Rad-Shortwave", "Net Shortwave Solar Radiation",
                    "W/m^2", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // EXT: Light extinction coefficient (1/m)
            parameter = new Parameter(tsrLocation, "Ext Coeff", "Light Extinction Coefficient",
                    "1/m", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // DEPTH: Depth from water surface to channel bottom (m)
            // Note: this will be labeled Stage
            parameter = new Parameter(tsrLocation, "Stage", "Depth",
                    "m", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // WIDTH: Surface width (m)*
            parameter = new Parameter(tsrLocation, "Surface Width", "Surface Width",
                    "m", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // SHADE: Shade (shade factor multiplied by SRON)
            //   if SHADE = 1 then no shade
            //   if shade = 0 then no short wave solar reaches the water surface
            parameter = new Parameter(tsrLocation, "Shade", "Shade",
                    "", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // Ice thickness, optional
            if (any(isOn(ICEC))) {
                parameter = new Parameter(tsrLocation, "Ice Thickness", "Ice Thickness",
                        "m", outputColumn, 0, tsrFilename, "outflow", "output");
                parameter.setVerticalLocation(verticalLocation);
                parameter.setSegment(segment);
                parameters.add(parameter);
                outputColumn++;
                paramIndex++;
            }

            // Tvolavg: Vertically volume-weighted temperature at the specified model segment
            // TODO Check why parser.exe program has W/m^2 for the units
            parameter = new Parameter(tsrLocation, "Temp-VolAvg", "Vertically Volume-Weighted Temperature",
                    "C", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // NetRad: Net radiation at surface of segment (W/m^2)
            parameter = new Parameter(tsrLocation, "Rad-Net", "Net Radiation",
                    "C", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // SWSolar: Net short wave solar radiation at surface (W/m^2)
            parameter = new Parameter(tsrLocation, "Rad-Shortwave", "Net Shortwave Solar Radiation",
                    "W/m^2", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // LWRad: Net long wave radiation at surface (W/m2)
            parameter = new Parameter(tsrLocation, "Rad-Longwave", "Net Longwave Radiation",
                    "W/m^2", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // BackRad: Back radiation at surface (W/m2)
            parameter = new Parameter(tsrLocation, "Rad-Back", "Back Radiation",
                    "W/m^2", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // EvapF: Evaporative heat flux at surface (W/m2)
            parameter = new Parameter(tsrLocation, "Rad-Evaporative", "Evaporative Heat Flux",
                    "W/m^2", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // ConducF: Conductive heat flux at surface (W/m2)
            parameter = new Parameter(tsrLocation, "Rad-Conductive", "Conductive Heat Flux",
                    "W/m^2", outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
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
                if (outputSegment >= upstreamSegment && outputSegment <= downstreamSegment){
                    break;
                }
                outputWaterbody++;
            }

            // Constituents (iterate over all constituents, but only output active constituents
            constituentNames = activeConstituentsCard.getConstituentNames();
            List<String> constituentSettings = activeConstituentsCard.getCAC(); // Active/inactive status of constituents (ON/OFF)
            //numActiveConstituents = count(isOn(constituentSettings));
            for (int jc = 0; jc < numConstituents; jc++) {
                if (isOn(constituentSettings.get(jc))) {
                    Constituent constituent = graphFileConstituents.get(jc);
                    parameter = new Parameter(tsrLocation, constituentNames.get(jc),
                            constituent.getLongName(), constituent.getUnits(),
                            outputColumn, 0, tsrFilename, "outflow", "output");
                    parameter.setWaterBody(outputWaterbody);
                    parameter.setVerticalLocation(verticalLocation);
                    parameter.setSegment(segment);
                    parameters.add(parameter);
                    outputColumn++;
                    paramIndex++;
                }
            }

            // Epiphyton
            int numEpiphytonGroups = constituentDimensionsCard.getNEP();
            int epiphytonGroup = 1;
            for (int je = 0; je < numEpiphytonGroups; je++) {
                int epiGroup = je + 1;
                parameter = new Parameter(tsrLocation, "EPI" + epiGroup,
                        "Epiphython " + epiGroup, "g/m^3",
                        outputColumn, 0, tsrFilename, "outflow", "output");
                parameter.setWaterBody(outputWaterbody);
                parameter.setVerticalLocation(verticalLocation);
                parameter.setSegment(segment);
                parameters.add(parameter);
                outputColumn++;
                paramIndex++;
            }

            // Macrophytes
            int numMacrophytes = constituentDimensionsCard.getNMC();
            for (int jm = 0; jm < numMacrophytes; jm++) {
                int macroGroup = jm + 1;
                parameter = new Parameter(tsrLocation, "MAC" + macroGroup,
                        "Macrophytes" + macroGroup, "g/m^3",
                        outputColumn, 0, tsrFilename,
                        "outflow", "output");
                parameter.setWaterBody(outputWaterbody);
                parameter.setVerticalLocation(verticalLocation);
                parameter.setSegment(segment);
                parameters.add(parameter);
                outputColumn++;
                paramIndex++;
            }

            // Sediment - if turned on
            if (SEDIMENT_CALC.get(outputWaterbody - 1)) {
                // SED: Sediment?? (g/m^3)
                parameter = new Parameter(tsrLocation, "SED", "Sediment",
                        "g/m^3", outputColumn, 0, tsrFilename,
                        "outflow", "output");
                parameter.setVerticalLocation(verticalLocation);
                parameter.setSegment(segment);
                parameters.add(parameter);
                outputColumn++;
                paramIndex++;

                // SEDP: Sediment Phosphorus (g/m^3)
                parameter = new Parameter(tsrLocation, "SEDP", "Sediment Phosphorus",
                        "g/m^3", outputColumn, 0, tsrFilename,
                        "outflow", "output");
                parameter.setVerticalLocation(verticalLocation);
                parameter.setSegment(segment);
                parameters.add(parameter);
                outputColumn++;
                paramIndex++;

                // SEDN: Sediment Nitrogen (g/m^3)
                parameter = new Parameter(tsrLocation, "SEDN", "Sediment Nitrogen",
                        "g/m^3", outputColumn, 0, tsrFilename,
                        "outflow", "output");
                parameter.setVerticalLocation(verticalLocation);
                parameter.setSegment(segment);
                parameters.add(parameter);
                outputColumn++;
                paramIndex++;

                // SEDC: Sediment Carbon (g/m^3)
                parameter = new Parameter(tsrLocation, "SEDC", "Sediment Carbon",
                        "g/m^3", outputColumn, 0, tsrFilename,
                        "outflow", "output");
                parameter.setVerticalLocation(verticalLocation);
                parameter.setSegment(segment);
                parameters.add(parameter);
                outputColumn++;
                paramIndex++;
            }

            // Derived constituents (iterate over all derived constituents, but only output active constituents
            derivedConstituentNames = activeDerivedConstituentsCard.getConstituentNames();
            List<List<String>> derivedConstituentSettings = activeDerivedConstituentsCard.getValues();
            for (int jc = 0; jc < numDerivedConstituents; jc++) {
                if (isOn(derivedConstituentSettings.get(outputWaterbody - 1).get(jc))) {
                    Constituent constituent = graphFileDerivedConstituents.get(jc);
                    parameter = new Parameter(tsrLocation, derivedConstituentNames.get(jc),
                            constituent.getLongName(), constituent.getUnits(),
                            outputColumn, 0, tsrFilename, "outflow", "output");
                    parameter.setWaterBody(outputWaterbody);
                    parameter.setVerticalLocation(verticalLocation);
                    parameter.setSegment(segment);
                    parameters.add(parameter);
                    outputColumn++;
                    paramIndex++;
                }
            }

            // Fluxes (iterate over all fluxes, but only output active fluxes
            List<String> fluxNames = activeConstituentFluxesCard.getConstituentNames();
            List<List<String>> fluxSettings = activeConstituentFluxesCard.getValues();
            for (int jc = 0; jc < NUM_FLUXES; jc++) {
                if (isOn(fluxSettings.get(outputWaterbody - 1).get(jc))) {
                    parameter = new Parameter(tsrLocation, fluxNames.get(jc),
                            fluxNames.get(jc), "kg/d",
                            outputColumn, 0, tsrFilename,
                            "outflow", "output");
                    parameter.setWaterBody(outputWaterbody);
                    parameter.setVerticalLocation(verticalLocation);
                    parameter.setSegment(segment);
                    parameters.add(parameter);
                    outputColumn++;
                    paramIndex++;
                }
            }

            // Add CO2GASX flux manually - I don't really understand this in the parser.exe source code
            parameter = new Parameter(tsrLocation, "CO2GASX", "CO2GASX", "kg/d",
                    outputColumn, 0, tsrFilename, "outflow", "output");
            parameter.setWaterBody(outputWaterbody);
            parameter.setVerticalLocation(verticalLocation);
            parameter.setSegment(segment);
            parameters.add(parameter);
            outputColumn++;
            paramIndex++;

            // Set total number of columns
            outputColumn -= 1; // correct for the extra increment in the flux loop above
            int numColumns = outputColumn;
            int istart = paramIndex - numColumns;
            int iend = paramIndex;
            for (int ii = istart; ii < iend; ii++) {
                parameters.get(ii).setNumColumns(numColumns);
            }
        }
        return parameters;
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
     * @param outfile CE-QUAL-W2 control filename
     */
    public void saveControlFile(String outfile) {
       w2con.save(outfile);
    }

    /**
     * Read initial water temperatures from the initial conditions card
     * @return List of initial water temperatures, one per water body
     */
    private List<Double> fetchInitialTemperatures() {
        initCondCard = new InitialConditionsCard(w2con, NWB);
        return initCondCard.getT2I();
    }

    /**
     * Read constituent names obtained from the graph.npt flie
     * @return List of constituent names (i.e., "long" names)
     */
    private List<String> fetchConstituentNames() {
        List<String> ConstituentNames = new ArrayList<>();
        for (Constituent c : graphFileConstituents) {
            ConstituentNames.add(c.longName);
        }
        return ConstituentNames;
    }

    /**
     * Read initial constituent concentrations from the CST ICON card
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

    private String tributaryLocationName(int trib) {
        return "Tributary " + trib;
    }

    private String distTributaryLocationName(int branch) {
        return "Branch " + branch + " Distr Trib";
    }

    private String latWithdrawalLocationName(int segment, int withdrawal) {
        return "Segment " + segment + " Lat Withdrawal " + withdrawal;
    }

    private String precipLocationName(int branch) {
        return "Branch " + branch + " Precip";
    }

    private String structWithdrawalLocationName(int branch, int structure) {
        return "Branch " + branch + " Struct Withdrawal " + structure;
    }

    private String metLocationName(int waterbody) {
        return "WB " + waterbody + " Met";
    }

    /**
     * Create list of input parameters
     */
    private List<Parameter> fetchInputParameters() {
        // Assemble lists of input and output parameters. Each parameter will contain:
        // location, parameter name (short name), description (long name), units
        // column number, total columns in file, and filename
        List<Parameter> parameters = new ArrayList<>();

        // ------------------------------------------------------------------------------------
        // Assemble list of input parameters (inflows and outflows)
        // ------------------------------------------------------------------------------------

        // Branch Inflows, QIN
        qinCard = new FileCard(w2con, CardNames.BranchInflowFilenames, NBR);
        for (int jbr = 0; jbr < NBR; jbr++) {
            if (UHS.get(jbr) == 0) {
                int branch = jbr + 1;
                Parameter parameter = new Parameter("Branch " + branch + " Inflow",
                        "Flow-QIN", "Upstream Branch Inflow", "m^3/s",
                        1, 1, qinCard.getFileNames().get(jbr),
                        "inflow", "input");
                parameters.add(parameter);
            }
        }

        // Branch Inflow Temperature, TIN
        tinCard = new FileCard(w2con, CardNames.BranchInflowTemperatureFilenames, NBR);
        for (int jbr = 0; jbr < NBR; jbr++) {
            if (UHS.get(jbr) == 0) {
                int branch = jbr + 1;
                Parameter parameter = new Parameter(branchInflowLocationName(branch),
                        "Temp-TIN", "Temperature", "C",
                        1, 1, tinCard.getFileNames().get(jbr),
                        "inflow", "input");
                parameters.add(parameter);
            }
        }

        // Distributed Tributary Inflows, QDT
        dstTribCard = new ValuesCard(w2con, CardNames.DistributedTributaries, NBR);
        DTRC = dstTribCard.getValues();
        qdtCard = new FileCard(w2con, CardNames.DistributedTributaryInflowFilenames, NBR);
        for (int jbr = 0; jbr < NBR; jbr++) {
            if (isOn(DTRC.get(jbr))) {
                int branch = jbr + 1;
                Parameter parameter = new Parameter(distTributaryLocationName(branch),
                        "Flow-QDT", "Distributed Tributary Inflow", "m^3/s",
                        1, 1, qdtCard.getFileNames().get(jbr),
                        "inflow", "input");
                parameters.add(parameter);
            }
        }

        // Distributed Tributary Inflow Temperature, TDT
        tdtCard = new FileCard(w2con, CardNames.DistributedTributaryInflowTemperatureFilenames, NBR);
        for (int jbr = 0; jbr < NBR; jbr++) {
            if (isOn(DTRC.get(jbr))) {
                int branch = jbr + 1;
                Parameter parameter = new Parameter(distTributaryLocationName(branch),
                        "Temp-TDT", "Temperature", "C",
                        1, 1, tdtCard.getFileNames().get(jbr),
                        "inflow", "input");
                parameters.add(parameter);
            }
        }

        // Tributary Inflows, QTR
        qtrCard = new FileCard(w2con, CardNames.TributaryInflowFilenames, NTR);
        for (int jtr = 0; jtr < NTR; jtr++) {
            int tributary = jtr + 1;
            Parameter parameter = new Parameter(tributaryLocationName(tributary),
                    "Flow-QTR", "Tributary Inflow", "m^3/s",
                    1, 1, qtrCard.getFileNames().get(jtr),
                    "inflow", "input");
            parameters.add(parameter);
        }

        // Tributary Inflow Temperature, TTR
        ttrCard = new FileCard(w2con, CardNames.TributaryInflowTemperatureFilenames, NTR);
        for (int jtr = 0; jtr < NTR; jtr++) {
            int tributary = jtr + 1;
            Parameter parameter = new Parameter(tributaryLocationName(tributary),
                    "Temp-TTR", "Temperature", "C",
                    1, 1, ttrCard.getFileNames().get(jtr),
                    "inflow", "input");
            parameters.add(parameter);
        }

        // Lateral Withdrawals (outflows), QWD
        qwdCard = new FileCard(w2con, CardNames.WithdrawalFilenames, 1);
        String qwdFileName = qwdCard.getFileNames().get(0);
        for (int jwd = 0; jwd < NWD; jwd++) {
            int segment = IWD.get(jwd);
            int withdrawal = jwd + 1;
            Parameter parameter = new Parameter(latWithdrawalLocationName(segment, withdrawal),
                    "Flow-QWD", "Lateral Withdrawal", "m^3/s",
                    withdrawal, NWD, qwdFileName, "outflow", "input");
            parameters.add(parameter);
        }

        // Precipitation (inflow)
        calculationsCard = new CalculationsCard(w2con, NWB);
        List<String> PRC = calculationsCard.getPRC();
        locationCard = new LocationCard(w2con, NWB);
        BS = locationCard.getBS();
        BE = locationCard.getBE();
        for (int jwb = 0; jwb < NWB; jwb++) {
            if (isOn(PRC.get(jwb))) {
                int waterbody = jwb + 1;
                for (int branch = BS.get(jwb); branch <= BE.get(jwb); branch++) {
                    Parameter parameter = new Parameter(precipLocationName(branch),
                            "Precip", "Precipitation", "m/s",
                            branch, NWD, qwdFileName, "outflow", "input");
                    parameters.add(parameter);
                }
            }
        }

        // Precipitation Temperature (inflow)
        // Iterate over water bodies
        for (int jwb = 0; jwb < NWB; jwb++) {
            if (isOn(PRC.get(jwb))) {
                int waterbody = jwb + 1;
                for (int branch = BS.get(jwb); branch <= BE.get(jwb); branch++) {
                    Parameter parameter = new Parameter(precipLocationName(branch),
                            "Temp-TPR", "Precipitation Temperature", "C",
                            branch, NWD, qwdFileName, "outflow", "input");
                    parameters.add(parameter);
                }
            }
        }

        // Structural Withdrawals (outflows), QOT
        qotCard = new FileCard(w2con, CardNames.StructuralWithdrawalFilenames, 1);
        numberStructuresCard = new NumberStructuresCard(w2con, NBR);
        List<Integer> NSTR = numberStructuresCard.getNSTR();
        for (int jbr = 0; jbr < NBR; jbr++) {
            int branch = jbr + 1;
            for (int structure = 1; structure <= NSTR.get(jbr); structure++) {
                String qotFileName = qotCard.getFileNames().get(jbr);
                Parameter parameter = new Parameter(structWithdrawalLocationName(branch, structure),
                        "Flow-QOT", "Structural Withdrawal", "m^3/s",
                        structure, NSTR.get(branch), qotFileName, "outflow", "input");
                parameters.add(parameter);
            }
        }

        //-----------------------------------------------------------------------------------------------------
        // constituents
        //-----------------------------------------------------------------------------------------------------

        cinCard = new FileCard(w2con, CardNames.BranchInflowConcentrationFilenames, NBR);

        // Branch Inflow Concentrations
        branchConstituentsCard = new MultiRecordRepeatingStringCard(w2con, "CIN CON", numConstituents, NBR);
        List<String> branchInflowConstituentNames = branchConstituentsCard.getNames();
        List<List<String>> CINBRC = branchConstituentsCard.getValues();


        if (CONSTITUENTS) {
            // Iterate over branches
            for (int jbr = 0; jbr < NBR; jbr++) {
                if (UHS.get(jbr) == 0) {
                    int numColumns = computeNumberConstituentColumns(CINBRC, jbr, numConstituents);
                    int branch = jbr + 1;

                    int icol = 0;
                    for (int jc = 0; jc < numConstituents; jc++) {
                        if (isOn(CINBRC.get(jc).get(jbr))) {
                            icol++;
                            Constituent graphFileConstituent = graphFileConstituents.get(jc);
                            Parameter parameter = new Parameter(branchInflowLocationName(branch),
                                    branchInflowConstituentNames.get(jc) + "-CIN",
                                    graphFileConstituent.getLongName(),
                                    graphFileConstituent.getUnits(), icol, numColumns, cinCard.getFileNames().get(jbr),
                                    "inflow", "input");
                            parameters.add(parameter);
                        }
                    }
                }
            }
        }

        // Distributed Tributary Inflow Concentrations
        distributedTributaryConstituentsCard = new MultiRecordRepeatingStringCard(w2con, "CDT CON", numConstituents, NBR);
        List<String> distributedTributaryConstituentNames = distributedTributaryConstituentsCard.getNames();
        List<List<String>> CDTBRC = distributedTributaryConstituentsCard.getValues();
        cdtCard = new FileCard(w2con, CardNames.DistributedTributaryInflowConcentrationFilenames, NBR);
        if (CONSTITUENTS) {
            // Iterate over branches
            for (int jbr = 0; jbr < NBR; jbr++) {
                if (isOn(DTRC.get(jbr))) {
                    int numColumns = computeNumberConstituentColumns(CDTBRC, jbr, numConstituents);
                    int branch = jbr + 1;

                    int icol = 0;
                    for (int jc = 0; jc < numConstituents; jc++) {
                        if (isOn(CDTBRC.get(jc).get(jbr))) {
                            icol++;
                            Constituent graphFileConstituent = graphFileConstituents.get(jc);
                            Parameter parameter = new Parameter(distTributaryLocationName(branch),
                                    distributedTributaryConstituentNames.get(jc) + "-CDT", graphFileConstituent.getLongName(),
                                    graphFileConstituent.getUnits(), icol, numColumns, cdtCard.getFileNames().get(jbr),
                                    "inflow", "input");
                            parameters.add(parameter);
                        }
                    }
                }
            }
        }

        // Tributary Inflow Concentrations
        MultiRecordRepeatingStringCard tributaryConstituentsCard = new MultiRecordRepeatingStringCard(w2con,
                "CDT CON", numConstituents, NTR);
        List<String> tributaryConstituentNames = tributaryConstituentsCard.getNames();
        List<List<String>> CTRBRC = tributaryConstituentsCard.getValues();
        ctrCard = new FileCard(w2con, CardNames.TributaryInflowConcentrationFilenames, NTR);
        if (CONSTITUENTS) {
            // Iterate over tributaries
            for (int jtr = 0; jtr < NTR; jtr++) {
                if (UHS.get(jtr) == 0) {
                    int numColumns = computeNumberConstituentColumns(CTRBRC, jtr, numConstituents);
                    int tributary = jtr + 1;

                    int icol = 0;
                    for (int jc = 0; jc < numConstituents; jc++) {
                        if (isOn(CTRBRC.get(jc).get(jtr))) {
                            icol++;
                            Constituent graphFileConstituent = graphFileConstituents.get(jc);
                            Parameter parameter = new Parameter(tributaryLocationName(tributary),
                                    tributaryConstituentNames.get(jc) + "-CTR", graphFileConstituent.getLongName(),
                                    graphFileConstituent.getUnits(), icol, numColumns, ctrCard.getFileNames().get(jtr),
                                    "inflow", "input");
                            parameters.add(parameter);
                        }
                    }
                }
            }
        }

        // Precipitation Inflow Concentration
        precipConstituentsCard = new MultiRecordRepeatingStringCard(w2con, "CPR CON", numConstituents, NBR);
        List<String> precipConstituentNames = precipConstituentsCard.getNames();
        List<List<String>> CPRBRC = tributaryConstituentsCard.getValues();
        FileCard cprCard = new FileCard(w2con, "CPR FILE", NBR);
        if (CONSTITUENTS) {
            for (int jwb = 0; jwb < NWB; jwb++) {
                if (isOn(PRC.get(jwb))) {
                    int waterbody = jwb + 1;
                    for (int branch = BS.get(jwb); branch <= BE.get(jwb); branch++) {
                        int numColumns = computeNumberConstituentColumns(CPRBRC, jwb, numConstituents);

                        int icol = 0;
                        for (int jc = 0; jc < numConstituents; jc++) {
                            if (isOn(CTRBRC.get(jc).get(jwb))) {
                                icol++;
                                Constituent graphFileConstituent = graphFileConstituents.get(jc);
                                Parameter parameter = new Parameter(precipLocationName(branch),
                                        precipConstituentNames.get(jc) + "-CPR",
                                        graphFileConstituent.getLongName(), graphFileConstituent.getUnits(),
                                        icol, numColumns, cprCard.getFileNames().get(branch - 1),
                                        "inflow", "input");
                                parameters.add(parameter);
                            }
                        }
                    }
                }
            }
        }
        return parameters;
    }

    /**
     * Retrieve meteorology input parameters
     */
    private List<Parameter> fetchMeteorologyInputs() {
        List<Parameter> parameters = new ArrayList<>();
        HeatExchangeCard heatExchangeCard = new HeatExchangeCard(w2con, NWB);
        List<String> SROC = heatExchangeCard.getSROC();

        FileCard metCard = new FileCard(w2con, "MET FILE", NWB);
        for (int jw = 0; jw < NWB; jw++) {
            int waterbody = jw + 1;
            String sroc = SROC.get(jw);
            int nColumns;
            if (isOn(sroc)) {
                nColumns = 6;
            } else {
                nColumns = 5;
            }
            Parameter parameter = new Parameter(metLocationName(waterbody),
                    "TAIR", "Air Temperature", "C",
                    1, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input");
            parameters.add(parameter);
            parameter = new Parameter(metLocationName(waterbody),
                    "TDEW", "Dew Point Temperature", "C",
                    2, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input");
            parameters.add(parameter);
            parameter = new Parameter(metLocationName(waterbody),
                    "WS", "Wind Speed", "m/s",
                    3, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input");
            parameters.add(parameter);
            parameter = new Parameter(metLocationName(waterbody),
                    "PHI", "Wind Direction", "radians",
                    4, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input");
            parameters.add(parameter);
            parameter = new Parameter(metLocationName(waterbody),
                    "Cloud", "Cloud Cover", "0-10",
                    5, nColumns, metCard.getFileNames().get(jw),
                    "inflow", "input");
            parameters.add(parameter);
            if (isOn(sroc)) {
                parameter = new Parameter(metLocationName(waterbody),
                        "SRO", "Shortwave Solar Radiation", "W/m^2",
                        6, nColumns, metCard.getFileNames().get(jw),
                        "inflow", "input");
                parameters.add(parameter);
            }
        }
        return parameters;
    }

//    /**
//     * Compute number of constituents
//     *
//     * This method encapsulates the calculations from PSU's original parser.exe program for HEC-WAT.
//     * However, it is not currently needed to compute the number of constituents, NCT. The new parser
//     * uses the number of constituents in the graph.npt file.
//     */
//    private int computeNumberOfConstituents() {
//        InOutflowCard inOutflowCard = new InOutflowCard(w2con);
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
//        // Constituent, tributary, and withdrawal totals
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
     * @param jdayMin Start time
     * @param jdayMax End time
     * @param startYear Start year
     */
    public void setTimeWindow(double jdayMin, double jdayMax, int startYear) {
        this.jdayMin = jdayMin;
        this.jdayMax = jdayMax;
        this.startYear = startYear;
    }

    /**
     * Create a formatted table of Parameters for writing to file or console
     * @param Parameters List of Parameters
     * @return List of formatted output lines
     */
    private List<String> createTable(List<Parameter> Parameters) {
        List<String> outputList = new ArrayList<>();
        String headerFormat = "%-28s%-17s%-42s%-10s%-8s%-10s%-30s%-15s%-13s";
        String dataFormat   = "%-28s%-17s%-42s%-10s%-8d%-10d%-30s%-15s%-13s";
        String header = String.format(headerFormat, "Location", "Short Name",
                "Long Name", "Units", "Column", "#Columns", "Filename", "Inflow/Outflow", "Input/Output");
        outputList.add(header);
        StringBuilder horizontalBar = new StringBuilder();
        for (int i = 0; i < header.length(); i++) { horizontalBar.append("-"); }
        outputList.add(horizontalBar.toString());

        for (Parameter p : Parameters) {
            String line = String.format(dataFormat, p.getLocation(), p.getShortName(),
                    p.getLongName(), p.getUnits(), p.getColumnNumber(), p.getNumColumns(),
                    p.getFileName(), p.getInflowOutflow(), p.getInputOutput());
            outputList.add(line);
        }
        return outputList;
    }

    /**
     * Print a table of Parameters to the console
     * @param Parameters List of Parameters
     */
    public void printTable(List<Parameter> Parameters) {
        for (String line : createTable(Parameters)) {
            System.out.println(line);
        }
    }

    /**
     * Write a table of Parameters
     * @param Parameters List of Parameters
     * @param outfileName Output filename
     */
    public void writeTable(List<Parameter> Parameters, String outfileName) {
        List<String> lines = createTable(Parameters);
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
     * @param constituentSettings Specifies if a parameter is turned on or off
     * @param column Column in the settings record in the control file. This typically corresponds to a branch.
     * @param numConstituents Number of constituents in the model
     * @return Number of columns in the constituent file for the given column (e.g., branch)
     */
    private int computeNumberConstituentColumns(List<List<String>> constituentSettings, int column, int numConstituents) {
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
        FileCard bathyFileCard = new FileCard(w2con, "BTH FILE", NWB);
        List<List<Double>> waterSurfaceElevations = new ArrayList<>();
        for (int jwb = 0; jwb < NWB; jwb++) {
            String bathyFilename = bathyFileCard.getFileName(jwb);
            try {
                BathymetryFile bathyFile = new BathymetryFile(w2con, bathyFilename, KMX);
                List<Double> ELWS = bathyFile.getELWS().getValues();
                waterSurfaceElevations.add(ELWS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return waterSurfaceElevations;
    }

    /**
     * Return a list of model input parameters
     * @return List of model input parameters
     */
    public List<Parameter> getInputParameters() {
        return this.inputParameters;
    }

    /**
     * Return a list of meteorology parameters
     * @return List of meteorology parameters
     */
    public List<Parameter> getMeteorologyParameters() {
        return this.meteorologyParameters;
    }

    /**
     * Return a list of TSR (time series) output parameters
     * @return List of TSR output parameters
     */
    public List<Parameter> getTsrOutputParameters() {
        return tsrOutputParameters;
    }

    /**
     * Return a list of withdrawal output parameters
     * @return List of withdrawal output parameters
     */
    public List<Parameter> getWithdrawalOutputParameters() {
        return withdrawalOutputParameters;
    }

    /**
     * Return a list of initial water temperatures
     * @return List of initial water temperatures, one list per water body
     */
    public List<Double> getInitialTemperatures() {
        return initialTemperatures;
    }

    /**
     * Return a list of initial concentrations
     * @return List of initial concentrations, one list per water body
     */
    public List<List<Double>> getInitialConcentrations() {
        return initialConcentrations;
    }

    /**
     * Return a list of constituent names, as they appear in the graph.npt file
     * @return List of constituent names
     */
    public List<String> getConstituentNames() {
        return constituentNames;
    }

    /** Return a list of initial water surface elevations
     * @return List of water surface elevations. The list contains a list of water surface elevations
     * for each waterbody.
     */
    public List<List<Double>> getInitialWaterSurfaceElevations() {
        return initialWaterSurfaceElevations;
    }

    /**
     * Get Julian start day of simulation
     * @return Julian start day
     */
    public double getJdayMin() {
        return jdayMin;
    }

    /**
     * Set Julian start day of simulation
     * @param jdayMin Julian start day
     */
    public void setJdayMin(double jdayMin) {
        this.jdayMin = jdayMin;
        timeControlCard.setJdayMin(jdayMin);
        timeControlCard.updateTable();
    }

    /**
     * Get Julian end day of simulation
     * @return Julian end day
     */
    public double getJdayMax() {
        return jdayMax;
    }

    /**
     * Set Julian end day of simulation
     * @param jdayMax Julian end day
     */
    public void setJdayMax(double jdayMax) {
        this.jdayMax = jdayMax;
        timeControlCard.setJdayMax(jdayMax);
        timeControlCard.updateTable();
    }

    /**
     * Get start year of simulation
     * @return Start year of simulation
     */
    public int getStartYear() {
        return startYear;
    }

    /**
     * Set start year of simulation
     * @param startYear Start year of simulation
     */
    public void setStartYear(int startYear) {
        this.startYear = startYear;
        timeControlCard.setStartYear(startYear);
        timeControlCard.updateTable();
    }

    /**
     * Return number of branches
     * @return Number of branches
     */
    public int getNumBranches() {
        return NBR;
    }

    /**
     * Return number of water bodies
     * @return Number of water bodies
     */
    public int getNumWaterbodies() {
        return NWB;
    }

    /**
     * Return number of segments
     * @return Number of segments
     */
    public int getNumSegments() {
        return IMX;
    }

    /**
     * Return number of layers
     * @return Number of layers
     */
    public int getNumLayers() {
        return KMX;
    }

    /**
     * Return number of tributaries
     * @return Number of tributaries
     */
    public int getNumTributaries() {
        return NTR;
    }

    /**
     * Return number of structures
     * @return Number of structures
     */
    public int getNumStructures() {
        return NST;
    }

    /**
     * Return number of withdrawals
     * @return Number of withdrawals
     */
    public int getNumWithdrawals() {
        return NWD;
    }

    /**
     * Return number of gates
     * @return Number of gates
     */
    public int getNumGates() {
        return NGT;
    }

    /**
     * Return number of constituents
     * @return Number of constituents
     */
    public int getNumConstituents() {
        return numConstituents;
    }

    /**
     * Determine if value is "ON"
     * @param value "ON" or "OFF"
     * @return True if value is "ON"
     */
    private boolean isOn(String value) {
        return value.equalsIgnoreCase(Globals.ON);
    }

    /**
     * Determine if each value in a list is "ON"
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
     * @param value "ON" or "OFF"
     * @return True if value is "OFF"
     */
    private boolean isOff(String value) {
        return value.equalsIgnoreCase(Globals.OFF);
    }

    /**
     * Determine if a value is "OFF"
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
     * @param values List of boolean values
     * @return True if any value is true; otherwise false
     */
    private boolean any(List<Boolean> values) {
        return values.stream().anyMatch(item -> item);
    }

    /**
     * Count number of true values
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

