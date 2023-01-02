package Code.Server.SingleServer;

import Code.Server.Interface.Server;
import Code.Simulation.Handler.SimulationHandler;
import Code.Simulation.Handler.StatsHandler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;


public class SingleServer implements Server {
    private int jobNumbers;
    private int departedJobs;
    private int arrivedJobs;
    private int endJobNumber;
    private double areaNode;
    private double areaQueue;
    private double areaService;
    private double lastTime;
    private int streamSimulation;
    private double meanService;
    private double meanArrival;
    private String id;
    private double lastTimeEvent;
    private double lastUpdateBatch;
    private Distribution distribution;
    private StatsHandler statsHandler;


    public SingleServer(int jobNumbers,int streamSimulation,double meanService,String id,double meanArrival,Distribution distribution){
        this.jobNumbers = jobNumbers;
        this.departedJobs= 0;
        this.arrivedJobs= 0;
        this.endJobNumber=0;
        this.areaNode = 0;
        this.areaQueue = 0;
        this.areaService = 0;
        this.lastTime=0;
        this.lastUpdateBatch=0;
        this.streamSimulation = streamSimulation;
        this.meanService= meanService;
        this.meanArrival = meanArrival;
        this.id = id;
        this.lastTimeEvent = 0.0;
        this.distribution = distribution;
        this.statsHandler = new StatsHandler();
    }


    public void processArrival(double timeNext, double timeCurrent){
        arrivedJobs++;

        if (jobNumbers >= 0) {                            /* update integrals  */
            areaNode += (timeNext - lastTimeEvent) * jobNumbers;

            if(jobNumbers>0){
                areaQueue += (timeNext - lastTimeEvent) * (jobNumbers - 1);
                areaService += (timeNext - lastTimeEvent);
            }

            this.jobNumbers++;
            this.lastTime = timeNext;
            this.lastTimeEvent = timeNext;

        }


    }

    public void processCompletition(double timeNext, double timeCurrent){

        //Usato per calcolare autocorrelazione
        this.statsHandler.addServiceTime(timeNext - lastTimeEvent);

        if (jobNumbers > 0)  {                               /* update integrals  */
            areaNode    += (timeNext - lastTimeEvent) * jobNumbers;
            areaQueue   += (timeNext - lastTimeEvent) * (jobNumbers - 1);
            areaService += (timeNext - lastTimeEvent) ;

            this.jobNumbers--;
            this.departedJobs++;
            this.lastTimeEvent = timeNext;

        }


    }

    public void incrementEndJobNumber(){
        this.endJobNumber++;
    }

    public int getStreamSimulation(){
        return this.streamSimulation;
    }

    public double getMeanService(){
        return this.meanService;
    }

    public int getJobNumbers(){
        return this.jobNumbers;
    }

    public String getId(){
        return this.id;
    }

    public double getMeanArrival(){
        return this.meanArrival;
    }

    public Distribution getDistribution(){
        return this.distribution;
    }

    public Statistics getStatistics() {
        double serviceTime = areaService/ departedJobs;
        double averageDelay = areaQueue / departedJobs;
        double averageWait = areaNode / departedJobs;
        double utilization = areaService/ SimulationHandler.getCurrentTime();
        double averageJobQueue = areaQueue / SimulationHandler.getCurrentTime();
        double averageJobNode = areaNode / SimulationHandler.getCurrentTime();

        return new Statistics(serviceTime,averageDelay,averageWait,utilization,averageJobQueue,averageJobNode,arrivedJobs,departedJobs,endJobNumber);
    }

    public void printStats() throws IOException {

        FileWriter fileout = new FileWriter("Output\\"+this.id+".txt");
        BufferedWriter filebuf = new BufferedWriter(fileout);
        PrintWriter printout = new PrintWriter(filebuf);

        printout.println("Code/Server " +this.id);
        printout.println("   average interarrival time = "+ statsHandler.getAverageInterarrivalTime()+"\n" );
        printout.println("   average wait ............ = "+ statsHandler.getAverageWait()+"\n" );
        printout.println("   average delay ........... = "+ statsHandler.getAverageDelay()+"\n" );
        printout.println("   average service time .... = "+ statsHandler.getAverageServiceTime()+"\n" );
        printout.println("   average # in the node ... = "+ statsHandler.getAverageNumberNode()+"\n" );
        printout.println("   average # in the queue .. = "+ statsHandler.getAverageNumberQueue()+"\n" );
        printout.println("   utilization ............. = "+ statsHandler.getAverageUtilization()+"\n" );

        printout.println("   std interarrival time = "+ statsHandler.getStdDevInterarrivalTime()+"\n" );
        printout.println("   std wait ............ = "+ statsHandler.getStdDevWait()+"\n" );
        printout.println("   std delay ........... = "+ statsHandler.getStdDevDelay()+"\n" );
        printout.println("   std service time .... = "+ statsHandler.getStdDevServiceTime()+"\n" );
        printout.println("   std # in the node ... = "+ statsHandler.getStdDevNumberNode()+"\n" );
        printout.println("   std # in the queue .. = "+ statsHandler.getStdDevNumberQueue()+"\n" );
        printout.println("   std utilization ............. = "+ statsHandler.getStdDevUtilization()+"\n" );
        printout.println("   autocorrelation .. = "+ statsHandler.getAutocorrelation()+"\n" );
        printout.println("   departedJob .. = "+ statsHandler.getDepartedJob()+"\n" );

        printout.close();

    }


    public void cleanAndSaveStats() {
        HashMap<String, Object> allStats = new HashMap<>();

        if(departedJobs!=0){
            allStats.put("Server Id", this.id);
            allStats.put("departed Jobs", departedJobs);
            allStats.put("average interarrival time", (lastTime-lastUpdateBatch) / departedJobs);
            allStats.put("average service time", areaService/ departedJobs);
            allStats.put("average delay", areaQueue / departedJobs);
            allStats.put("average wait", areaNode / departedJobs);
            allStats.put("utilization", areaService/( SimulationHandler.getCurrentTime()-lastUpdateBatch));
            allStats.put("average # in the queue", areaQueue /( SimulationHandler.getCurrentTime()-lastUpdateBatch));
            allStats.put("average # in the node", areaNode / ( SimulationHandler.getCurrentTime()-lastUpdateBatch));


            statsHandler.saveStats(allStats);
            statsHandler.cleanBatchServiceTime();
        }

        this.departedJobs=0;
        this.areaNode = 0;
        this.areaQueue=0;
        this.areaService=0;
        this.lastUpdateBatch= SimulationHandler.getCurrentTime();


    }


}
