package Code.Simulation.Horizon.Long;

import Code.Generator.Rngs;
import Code.Network.Node;
import Code.Simulation.Event.Event;
import Code.Simulation.Handler.SimulationHandler;
import Code.Network.Network;
import org.json.simple.parser.ParseException;


import java.io.IOException;

public class BatchMeans {
    private int k;
    private int b;
    private int seed;

    private Network network;
    private Rngs rngs;

    public BatchMeans(int k, int b, String configuration) throws IOException, ParseException {

        this.k = k;
        this.b = b;
        this.seed = 0;
        this.rngs = new Rngs();
        this.network = new Network(configuration,rngs);

    }

    public void simule() throws IOException {
        Rngs rngs = new Rngs();
        rngs.plantSeed(100);
        SimulationHandler.setStop(k*b);

        //Permette di evitare di salvare più volte le stesse statistiche.
        int last=0;



        network.initialize();

        Event nextEvent;
        Node nextNode;

        while(SimulationHandler.cointinue()){
            nextEvent = SimulationHandler.extractMinEvent();
            nextNode = nextEvent.getNode();
            nextNode.handleEvent(nextEvent, SimulationHandler.getCurrentTime());

            //Al termine di ogni batch memorizzo le statistiche e cancello quelle precedenti
            if(SimulationHandler.getJobsDepar()%b==0 && SimulationHandler.getJobsDepar()!=0 && last!=SimulationHandler.getJobsDepar() ){

                for (Node node: network.getAllNode().values()) {
                    node.getServer().cleanAndSaveStats();
                    node.getServer().printStats();
                }

                last=SimulationHandler.getJobsDepar();
            }
        }


    }



}
