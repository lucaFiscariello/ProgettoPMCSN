package Server;

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

    public void saveStats();

    public Statistics getStatistics();

    public Distribution getDistribution();

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

        public Statistics(double averageServiceTime, double averageDelay, double averageWait, double utilization, double averageJobQueue, double averageJobNode) {
            this.averageServiceTime = averageServiceTime;
            this.averageDelay = averageDelay;
            this.averageWait = averageWait;
            this.utilization = utilization;
            this.averageJobQueue = averageJobQueue;
            this.averageJobNode = averageJobNode;
        }

        public double getAverageServiceTime() {
            return averageServiceTime;
        }

        public void setAverageServiceTime(double averageServiceTime) {
            this.averageServiceTime = averageServiceTime;
        }

        public double getAverageDelay() {
            return averageDelay;
        }

        public void setAverageDelay(double averageDelay) {
            this.averageDelay = averageDelay;
        }

        public double getAverageWait() {
            return averageWait;
        }

        public void setAverageWait(double averageWait) {
            this.averageWait = averageWait;
        }

        public double getUtilization() {
            return utilization;
        }

        public void setUtilization(double utilization) {
            this.utilization = utilization;
        }

        public double getAverageJobQueue() {
            return averageJobQueue;
        }

        public void setAverageJobQueue(double averageJobQueue) {
            this.averageJobQueue = averageJobQueue;
        }

        public double getAverageJobNode() {
            return averageJobNode;
        }

        public void setAverageJobNode(double averageJobNode) {
            this.averageJobNode = averageJobNode;
        }
    }
}

