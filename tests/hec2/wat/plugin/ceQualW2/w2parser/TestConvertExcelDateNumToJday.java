package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;

public class TestConvertExcelDateNumToJday {

    private final String basePath = "F:/CRSO 5 year transition models from Laurel";

    private void unitTest(String modelFolder) throws IOException {
        new ConvertExcelDateNumToJday(String.format("%s/%s", basePath, modelFolder), 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_Bonneville() throws IOException {
        unitTest("BON");
    }

    @Test
    public void testConvertExcelDateNumToJday_ChiefJoseph() throws IOException {
        unitTest("CHJ");
    }

    @Test
    public void testConvertExcelDateNumToJday_Dworshak() throws IOException {
        unitTest("DWR");
    }

    @Test
    public void testConvertExcelDateNumToJday_IceHarbor() throws IOException {
        unitTest("IHR");
    }

    @Test
    public void testConvertExcelDateNumToJday_JohnDay() throws IOException {
        unitTest("JDA");
    }

    @Test
    public void testConvertExcelDateNumToJday_LittleGoose() throws IOException {
        unitTest("LGS");
    }

    @Test
    public void testConvertExcelDateNumToJday_LowerMonumental() throws IOException {
        unitTest("LMN");
    }

    @Test
    public void testConvertExcelDateNumToJday_LowerGranite() throws IOException {
        unitTest("LWG");
    }

    @Test
    public void testConvertExcelDateNumToJday_McNary() throws IOException {
        unitTest("MCN");
    }

    @Test
    public void testConvertExcelDateNumToJday_TheDalles() throws IOException {
        unitTest("TDA");
    }
}
