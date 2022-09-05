package Server;

public interface Server {

    public void processArrival(double timeNext, double timeCurrent);

    public void processCompletition(double timeNext, double timeCurrent);

    public int getStreamSimulation();

    public double getMeanService();

    public int getJobNumbers();

    public String getId();
}

