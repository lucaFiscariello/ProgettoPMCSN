package Experiment;

import Code.Simulation.Horizon.Short.Simulation;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class MainShortSimulation {

    public static void main(String[] args) throws IOException, ParseException {
        int stop = 1000000;
        int seed = 100;

        String serverOfInteress="tribunale";
        String configuration = "src/main/java/ConfigurationFile/ImproveNetwork_3.json";

        Simulation.simule(stop,configuration,seed,serverOfInteress);

    }


}
