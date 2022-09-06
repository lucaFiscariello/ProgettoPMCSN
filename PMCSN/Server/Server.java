package Server;

public interface Server {

    public void processArrival(double timeNext, double timeCurrent);

    public void processCompletition(double timeNext, double timeCurrent);

    public int getStreamSimulation();

    public double getMeanService();
    public double getMeanArrival();

    public int getJobNumbers();

    public String getId();

    public void printStats();
}

