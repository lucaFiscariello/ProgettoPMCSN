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
        rngs.plantSeed(0);

        Network network = new Network("src/main/java/Configuration3.json",rngs);

        Event firstEvent = network.getFirstEvent();
        Event nextEvent ;
        Simulation.insertEvent(firstEvent);

        for(int i=0 ; i<10000; i++){
            nextEvent = Simulation.extractMinEvent();
            network.handleEvent(nextEvent, Simulation.getCurrentTime());
        }

        Simulation.stopSimulation();

        while(Simulation.getRemainEvents()>0){
            nextEvent = Simulation.extractMinEvent();
            network.handleEvent(nextEvent, Simulation.getCurrentTime());
        }

        for (Node node: network.getAllNode().values()) {
            node.getServer().printStats();
        }



    }


}
