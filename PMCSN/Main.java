import Generator.Rngs;
import Network.Network;
import Network.Node;
import Simulation.Event.Event;
import Simulation.Simulation;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException, JSONException {
        Rngs rngs = new Rngs();
        rngs.plantSeed(123456789);

        Network network = new Network("src/main/java/Configuration.json",rngs);

        Event firstEvent = network.getFirstEvent();
        Event nextEvent = firstEvent;
        Simulation.insertEvent(firstEvent);


        for(int i=0 ; i<30; i++){
            System.out.println(nextEvent.getTimeNext());
            System.out.println(nextEvent.getTypeEvent());
            System.out.println(nextEvent.getNode().getId());
            System.out.println("-----");


            network.handleEvent(nextEvent, Simulation.getCurrentTime());
            nextEvent = Simulation.extractMinEvent();
        }

        for (Node node: network.getAllNode().values()) {
            node.getServer().printStats();
        }


    }


}
