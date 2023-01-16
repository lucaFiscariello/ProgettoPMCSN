package Experiment;

import Code.Simulation.Horizon.Long.BatchMeans;
import org.json.simple.parser.ParseException;
import java.io.IOException;

public class MainLongSimulation {

    public static void main(String[] args) throws IOException, ParseException {
        BatchMeans batchMeans = new BatchMeans(32,70000,"src/main/java/ConfigurationFile/InitialNetwork.json");
        batchMeans.simule();
    }


}
