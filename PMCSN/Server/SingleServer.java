package Server;

import Simulation.Simulation;
import Simulation.StatsHandler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;


public class SingleServer implements Server{
    private int jobNumbers;
    private int departedJobs;
    private double areaNode;
    private double areaQueue;
    private double areaService;
    private double lastTime;
    private int streamSimulation;
    private double meanService;
    private double meanArrival;
    private String id;
    private double lastTimeEvent;
    private Distribution distribution;
    private StatsHandler statsHandler;




    public SingleServer(int jobNumbers,int streamSimulation,double meanService,String id,double meanArrival,Distribution distribution){
        this.jobNumbers = jobNumbers;
        this.departedJobs= 0;
        this.areaNode = 0;
        this.areaQueue = 0;
        this.areaService = 0;
        this.lastTime=0;
        this.streamSimulation = streamSimulation;
        this.meanService= meanService;
        this.meanArrival = meanArrival;
        this.id = id;
        this.lastTimeEvent = 1;
        this.distribution = distribution;
        this.statsHandler = new StatsHandler();
    }


    public void processArrival(double timeNext, double timeCurrent){
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
        if (jobNumbers > 0)  {                               /* update integrals  */
            areaNode    += (timeNext - lastTimeEvent) * jobNumbers;
            areaQueue   += (timeNext - lastTimeEvent) * (jobNumbers - 1);
            areaService += (timeNext - lastTimeEvent) ;

            this.jobNumbers--;
            this.departedJobs++;
            this.lastTimeEvent = timeNext;

        }


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
        double utilization = areaService/Simulation.getCurrentTime();
        double averageJobQueue = areaQueue / Simulation.getCurrentTime();
        double averageJobNode = areaNode / Simulation.getCurrentTime();

        return new Statistics(serviceTime,averageDelay,averageWait,utilization,averageJobQueue,averageJobNode);
    }

    public void printStats() throws IOException {

        FileWriter fileout = new FileWriter("Output\\"+this.id+".txt");
        BufferedWriter filebuf = new BufferedWriter(fileout);
        PrintWriter printout = new PrintWriter(filebuf);

        printout.println("Server "+this.id);
        printout.println("\nfor "+departedJobs+" jobs\n");
        printout.println("   average interarrival time = "+lastTime / departedJobs+"\n" );
        printout.println("   average wait ............ = "+areaNode / departedJobs+"\n" );
        printout.println("   average delay ........... = "+areaQueue / departedJobs+"\n" );
        printout.println("   average service time .... = "+areaService / departedJobs+"\n" );
        printout.println("   average # in the node ... = "+areaNode / Simulation.getCurrentTime()+"\n" );
        printout.println("   average # in the queue .. = "+areaQueue / Simulation.getCurrentTime()+"\n" );
        printout.println("   utilization ............. = "+areaService / Simulation.getCurrentTime()+"\n" );

        printout.close();

    }

    public void saveStats() {
        HashMap<String, Object> allStats = new HashMap<>();

        allStats.put("Server Id", this.id);
        allStats.put("departed Jobs", departedJobs);
        allStats.put("average interarrival time", lastTime / departedJobs);
        allStats.put("average service time", areaService/ departedJobs);
        allStats.put("average delay", areaQueue / departedJobs);
        allStats.put("average wait", areaNode / departedJobs);
        allStats.put("utilization", areaService/Simulation.getCurrentTime());
        allStats.put("average # in the queue", areaQueue / Simulation.getCurrentTime());
        allStats.put("average # in the node", areaNode / Simulation.getCurrentTime());

        statsHandler.saveStats(allStats);

    }


}
