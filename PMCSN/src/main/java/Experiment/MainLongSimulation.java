package Experiment;

import Code.Simulation.Horizon.Long.BatchMeans;
import org.json.simple.parser.ParseException;
import java.io.IOException;

public class MainLongSimulation {

    public static void main(String[] args) throws IOException, ParseException {
        BatchMeans batchMeans = new BatchMeans(128,8000,"src/main/java/ConfigurationFile/InitialNetwork_Verifica.json");
        batchMeans.simule();
    }


}
