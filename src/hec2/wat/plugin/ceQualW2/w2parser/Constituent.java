package hec2.wat.plugin.ceQualW2.w2parser;

/**
 * Constituent information container
 */
public class Constituent {
    private String shortName;
    private String longName;
    private String units;
    private int columnNumber;

    public Constituent(String shortName, String longName, String units, int columnNumber) {
        this.shortName = shortName;
        this.longName = longName;
        this.units = units;
        this.columnNumber = columnNumber;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getUnits() {
        return units;
    }

    public int getColumnNumber() {
        return columnNumber;
    }
}
