package Server;

import Simulation.Simulation;

public class MultiServer implements Server{
    private double areaNode;
    private int jobNumbers;
    private double lastTime;
    private int serverNumber;
    private int departedJobs;
    private double[] sumService;
    private int[] served;
    private boolean[] idleServer;
    private String id;
    private double meanService;
    private double meanArrival;
    private int streamSimulation;

    public MultiServer(int jobNumbers,int streamSimulation,double meanService,String id,double meanArrival,int serverNumber){
        this.jobNumbers = jobNumbers;
        this.departedJobs= 0;
        this.areaNode = 0;
        this.lastTime=0;
        this.streamSimulation = streamSimulation;
        this.meanService= meanService;
        this.meanArrival = meanArrival;
        this.id = id;
        this.serverNumber = serverNumber;

        this.served = new int[serverNumber];
        this.idleServer = new boolean[serverNumber];
        this.sumService = new double[serverNumber];

        for(int i=0; i<serverNumber;i++){
            this.served[i] = 0;
            this.idleServer[i] = true;
            this.sumService[i] = 0.0;
        }

    }

    public void processArrival(double timeNext, double timeCurrent) {

        if (jobNumbers >= 0) {                           /* update integrals  */
            areaNode += (timeNext - timeCurrent) * jobNumbers;

            // Se ci sono server liberi è possibile processare un nuovo job
            if (this.hasAnyServerIdle()) {
                int s = this.findOneIdle();
                idleServer[s] = false;
            }

            this.jobNumbers++;
        }
    }


    public void processCompletition(double timeNext, double timeCurrent){

        if (jobNumbers > 0)   {                           /* update integrals  */
            areaNode += (timeNext - timeCurrent) * jobNumbers;

            //Processo il job e libero un server
            if (this.hasAnyServerNotIdle()) {
                int s = this.findOneNotIdle();
                sumService[s] += timeNext - timeCurrent;
                served[s]++;

                idleServer[s] = true;
            }

            lastTime = timeNext;
            this.jobNumbers--;
            this.departedJobs++;

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


    public void printStats(){
        System.out.println("Server "+this.id);
        System.out.println("\nfor "+departedJobs+" jobs\n");
        System.out.println("   average interarrival time = "+lastTime / departedJobs+"\n" );
        System.out.println("   average wait ............ = "+areaNode / departedJobs+"\n" );
        System.out.println("   average # in the node ... = "+areaNode / Simulation.getCurrentTime()+"\n" );

        for (int i = 0; i < serverNumber; i++)            /* adjust area to calculate */
            areaNode -= sumService[i];              /* averages for the queue   */

        System.out.println("   average delay ........... = "+areaNode / departedJobs+"\n" );
        System.out.println("   average # in the queue .. = "+areaNode / Simulation.getCurrentTime()+"\n" );

        for (int i = 0; i < serverNumber; i++){
            System.out.println("   average service time .... = "+sumService[i] / served[i]+"\n" );
            System.out.println("   utilization ............. = "+sumService[i] / Simulation.getCurrentTime()+"\n" );
        }



    }
}
