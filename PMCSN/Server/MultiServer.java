package Server;
import Simulation.Simulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MultiServer implements Server{
    private double areaNode;
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


    public MultiServer(int jobNumbers,int streamSimulation,double meanService,String id,double meanArrival,int serverNumber,Distribution distribution){
        this.jobNumbers = jobNumbers;
        this.departedJobs= 0;
        this.areaNode = 0;
        this.lastArrivalTime=0;
        lastEventTime = 0.0;
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

    }

    public void processArrival(double timeNext, double timeCurrent) {

        if (jobNumbers >= 0) {                           /* update integrals  */
            areaNode += (timeNext - lastEventTime) * jobNumbers;

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
            areaNode += service * jobNumbers;

            //Processo il job e libero un server
            if (this.hasAnyServerNotIdle()) {
                int s = this.findOneNotIdle();

                sumService[s] += service;
                served[s]++;
                idleServer[s] = true;
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

    private int getIdleServerNumber() {
        int serversIdle=0;
        for(int i=0; i<serverNumber;i++){
            if(this.idleServer[i])
                serversIdle = serversIdle+1;
        }

        return  serversIdle;
    }

    private int getNotIdleServerNumber() {
        int serversNotIdle=0;
        for(int i=0; i<serverNumber;i++){
            if(!this.idleServer[i])
                serversNotIdle = serversNotIdle+1;
        }

        return  serversNotIdle;
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
        double minservice = Integer.MAX_VALUE;

        for(int i=0; i<serverNumber;i++){
            if(this.idleServer[i] && sumService[i]<=minservice){
                minservice= sumService[i];
                minPos = i;
            }
        }

        return minPos;
    }

    public boolean hasAnyServerIdle(){
        return this.getIdleServerNumber() > 0;
    }
    public boolean hasAnyServerNotIdle(){
        return this.getNotIdleServerNumber() > 0;
    }

    public Distribution getDistribution(){
        return this.distribution;
    }

    public void printStats() throws IOException {

        FileWriter fileout = new FileWriter("Output\\"+this.id+".txt");
        BufferedWriter filebuf = new BufferedWriter(fileout);
        PrintWriter printout = new PrintWriter(filebuf);

        double areaQueue = areaNode;


        for (int i = 0; i < serverNumber; i++)            /* adjust area to calculate */
            areaQueue -= sumService[i];              /* averages for the queue   */

        double sumServiceTime = 0.0;
        double sumUtilization = 0.0;

        for (int i = 0; i < serverNumber; i++){
            sumServiceTime += sumService[i] / served[i];
            sumUtilization += sumService[i] / Simulation.getCurrentTime();
        }

        printout.println("Server "+this.id);
        printout.println("\nfor "+departedJobs+" jobs\n");
        printout.println("   average interarrival time = "+lastArrivalTime / departedJobs+"\n" );
        printout.println("   average service time .... = "+sumServiceTime/ this.serverNumber+"\n" );
        printout.println("   average delay ........... = "+areaQueue / departedJobs+"\n" );
        printout.println("   average wait ............ = "+areaNode / departedJobs+"\n" );
        printout.println("   utilization ............. = "+sumUtilization/this.serverNumber+"\n" );
        printout.println("   average # in the queue .. = "+areaQueue / Simulation.getCurrentTime()+"\n" );
        printout.println("   average # in the node ... = "+areaNode / Simulation.getCurrentTime()+"\n" );

        printout.close();



    }
}
