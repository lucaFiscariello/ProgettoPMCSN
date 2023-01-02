package Experiment;

import Code.Generator.Rngs;
import Code.Network.Network;
import Code.Network.Node;
import Code.Server.Interface.Server;
import Code.Server.Interface.Server.Statistics;
import Code.Simulation.Event.Event;
import Code.Simulation.Handler.SimulationHandler;
import org.json.simple.parser.ParseException;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.ArrayList;

public class MainMeanService {

    //Server di cui voglio le statistiche
    static String serverId = "corte cassazione fisica";
    static ArrayList<Double> averageServiceTime = new ArrayList<>();
    static ArrayList<Double> averageDelayTime = new ArrayList<>();
    static ArrayList<Double> averageWaitTime = new ArrayList<>();
    static ArrayList<Double> utilization= new ArrayList<>();
    static ArrayList<Double> averageJobQueue= new ArrayList<>();
    static ArrayList<Double> averageJobNode= new ArrayList<>();
    static ArrayList<Double> averagePercEnd= new ArrayList<>();

    static Network network;
    static int runNumber = 3000000;


    public static void main(String[] args) throws IOException, ParseException {

        Rngs rngs = new Rngs();
        rngs.plantSeed(2);
        SimulationHandler.setStop(runNumber);
        Event nextEvent ;
        Node nextNode;

        network = new Network("src/main/java/ConfigurationRete2.json",rngs);
        network.initialize();

        while(SimulationHandler.cointinue()){

            nextEvent = SimulationHandler.extractMinEvent();
            nextNode = nextEvent.getNode();
            nextNode.handleEvent(nextEvent, SimulationHandler.getCurrentTime());

            //Ad ogni completamento di un job esterno raccolgo dati delle statistiche
            if(nextEvent.getNode().getId().equals("id1") && nextEvent.getTypeEvent().equals(Event.Type.completion)){
                collectData();
            }

            System.out.println(SimulationHandler.getJobsDepar());

        }

        saveCSV();
        saveStats();

        //System.out.println((double)network.getNodeById("udienza preliminare").getServer().getStatistics().getEndJobNumber()/network.getNodeById("udienza preliminare").getServer().getStatistics().getArrival());

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

        //int total = network.getNodeById("udienza preliminare").getServer().getStatistics().getArrival();
        //int end = network.getNodeById("udienza preliminare").getServer().getStatistics().getEndJobNumber();
        //double perc = (double) end/total;

        //averagePercEnd.add(perc);

    }

    private static void saveCSV() {
        DoubleColumn columnServiceTime = DoubleColumn.create("columnServiceTime",  averageServiceTime.toArray(new Double[runNumber]));
        DoubleColumn columnDelayTime = DoubleColumn.create("columnDelayTime",  averageDelayTime.toArray(new Double[runNumber]));
        DoubleColumn columnWaitTime= DoubleColumn.create("columnWaitTime",  averageWaitTime.toArray(new Double[runNumber]));
        DoubleColumn columnUtilization = DoubleColumn.create("columnUtilization",  utilization.toArray(new Double[runNumber]));
        DoubleColumn columnJobQueue = DoubleColumn.create("columnJobQueue",  averageJobQueue.toArray(new Double[runNumber]));
        DoubleColumn columnJobNode = DoubleColumn.create("columnJobNode",  averageJobNode.toArray(new Double[runNumber]));
        DoubleColumn columnPerc = DoubleColumn.create("columnPerc",  averagePercEnd.toArray(new Double[runNumber]));

        Table dataset = Table.create();
        dataset.addColumns(columnServiceTime,columnDelayTime,columnWaitTime,columnUtilization,columnJobQueue,columnJobNode);

        Table datasetEndJobs = Table.create();
        datasetEndJobs.addColumns(columnPerc);

        dataset.write().csv("CSV//Modello iniziale//"+serverId+".csv");
        datasetEndJobs.write().csv("perc.csv");
    }

    private static void saveStats() throws IOException {
        for (Node node: network.getAllNode().values()) {
            node.getServer().cleanAndSaveStats();
            node.getServer().printStats();
        }
    }


}
