package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCard2 {

    class QinFileCard extends Card2 {

        public QinFileCard(W2ControlFile w2ControlFile, String cardName,
                           int numRecords, List<Integer> numFieldsList) {
            super(w2ControlFile, cardName, numRecords, numFieldsList, 50);
            parseTable();
        }

        @Override
        public void updateText() {}
    }

    @Test
    public void Test1() throws FileNotFoundException {
        String infile = "data/ColumbiaSlough/w2_con.npt";
        W2ControlFile w2con = new W2ControlFile(infile);
        GridCard gridCard = new GridCard(w2con);
        int numBranches = gridCard.getNumBranches();
        List<Integer> numFields = new ArrayList<>();
        for (int i = 0; i < numBranches; i++) {
            numFields.add(1);
        }
        QinFileCard qinCard = new QinFileCard(w2con, CardNames.BranchInflowFilenames,
                numBranches, numFields);
    }
}