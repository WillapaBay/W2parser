package hec2.wat.plugin.ceQualW2.w2parser;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.util.List;

public class TestGraphFile {

    @Test
    public void testGraphFile() throws FileNotFoundException {
        String infile = "data/ParticleTracking/graph.npt";
        GraphFile graph = new GraphFile(infile);

        List<Constituent> Constituents = graph.getConstituents();
        int numColumns = Constituents.size();

        System.out.println("Constituents:");
        for (Constituent constituent : Constituents) {
            System.out.println("Name: " + constituent.getLongName() +
                    ", Units: " + constituent.getUnits() +
                    ", Column: " + constituent.getColumnNumber() + "/" + numColumns);
        }

        List<Constituent> DerivedConstituents = graph.getDerivedConstituents();
        numColumns = DerivedConstituents.size();

        System.out.println("\nDerived Constituents:");
        for (Constituent constituent : DerivedConstituents) {
            System.out.println("Name: " + constituent.getLongName() +
                    ", Units: " + constituent.getUnits() +
                    ", Column: " + constituent.getColumnNumber() + "/" + numColumns);
        }
    }
}
