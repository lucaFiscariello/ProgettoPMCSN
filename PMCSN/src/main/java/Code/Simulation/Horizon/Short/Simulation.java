package Code.Simulation.Horizon.Short;
import Code.Network.Node;
import Code.Server.Interface.Server;
import Code.Simulation.Event.Event;
import Code.Simulation.Handler.SimulationHandler;
import Code.Generator.Rngs;
import Code.Network.Network;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulation {

    public static void simule(int stop, String configuration,int seed,String serverOfInteress) throws IOException, ParseException {

        //Inizializzo generatore pseudo-random
        Rngs rngs = new Rngs();
        rngs.plantSeed(seed);
        SimulationHandler.setStop(stop);

        //Inizializzo L'handler della simulazione per raccogliere dati di un server specifico
        Network network = new Network(configuration,rngs);
        Server server= network.getNodeById(serverOfInteress).getServer();
        SimulationHandler.inizializeDataCollector(server,stop,configuration);

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
