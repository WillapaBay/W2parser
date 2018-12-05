package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.util.List;

public class TestW2GraphFile {

    @Test
    public void testGraphFile() {
        String infile = "data/ParticleTracking/graph.npt";
        W2GraphFile graph = new W2GraphFile(infile);

        List<W2Constituent> w2Constituents = graph.getW2Constituents();
        int numColumns = w2Constituents.size();

        System.out.println("w2Constituents:");
        for (W2Constituent w2Constituent : w2Constituents) {
            System.out.println("Name: " + w2Constituent.getLongName() +
                    ", Units: " + w2Constituent.getUnits() +
                    ", Column: " + w2Constituent.getColumnNumber() + "/" + numColumns);
        }

        List<W2Constituent> derivedW2Constituents = graph.getDerivedW2Constituents();
        numColumns = derivedW2Constituents.size();

        System.out.println("\nDerived w2Constituents:");
        for (W2Constituent w2Constituent : derivedW2Constituents) {
            System.out.println("Name: " + w2Constituent.getLongName() +
                    ", Units: " + w2Constituent.getUnits() +
                    ", Column: " + w2Constituent.getColumnNumber() + "/" + numColumns);
        }
    }
}
