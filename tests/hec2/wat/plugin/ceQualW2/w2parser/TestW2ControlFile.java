package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestW2ControlFile {

    @Test
    public void testReadW2ControlFile() throws IOException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
    }

    @Test
    public void testWriteW2ControlFile() throws IOException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        String outfile = "results/ColumbiaSlough/w2_con.npt.NEW";
        W2ControlFile w2con = new W2ControlFile(infile);
        w2con.save(outfile);
    }
}
