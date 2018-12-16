package hec2.wat.plugin.ceQualW2.w2parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Global variables
 */
public class W2Globals {
    public enum FILE_TYPE {CSV, TXT}
    public enum Granularity {DAYS, HOURS, SECONDS}

    public static final String ON = "ON";
    public static final String OFF = "OFF";
    public static final String ONH = "ONH";
    public static final String ONS = "ONS";

    public static List<Integer> ones(int length) {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            values.add(1);
        }
        return values;
    }
}
