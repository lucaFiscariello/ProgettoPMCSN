package Code.Simulation.Handler;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.sqrt;

public class StatsHandler {
    private int index;
    private double averageInterarrivalTime;
    private double averageServiceTime;
    private double averageDelay;
    private double averageWait;
    private double averageUtilization;
    private double averageNumberQueue;
    private double averageNumberNode;
    private double stdDevInterarrivalTime;
    private double stdDevServiceTime;
    private double stdDevDelay;
    private double stdDevWait;
    private double stdDevUtilization;
    private double stdDevNumberQueue;
    private double stdDevNumberNode;

    private double sumInterarrivalTime;
    private double sumServiceTime;
    private double sumDelay;
    private double sumWait;
    private double sumUtilization;
    private double sumNumberQueue;
    private double sumNumberNode;
    private ArrayList<Double> allServiceGenerate = new ArrayList<>();
    private double autocorrelation;
    private int departedJob;



    public StatsHandler(){
        index = 0;

        sumInterarrivalTime = 0;
        sumServiceTime = 0;
        sumDelay = 0;
        sumWait = 0;
        sumUtilization = 0;
        sumNumberQueue = 0;
        sumNumberNode = 0;
        averageInterarrivalTime = 0;
        averageServiceTime= 0;
        averageDelay = 0;
        averageWait= 0;
        averageUtilization= 0;
        averageNumberQueue= 0;
        averageNumberNode= 0;
        departedJob=0;
    }


    public void saveStats(HashMap<String,Object> allStats){
        index++;
        double diff;

        diff= (double)allStats.get("average interarrival time") - averageInterarrivalTime;
        sumInterarrivalTime  += diff * diff * (index - 1) / index;
        averageInterarrivalTime += diff / index;
        stdDevInterarrivalTime = sqrt(sumInterarrivalTime/index);

        diff= (double)allStats.get("average service time") - averageServiceTime;
        sumServiceTime  += diff * diff * (index - 1) / index;
        averageServiceTime += diff / index;
        stdDevServiceTime = sqrt(sumServiceTime/index);

        diff= (double)allStats.get("average delay") - averageDelay;
        sumDelay  += diff * diff * (index - 1) / index;
        averageDelay += diff / index;
        stdDevDelay = sqrt(sumDelay/index);

        diff= (double)allStats.get("average wait") - averageWait;
        sumWait  += diff * diff * (index - 1) / index;
        averageWait += diff / index;
        stdDevWait = sqrt(sumWait/index);

        diff= (double)allStats.get("utilization") - averageUtilization;
        sumUtilization  += diff * diff * (index - 1) / index;
        averageUtilization += diff / index;
        stdDevUtilization = sqrt(sumUtilization/index);

        diff= (double)allStats.get("average # in the queue") - averageNumberQueue;
        sumNumberQueue  += diff * diff * (index - 1) / index;
        averageNumberQueue += diff / index;
        stdDevNumberQueue = sqrt(sumNumberQueue/index);

        diff= (double)allStats.get("average # in the node") - averageNumberNode;
        sumNumberNode  += diff * diff * (index - 1) / index;
        averageNumberNode += diff / index;
        stdDevNumberNode= sqrt(sumNumberNode/index);

        departedJob=departedJob+(int)allStats.get("departed Jobs");

        double num=0;
        double den=0;

        for(int i =0; i<this.allServiceGenerate.size()-1;i++){
            num = num+ (allServiceGenerate.get(i)-this.averageServiceTime)*(allServiceGenerate.get(i+1)-this.averageServiceTime);
            den = den +(allServiceGenerate.get(i+1)-this.averageServiceTime)*(allServiceGenerate.get(i+1)-this.averageServiceTime);
        }

        if(den!=0)
         this.autocorrelation=(num/den);

    }

    public void addServiceTime(double service){
        this.allServiceGenerate.add(service);
    }

    public void cleanBatchServiceTime(){
        this.allServiceGenerate = new ArrayList<>();
    }


    public double getAverageInterarrivalTime() {
        return averageInterarrivalTime;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public double getAverageDelay() {
        return averageDelay;
    }

    public double getAverageWait() {
        return averageWait;
    }

    public double getAverageUtilization() {
        return averageUtilization;
    }

    public double getAverageNumberQueue() {
        return averageNumberQueue;
    }

    public double getAverageNumberNode() {
        return averageNumberNode;
    }

    public double getStdDevInterarrivalTime() {
        return stdDevInterarrivalTime;
    }

    public double getStdDevServiceTime() {
        return stdDevServiceTime;
    }

    public double getStdDevDelay() {
        return stdDevDelay;
    }

    public double getStdDevWait() {
        return stdDevWait;
    }

    public double getStdDevUtilization() {
        return stdDevUtilization;
    }

    public double getStdDevNumberQueue() {
        return stdDevNumberQueue;
    }

    public double getStdDevNumberNode() {
        return stdDevNumberNode;
    }

    public double getAutocorrelation(){
        return this.autocorrelation;
    }

    public int getDepartedJob(){
        return this.departedJob;
    }
}
