package Server;
import Simulation.Simulation;
import Simulation.StatsHandler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class MultiServer implements Server{
    private double areaNode;
    private double areaqueue;
    private double areaService;
    private int jobNumbers;
    private double lastArrivalTime;
    private int serverNumber;
    private int departedJobs;
    private double[] sumService;
    private int[] served;
    private boolean[] idleServer;
    private String id;
    private double meanService;
    private double meanArrival;
    private int streamSimulation;
    private double lastEventTime ;
    private Distribution distribution;
    private StatsHandler statsHandler;


    public MultiServer(int jobNumbers,int streamSimulation,double meanService,String id,double meanArrival,int serverNumber,Distribution distribution){
        this.jobNumbers = jobNumbers;
        this.departedJobs= 0;
        this.areaNode = 0;
        this.areaqueue=0;
        this.areaService=0;
        this.lastArrivalTime=0;
        this.lastEventTime = 1.0;
        this.streamSimulation = streamSimulation;
        this.meanService= meanService;
        this.meanArrival = meanArrival;
        this.id = id;
        this.serverNumber = serverNumber;

        this.served = new int[serverNumber];
        this.idleServer = new boolean[serverNumber];
        this.sumService = new double[serverNumber];

        this.distribution = distribution;

        for(int i=0; i<serverNumber;i++){
            this.served[i] = 0;
            this.idleServer[i] = true;
            this.sumService[i] = 0.0;
        }

        this.statsHandler = new StatsHandler();

    }

    public void processArrival(double timeNext, double timeCurrent) {

        if (jobNumbers >= 0) {
            double jobsQueue = (jobNumbers<this.serverNumber)? 0:jobNumbers-serverNumber;
            double jobService = (jobNumbers-jobsQueue>0)?jobNumbers-jobsQueue:0;

            areaNode += (timeNext - lastEventTime) * jobNumbers;
            areaqueue +=(timeNext - lastEventTime) * jobsQueue;
            areaService += (timeNext - lastEventTime) * jobService;

            // Se ci sono server liberi è possibile processare un nuovo job
            if (this.hasAnyServerIdle()) {
                int s = this.findOneIdle();
                idleServer[s] = false;
            }

            this.jobNumbers++;
            lastEventTime = timeNext;
            lastArrivalTime = timeNext;

        }
    }


    public void processCompletition(double timeNext, double service){

        if (jobNumbers > 0)   {                           /* update integrals  */
            double jobsQueue = (jobNumbers<this.serverNumber)? 0:jobNumbers-serverNumber;
            double jobService = (jobNumbers-jobsQueue>0)?jobNumbers-jobsQueue:0;

            areaNode += (timeNext - lastEventTime) * jobNumbers;
            areaqueue +=(timeNext - lastEventTime) * jobsQueue;
            areaService += (timeNext - lastEventTime) * jobService;

            int s = this.findOneNotIdle();
            sumService[s] += service;
            served[s]++;
            idleServer[s] = true;

            if(jobsQueue>0){
                s= findOneIdle();
                idleServer[s] = false;
            }

            this.jobNumbers--;
            this.departedJobs++;
            lastEventTime = timeNext;

        }

    }

    public int getStreamSimulation() {
        return streamSimulation;
    }

    public double getMeanService() {
        return meanService;
    }

    public double getMeanArrival() {
        return meanArrival;
    }

    public int getJobNumbers() {
        return jobNumbers;
    }

    public String getId() {
        return id;
    }


    public Distribution getDistribution(){
        return this.distribution;
    }

    public int getServerNumber(){
        return this.serverNumber;
    }

    public int getQueueJobs(){
        return (jobNumbers<this.serverNumber)? 0:jobNumbers-serverNumber;
    }

    public Statistics getStatistics() {
        double serviceTime = areaService/ departedJobs;
        double averageDelay = areaqueue / departedJobs;
        double averageWait = areaNode / departedJobs;
        double utilization = areaService/(Simulation.getCurrentTime()*this.serverNumber);
        double averageJobQueue = areaqueue / Simulation.getCurrentTime();
        double averageJobNode = areaNode / Simulation.getCurrentTime();

        return new Statistics(serviceTime,averageDelay,averageWait,utilization,averageJobQueue,averageJobNode);

    }


    private int getIdleServerNumber() {
        int serversIdle=0;
        for(int i=0; i<serverNumber;i++){
            if(this.idleServer[i])
                serversIdle = serversIdle+1;
        }

        return  serversIdle;
    }

    private int findOneNotIdle() {
        for(int i=0; i<serverNumber;i++){
            if(!this.idleServer[i])
                return i;
        }

        return -1;
    }

    private int findOneIdle() {
        int minPos = -1;
        double minservice = Double.MAX_VALUE;

        for(int i=0; i<serverNumber;i++){
            if(this.idleServer[i] && sumService[i]<=minservice){
                minservice= sumService[i];
                minPos = i;
            }
        }

        return minPos;
    }

    public boolean hasAnyServerIdle(){
        return this.getIdleServerNumber()>0;
    }

    public void printStats() throws IOException {

        FileWriter fileout = new FileWriter("Output\\"+this.id+".txt");
        BufferedWriter filebuf = new BufferedWriter(fileout);
        PrintWriter printout = new PrintWriter(filebuf);

        printout.println("Server "+this.id);
        printout.println("\nfor "+departedJobs+" jobs\n");
        printout.println("   average interarrival time = "+lastArrivalTime / departedJobs+"\n" );
        printout.println("   average service time .... = "+areaService/ departedJobs+"\n" );
        printout.println("   average delay ........... = "+areaqueue / departedJobs+"\n" );
        printout.println("   average wait ............ = "+areaNode / departedJobs+"\n" );
        printout.println("   utilization ............. = "+areaService/(Simulation.getCurrentTime()*this.serverNumber)+"\n" );
        printout.println("   average # in the queue .. = "+areaqueue / Simulation.getCurrentTime()+"\n" );
        printout.println("   average # in the node ... = "+areaNode / Simulation.getCurrentTime()+"\n" );

        printout.close();
    }

    public void saveStats() {
        HashMap<String, Object> allStats = new HashMap<>();

        allStats.put("Server Id", this.id);
        allStats.put("departed Jobs", departedJobs);
        allStats.put("average interarrival time", lastArrivalTime / departedJobs);
        allStats.put("average service time", areaService/ departedJobs);
        allStats.put("average delay", areaqueue / departedJobs);
        allStats.put("average wait", areaNode / departedJobs);
        allStats.put("utilization", areaService/(Simulation.getCurrentTime()*this.serverNumber));
        allStats.put("average # in the queue", areaqueue / Simulation.getCurrentTime());
        allStats.put("average # in the node", areaNode / Simulation.getCurrentTime());

        statsHandler.saveStats(allStats);

    }


}
