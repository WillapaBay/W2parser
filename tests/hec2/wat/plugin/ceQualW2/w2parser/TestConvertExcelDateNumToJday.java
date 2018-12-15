package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TestConvertExcelDateNumToJday {

    @Test
    public void testExcelToJday_Bonneville() throws IOException {
        ConvertExcelDateNumToJday converter = new ConvertExcelDateNumToJday("BON", 2011);
    }
}
