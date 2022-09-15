import Generator.Rngs;
import Network.Network;
import Network.Node;
import Server.Server;
import Server.Server.Statistics;
import Simulation.Event.Event;
import Simulation.Simulation;
import org.json.simple.parser.ParseException;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;

public class MainMeanService {

    //Server di cui voglio le statistiche
    static String serverId = "corte cassazione";
    static ArrayList<Double> averageServiceTime = new ArrayList<>();
    static ArrayList<Double> averageDelayTime = new ArrayList<>();
    static ArrayList<Double> averageWaitTime = new ArrayList<>();
    static ArrayList<Double> utilization= new ArrayList<>();
    static ArrayList<Double> averageJobQueue= new ArrayList<>();
    static ArrayList<Double> averageJobNode= new ArrayList<>();
    static Network network;
    static int runNumber = 1000000;


    public static void main(String[] args) throws IOException, ParseException {

        Rngs rngs = new Rngs();
        rngs.plantSeed(1);
        Simulation.setStop(runNumber);
        Event nextEvent ;

        network = new Network("src/main/java/ConfigurationRete.json",rngs);
        Simulation.insertEvent(network.getFirstEventArrival());

        while(Simulation.cointinue()){
            nextEvent = Simulation.extractMinEvent();
            network.handleEvent(nextEvent, Simulation.getCurrentTime());

            //Ad ogni nuovo completamento raccolgo dati delle statistiche
            if(nextEvent.getNode().getId().equals("id1") && nextEvent.getTypeEvent().equals(Event.Type.completion)){
                collectData();
            }
        }

        saveCSV();
        saveStats();

    }


    private static void collectData() {
        Server server = network.getNodeById(serverId).getServer();
        Statistics statistics = server.getStatistics();

        averageServiceTime.add(statistics.getAverageServiceTime());
        averageDelayTime.add(statistics.getAverageDelay());
        averageWaitTime.add(statistics.getAverageWait());
        utilization.add(statistics.getUtilization());
        averageJobQueue.add(statistics.getAverageJobQueue());
        averageJobNode.add(statistics.getAverageJobNode());

    }

    private static void saveCSV() {
        DoubleColumn columnServiceTime = DoubleColumn.create("columnServiceTime",  averageServiceTime.toArray(new Double[runNumber]));
        DoubleColumn columnDelayTime = DoubleColumn.create("columnDelayTime",  averageDelayTime.toArray(new Double[runNumber]));
        DoubleColumn columnWaitTime= DoubleColumn.create("columnWaitTime",  averageWaitTime.toArray(new Double[runNumber]));
        DoubleColumn columnUtilization = DoubleColumn.create("columnUtilization",  utilization.toArray(new Double[runNumber]));
        DoubleColumn columnJobQueue = DoubleColumn.create("columnJobQueue",  averageJobQueue.toArray(new Double[runNumber]));
        DoubleColumn columnJobNode = DoubleColumn.create("columnJobNode",  averageJobNode.toArray(new Double[runNumber]));

        Table dataset = Table.create();
        dataset.addColumns(columnServiceTime,columnDelayTime,columnWaitTime,columnUtilization,columnJobQueue,columnJobNode);

        dataset.write().csv(serverId+".csv");
        System.out.println(Simulation.getJobsArrived());
        System.out.println(Simulation.getJobsDepar());
    }

    private static void saveStats() throws IOException {
        for (Node node: network.getAllNode().values()) {
            node.getServer().printStats();
        }
    }


}
