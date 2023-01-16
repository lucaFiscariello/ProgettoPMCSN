package Code.Simulation.StatsCollector;

import Code.Server.Interface.Server;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;

public class DataCollector {
    private  Server server;
    private int runNumber;

    private String configuration;
    private ArrayList<Double> averageServiceTime = new ArrayList<>();
    private ArrayList<Double> averageDelayTime = new ArrayList<>();
    private ArrayList<Double> averageWaitTime = new ArrayList<>();
    private ArrayList<Double> utilization= new ArrayList<>();
    private ArrayList<Double> averageJobQueue= new ArrayList<>();
    private ArrayList<Double> averageJobNode= new ArrayList<>();
    private ArrayList<Double> averagePercEnd= new ArrayList<>();

    public void Inizialize(Server server,int runNumber,String configuration){
        this.server = server;
        this.runNumber=runNumber;
        this.configuration = configuration;
    }

    public void collectData() {
        if(server!=null){
            Server.Statistics statistics = server.getStatistics();

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

    }

    public  void saveCSV() {

        if(server!=null){
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

            if(!configuration.contains("Improve")){
                dataset.write().csv("CSV//Modello iniziale//"+server.getId()+".csv");
                datasetEndJobs.write().csv("perc.csv");
            }
            else{
                dataset.write().csv("CSV//Modello migliorato//versione"+configuration.charAt(configuration.length()-6)+"//"+server.getId()+".csv");
            }

        }

    }
}
