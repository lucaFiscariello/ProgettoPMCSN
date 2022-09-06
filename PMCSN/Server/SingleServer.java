package Server;

import Simulation.Simulation;

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

    public SingleServer(int jobNumbers,int streamSimulation,double meanService,String id,double meanArrival){
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
    }

    public SingleServer(int streamSimulation,double meanService,String id,double meanArrival){
        this.jobNumbers = 0;
        this.departedJobs= 0;
        this.areaNode = 0;
        this.areaQueue = 0;
        this.areaService = 0;
        this.lastTime=0;
        this.streamSimulation = streamSimulation;
        this.meanService= meanService;
        this.meanArrival = meanArrival;
        this.id = id;

    }

    public void processArrival(double timeNext, double timeCurrent){
        if (jobNumbers > 0)  {                               /* update integrals  */
            areaNode    += (timeNext - timeCurrent) * jobNumbers;
            areaQueue   += (timeNext - timeCurrent) * (jobNumbers - 1);
            areaService += (timeNext - timeCurrent);
        }

        this.jobNumbers++;
        this.lastTime = timeNext;
    }

    public void processCompletition(double timeNext, double timeCurrent){
        if (jobNumbers > 0)  {                               /* update integrals  */
            areaNode    += (timeNext - timeCurrent) * jobNumbers;
            areaQueue   += (timeNext - timeCurrent) * (jobNumbers - 1);
            areaService += (timeNext - timeCurrent);
        }

        this.jobNumbers--;
        this.departedJobs++;
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

    public void printStats(){
        System.out.println("Server "+this.id);
        System.out.println("\nfor "+departedJobs+" jobs\n");
        System.out.println("   average interarrival time = "+lastTime / departedJobs+"\n" );
        System.out.println("   average wait ............ = "+areaNode / departedJobs+"\n" );
        System.out.println("   average delay ........... = "+areaQueue / departedJobs+"\n" );
        System.out.println("   average service time .... = "+areaService / departedJobs+"\n" );
        System.out.println("   average # in the node ... = "+areaNode / Simulation.getCurrentTime()+"\n" );
        System.out.println("   average # in the queue .. = "+areaQueue / Simulation.getCurrentTime()+"\n" );
        System.out.println("   utilization ............. = "+areaService / Simulation.getCurrentTime()+"\n" );
    }




}
