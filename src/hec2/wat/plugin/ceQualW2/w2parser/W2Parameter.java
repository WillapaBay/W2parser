package hec2.wat.plugin.ceQualW2.w2parser;

public class W2Parameter {
    private String ioName;              // I/O Name a.k.a short location name (used by original parser.exe program)
    private String longLocation;        // Location (waterbody, branch, segment, depth, flow type, etc.)
    private String shortName;           // Short name of parameter - to display in model linking editor
    private String longName;            // Long name of parameter - to display in plots and tool tip of model linking editor
    private String w2Name;              // Parameter name in CE-QUAL-W2 program and user's manual
    private String units;               // W2Parameter units
    private int columnNumber;           // Column number in output file (zero indexed, with Julian day at column 0)
    private int numColumns;             // Total number of data columns (parametersprivate )
    private String fileName;            // Input or output filename
    private String inflowOutflow;       // Flow direction (inflow or outflow)
    private String inputOutput;         // I/O type (input or output)
    private int waterBody;              // Waterbody number (optional)
    private int branch;                 // Branch number (optional)
    private int segment;                // Segment number (optional)
    private String verticalLocation;    // String containing vertical longLocation (either depth or layer number, optional)

    public W2Parameter(String ioName, String longLocation, String shortName, String longName,
                       String w2Name, String units, int columnNumber, int numColumns, String fileName,
                       String inflowOutflow, String inputOutput) {
        this.ioName = ioName;
        this.longLocation = longLocation;
        this.shortName = shortName;
        this.longName = longName;
        this.w2Name = w2Name;
        this.units = units;
        this.columnNumber = columnNumber;
        this.numColumns = numColumns;
        this.fileName = fileName;
        this.inflowOutflow = inflowOutflow;
        this.inputOutput = inputOutput;
        this.waterBody = 0;
        this.branch = 0;
        this.segment = 0;
        this.verticalLocation = "";
    }

    public String getIoName() { return ioName; }

    public String getLongLocation() { return longLocation; }

    public String getShortName() { return shortName; }

    public String getLongName() { return longName; }

    public String getW2Name() { return w2Name; }

    public String getUnits() { return units; }

    public int getColumnNumber() { return columnNumber; }

    public int getNumColumns() { return numColumns; }

    public String getFileName() { return fileName; }

    public String getInflowOutflow() { return inflowOutflow; }

    public String getInputOutput() { return inputOutput; }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public int getWaterBody() {
        return waterBody;
    }

    public void setWaterBody(int waterBody) {
        this.waterBody = waterBody;
    }

    public int getBranch() {
        return branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
    }

    public int getSegment() {
        return segment;
    }

    public void setSegment(int segment) {
        this.segment = segment;
    }

    public String getVerticalLocation() {
        return verticalLocation;
    }

    public void setVerticalLocation(String verticalLocation) {
        this.verticalLocation = verticalLocation;
    }
}
