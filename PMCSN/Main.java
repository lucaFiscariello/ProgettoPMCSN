import Generator.Rngs;
import Network.Network;
import Network.Node;
import Simulation.Event.Event;
import Simulation.Simulation;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Rngs rngs = new Rngs();
        rngs.plantSeed(100);

        Network network = new Network("src/main/java/ConfigurationRete.json",rngs);
        Simulation.insertEvent(network.getFirstEventArrival());

        Event nextEvent ;
        int runNumber = 3000000;

        while(network.getJobsArrived()<runNumber){
            nextEvent = Simulation.extractMinEvent();
            network.handleEvent(nextEvent, Simulation.getCurrentTime());
            System.out.println((double) network.getJobsArrived()/runNumber);
        }

        Simulation.stopSimulation();

        while(Simulation.getRemainEvents()>0){
            nextEvent = Simulation.extractMinEvent();
            network.handleEvent(nextEvent, Simulation.getCurrentTime());
            System.out.println(Simulation.getRemainEvents());
        }

        for (Node node: network.getAllNode().values()) {
            node.getServer().printStats();
        }

    }


}
