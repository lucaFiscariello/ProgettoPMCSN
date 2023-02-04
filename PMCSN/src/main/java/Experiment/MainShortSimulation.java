package Experiment;

import Code.Generator.Rng;
import Code.Generator.Rngs;
import Code.Simulation.Horizon.Short.Simulation;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class MainShortSimulation {

    public static void main(String[] args) throws IOException, ParseException {
        int stop = 100000;
        int seed = 5;

        String serverOfInteress="procuraRoma";
        String configuration = "src/main/java/ConfigurationFile/InitialNetwork_Verifica.json";

        Simulation.simule(stop,configuration,seed,serverOfInteress);


    }


}
