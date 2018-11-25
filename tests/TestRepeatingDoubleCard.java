import org.junit.Test;
import w2parser.CardNames;
import w2parser.RepeatingDoubleCard;
import w2parser.TsrPlotCard;
import w2parser.W2ControlFile;
import java.io.FileNotFoundException;
import java.util.List;

public class TestRepeatingDoubleCard {

    @Test
    public void testRepeatingDoubleCard() throws FileNotFoundException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        int numDates = 1;
        RepeatingDoubleCard rdc = new RepeatingDoubleCard(w2con,
                CardNames.WithdrawalOutputDate, numDates, "%8.5f");
        List<Double> data = rdc.getValues();
        data.forEach(System.out::println);
        for (int i = 0; i < data.size(); i++) {
            data.set(i, data.get(i)*2);
        }
        rdc.setValues(data);
        rdc.updateText();
        System.out.println("Done");
        rdc.updateTable();
        w2con.save("results/ParticleTracking/w2_con.npt_revised");
    }


    @Test
    public void testReadETSR() throws FileNotFoundException {
        String infile = "data/ParticleTracking/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);

        TsrPlotCard tsrPlotCard = new TsrPlotCard(w2con);
        int nitsr = tsrPlotCard.getNITSR();

        RepeatingDoubleCard rdc = new RepeatingDoubleCard(w2con,
                CardNames.TimeSeriesLayer, nitsr, "%8.3f");
        List<Double> ETSR = rdc.getValues();
        ETSR.forEach(System.out::println);

        for (int i = 0; i < ETSR.size(); i++) {
            ETSR.set(i, ETSR.get(i)*2);
        }

        ETSR.add(3.0);
        ETSR.add(4.0);

        rdc.setValues(ETSR);
        rdc.updateTable();
        w2con.save("results/ParticleTracking/w2_con.npt_revised");
    }
}
