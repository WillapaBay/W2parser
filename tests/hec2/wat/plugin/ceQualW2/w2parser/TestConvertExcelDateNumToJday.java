package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestConvertExcelDateNumToJday {

    private final String basePath = "F:/CRSO 5 year transition models from Laurel";

    private void unitTest(String modelFolder) throws IOException {
        ConvertExcelDateNumToJday c = new ConvertExcelDateNumToJday(String.format("%s/%s", basePath, modelFolder), 2011);
        c.init();
    }

    private void convertOneFile(String modelFolder, String inputFileName,
                                String backupFileName, int referenceYear) throws IOException {
        ConvertExcelDateNumToJday converter =
                new ConvertExcelDateNumToJday(String.format("%s/%s", basePath, modelFolder),
                        2011);
        Path directoryPath = converter.getDirectoryPath();
        Path inputFilePath = Paths.get(directoryPath.toString() + "/" + inputFileName);
        Path outputFilePath = Paths.get(directoryPath.toString() + "/" + inputFileName);
        Path backupFilePath = Paths.get(directoryPath.toString() + "/" + backupFileName);
        W2ControlFile.backupFile(inputFilePath, backupFilePath);
        List<String> output = converter.convert(inputFilePath, outputFilePath, referenceYear);
                Files.write(outputFilePath,output);
    }


    @Test
    public void testConvertExcelDateNumToJday_Bonneville() throws IOException {
        String modelFolder = "BON";
        unitTest(modelFolder);
        String inputFileName = "QGT_BON_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_BON_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_ChiefJoseph() throws IOException {
        String modelFolder = "CHJ";
        unitTest(modelFolder);
        String inputFileName = "QGT_CHJ_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_CHJ_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_Dworshak() throws IOException {
        String modelFolder = "DWR";
        unitTest(modelFolder);
        String inputFileName = "QGT_DWR_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_DWR_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_IceHarbor() throws IOException {
        String modelFolder = "IHR";
        unitTest(modelFolder);
        String inputFileName = "QGT_IHR_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_IHR_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_JohnDay() throws IOException {
        String modelFolder = "JDA";
        unitTest(modelFolder);
        String inputFileName = "QGT_JDA_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_JDA_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_LittleGoose() throws IOException {
        String modelFolder = "LGS";
        unitTest(modelFolder);
        String inputFileName = "QGT_LGS_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_LGS_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_LowerMonumental() throws IOException {
        String modelFolder = "LMN";
        unitTest(modelFolder);
        String inputFileName = "QGT_LMN_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_LMN_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_LowerGranite() throws IOException {
        String modelFolder = "LWG";
        unitTest(modelFolder);
        String inputFileName = "QGT_LWG_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_LWG_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_McNary() throws IOException {
        String modelFolder = "MCN";
        unitTest(modelFolder);
        String inputFileName = "QGT_MCN_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_MCN_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }

    @Test
    public void testConvertExcelDateNumToJday_TheDalles() throws IOException {
        String modelFolder = "TDA";
//        unitTest(modelFolder);
        String inputFileName = "QGT_TDA_2011_2015_daily_DSS-scaled.csv";
        String backupFileName = "QGT_TDA_2011_2015_daily_DSS-scaled.csv.ExcelDateNum.Backup";
        convertOneFile(modelFolder, inputFileName, backupFileName, 2011);
    }



}
