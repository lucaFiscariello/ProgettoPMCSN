package Code.Server.Interface;

import java.io.IOException;
import java.util.HashMap;

public interface Server {

    public void processArrival(double timeNext, double timeCurrent);

    public void processCompletition(double timeNext, double timeCurrent);

    public int getStreamSimulation();

    public double getMeanService();
    public double getMeanArrival();

    public int getJobNumbers();

    public String getId();

    public void printStats() throws IOException;

    public Statistics getStatistics();

    public Distribution getDistribution();

    public void incrementEndJobNumber();

    public void cleanAndSaveStats();

    public enum Distribution{
        Exponential,
        Deterministic,
        KErlang,
        Pareto;
    }

    public class Statistics {
        double averageServiceTime;
        double averageDelay;
        double averageWait;
        double utilization;
        double averageJobQueue;
        double averageJobNode;
        int arrival;
        int departed;
        int endJobNumber;

        public Statistics(double averageServiceTime, double averageDelay, double averageWait, double utilization, double averageJobQueue, double averageJobNode,int arrival, int departed,int endJob) {
            this.averageServiceTime = averageServiceTime;
            this.averageDelay = averageDelay;
            this.averageWait = averageWait;
            this.utilization = utilization;
            this.averageJobQueue = averageJobQueue;
            this.averageJobNode = averageJobNode;
            this.arrival=arrival;
            this.departed=departed;
            this.endJobNumber = endJob;
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

        public double getUtilization() {
            return utilization;
        }
        public double getAverageJobQueue() {
            return averageJobQueue;
        }
        public double getAverageJobNode() {
            return averageJobNode;
        }

        public int getArrival() {
            return arrival;
        }

        public int getDeparted() {
            return departed;
        }

        public int getEndJobNumber() {
            return endJobNumber;
        }
    }
}

