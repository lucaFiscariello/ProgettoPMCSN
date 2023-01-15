package Code.Server.MultiServer;
import Code.Server.Interface.Server;
import Code.Simulation.Handler.SimulationHandler;
import Code.Simulation.StatsCollector.StatsHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class MultiServer implements Server {
    private String id;
    private double areaNode;
    private double areaqueue;
    private double areaService;
    private int jobNumbers;
    private int departedJobs;
    private int arrivedJobs;
    private int endJobNumber;
    private double lastArrivalTime;
    private int serverNumber;
    private double[] sumService;
    private int[] served;
    private boolean[] idleServer;
    private double meanService;
    private double meanArrival;
    private int streamSimulation;
    private double lastEventTime ;
    private double lastUpdateBatch;
    private Distribution distribution;
    private StatsHandler statsHandler;



    public MultiServer(int jobNumbers,int streamSimulation,double meanService,String id,double meanArrival,int serverNumber,Distribution distribution){
        this.jobNumbers = jobNumbers;
        this.departedJobs= 0;
        this.arrivedJobs= 0;
        this.endJobNumber=0;
        this.areaNode = 0;
        this.areaqueue=0;
        this.areaService=0;
        this.lastArrivalTime=0;
        this.lastEventTime = 0.0;
        this.lastUpdateBatch=0;
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

        if(jobNumbers>serverNumber){
            for(int i=0; i<serverNumber;i++)
                this.idleServer[i] = false;
        }
        else if(jobNumbers>0){
            for(int i=0; i<jobNumbers;i++)
                this.idleServer[i] = false;
        }

        this.statsHandler = new StatsHandler();

    }

    public void processArrival(double timeNext, double timeCurrent) {
        arrivedJobs++;

        if (jobNumbers >= 0) {
            double jobsQueue = (jobNumbers<this.serverNumber)? 0:jobNumbers-serverNumber;
            double jobService = (jobNumbers-jobsQueue>0)?jobNumbers-jobsQueue:0;

            //Aggiorno integrali
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

        //Usato per calcolare autocorrelazione
        this.statsHandler.addServiceTime(service);

        if (jobNumbers > 0)   {
            double jobsQueue = (jobNumbers<this.serverNumber)? 0:jobNumbers-serverNumber;
            double jobService = (jobNumbers-jobsQueue>0)?jobNumbers-jobsQueue:0;

            //Aggiorno integrali
            areaNode += (timeNext - lastEventTime) * jobNumbers;
            areaqueue +=(timeNext - lastEventTime) * jobsQueue;
            areaService += (timeNext - lastEventTime) * jobService;

            int s = this.findOneNotIdle();
            sumService[s] += service;
            served[s]++;
            idleServer[s] = true;

            //Se ci sono altri job in coda posso servirli
            if(jobsQueue>0){
                s= findOneIdle();
                idleServer[s] = false;
            }

            this.jobNumbers--;
            this.departedJobs++;
            lastEventTime = timeNext;

        }

    }

    public void incrementEndJobNumber(){
        this.endJobNumber++;
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
        double utilization = areaService/(SimulationHandler.getCurrentTime()*this.serverNumber);
        double averageJobQueue = areaqueue / SimulationHandler.getCurrentTime();
        double averageJobNode = areaNode / SimulationHandler.getCurrentTime();

        return new Statistics(serviceTime,averageDelay,averageWait,utilization,averageJobQueue,averageJobNode,arrivedJobs,departedJobs,endJobNumber);

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
        printout.println("   autocorrelation .. = "+ statsHandler.getAutocorrelation()+"\n" );
        printout.println("   departedJob .. = "+ statsHandler.getDepartedJob()+"\n" );

        printout.close();

    }


    public void cleanAndSaveStats() {
        HashMap<String, Object> allStats = new HashMap<>();

        if(departedJobs!=0){
            allStats.put("Server Id", this.id);
            allStats.put("departed Jobs", departedJobs);
            allStats.put("average interarrival time", (lastArrivalTime-lastUpdateBatch) / departedJobs);
            allStats.put("average service time", areaService/ departedJobs);
            allStats.put("average delay", areaqueue / departedJobs);
            allStats.put("average wait", areaNode / departedJobs);
            allStats.put("utilization", areaService/((SimulationHandler.getCurrentTime()-lastUpdateBatch)*this.serverNumber));
            allStats.put("average # in the queue", areaqueue / (SimulationHandler.getCurrentTime()-lastUpdateBatch));
            allStats.put("average # in the node", areaNode / (SimulationHandler.getCurrentTime()-lastUpdateBatch));

            statsHandler.saveStats(allStats);
            statsHandler.cleanBatchServiceTime();

        }

        this.departedJobs=0;
        this.areaNode = 0;
        this.areaqueue=0;
        this.areaService=0;
        this.lastUpdateBatch = SimulationHandler.getCurrentTime();



    }


}
