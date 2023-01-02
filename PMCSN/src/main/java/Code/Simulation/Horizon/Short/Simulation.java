package Code.Simulation.Horizon.Short;
import Code.Network.Node;
import Code.Simulation.Event.Event;
import Code.Simulation.Handler.SimulationHandler;
import Code.Generator.Rngs;
import Code.Network.Network;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Simulation {

    public static void simule(int stop, String configuration,int seed) throws IOException, ParseException {
        Rngs rngs = new Rngs();
        rngs.plantSeed(seed);
        SimulationHandler.setStop(stop);

        Network network = new Network(configuration,rngs);
        network.initialize();

        Event nextEvent;
        Node nextNode;

        while(SimulationHandler.cointinue()){
            nextEvent = SimulationHandler.extractMinEvent();
            nextNode = nextEvent.getNode();
            nextNode.handleEvent(nextEvent, SimulationHandler.getCurrentTime());
        }

        for (Node node: network.getAllNode().values()) {
            node.getServer().cleanAndSaveStats();
            node.getServer().printStats();
        }
    }

}
