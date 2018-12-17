package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestRepeatingIntegerCard {

    @Test
    public void testRepeatingIntegerCard() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        RepeatingIntegerCard rdc = new RepeatingIntegerCard(w2con,
                W2CardNames.WithdrawalSegment, 5, "%8d");
        List<Integer> IWDO = rdc.getValues();
        IWDO.forEach(System.out::println);
        for (int i = 0; i < IWDO.size(); i++) {
            IWDO.set(i, IWDO.get(i) * 2);
        }

        rdc.setValues(IWDO);
        rdc.updateTable();
        w2con.save("results/ParticleTracking/w2_con.npt_revised");
    }

    @Test
    public void testReadNITSR() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        TsrPlotCard tsrPlotCard = new TsrPlotCard(w2con);
        int nitsr = tsrPlotCard.getNITSR();
        System.out.println("NITSR = " + nitsr);
    }

    @Test
    public void testReadITSR() throws IOException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);

        TsrPlotCard tsrPlotCard = new TsrPlotCard(w2con);
        int nitsr = tsrPlotCard.getNITSR();

        RepeatingIntegerCard rdc = new RepeatingIntegerCard(w2con, W2CardNames.TimeSeriesSegment, nitsr, "%8d");
        List<Integer> tsrSegments = rdc.getValues();
        tsrSegments.forEach(System.out::println);

        for (int i = 0; i < tsrSegments.size(); i++) {
            tsrSegments.set(i, tsrSegments.get(i)*2);
        }

        tsrSegments.add(93);
        tsrSegments.add(95);

        rdc.setValues(tsrSegments);
        rdc.updateTable();
        w2con.save("results/ParticleTracking/w2_con.npt_revised");
    }
}
