package Code.Simulation.Handler;

import Code.Server.Interface.Server;
import Code.Simulation.Event.BinaryHeap;
import Code.Simulation.Event.Event;
import Code.Simulation.StatsCollector.DataCollector;

public class SimulationHandler {

    public static boolean stopSimulation= false;
    public static double currentTime=0.0;
    public static BinaryHeap allEvents = new BinaryHeap(200000000);
    public static int stop;
    public static int jobsArrived=0;
    public static int jobsDepar=0;
    public static DataCollector dataCollector = new DataCollector();


    public static void advanceTime(double nextTime){
        currentTime=nextTime;
    }

    public static double  getCurrentTime(){
        return currentTime;
    }

    public static void insertEvent(Event e){
        if(e.getNode() == null){
            jobsDepar++;
            dataCollector.collectData(); //Colleziono statistiche di un server di interesse
        }
        else{
            allEvents.insertEvent(e);
            if(e.getNode().getId().contains("procura") && e.getTypeEvent().equals(Event.Type.arrival) && !e.isFeedbackFirstNode())
                jobsArrived++;
        }

    }

    public static Event extractMinEvent(){
        Event e = allEvents.extractMin();
        return e;
    }

    public static void stopSimulation(){
        stopSimulation = true;
    }
    public static void setStop(int stopRun){
        stop = stopRun;
    }

    public static boolean cointinue(){
        boolean continueSimulation = stop>jobsDepar;
        if(!continueSimulation)
            stopSimulation();

        if(!continueSimulation)
            dataCollector.saveCSV(); //Al termine della simulazione memorizzo in un csv le statistiche del server di interesse

        return  continueSimulation;
    }

    public static int getRemainEvents(){
        return allEvents.getHeapSize();
    }

    public static int getJobsArrived() {
        return jobsArrived;
    }

    public static int getJobsDepar() {
        return jobsDepar;
    }

    public static void inizializeDataCollector(Server server, int runNumber,String configuration){
        dataCollector.Inizialize(server,runNumber,configuration);
    }



}
