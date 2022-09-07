package Network;

import Generator.Rngs;
import Server.Server;
import Server.SingleServer;
import Server.MultiServer;
import Simulation.Event.Event;
import Simulation.Handler.HandlerEvent;
import Simulation.Handler.MultiServerHandler;
import Simulation.Handler.SingleServerHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class Network {
    private HashMap<String,Node> allNode;

    public Network(String configurationPath, Rngs rngs) throws IOException, ParseException{
        this.allNode = new HashMap();

        //Leggo file configurazione
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(configurationPath));
        JSONObject jsonConfiguration =  (JSONObject) obj;

        JSONArray allIdList = (JSONArray) jsonConfiguration.get("totalId");
        Iterator<String> idIterator = allIdList.iterator();

        //Scorro tutti i centri della rete
        while (idIterator.hasNext()){

            //Mappa contenente le probabilità di routing di un centro specifico
            HashMap<String,Double> allConnectionProbability = new HashMap<>();
            // identificativo del centro
            String idNode = idIterator.next();

            JSONObject jsonNode = (JSONObject) jsonConfiguration.get(idNode);
            HandlerEvent handlerEvent = null;
            Server server;
            double meanArrival = 0;

            //Dati che caratterizzano il server. Type può essere single server o muti server
            long initialjobNumbers = (long) jsonNode.get("initialjobNumbers");
            long streamSimulation = (long) jsonNode.get("streamSimulation");
            double meanService = (double) jsonNode.get("meanService");
            String typeServer = (String) jsonNode.get("type");

            //Solo per il primo centro sono interessato a conoscere il tasso di arrivo medio
            if(jsonNode.containsKey("meanArrival"))
                meanArrival = (double) jsonNode.get("meanArrival");

            if(typeServer.equals("single server")){
                server = new SingleServer((int)initialjobNumbers,(int)streamSimulation, meanService,idNode,meanArrival);
                handlerEvent = new SingleServerHandler(server,rngs ,this);
            }
            else{
                long numberServer = (long) jsonNode.get("numberServer");
                server = new MultiServer((int)initialjobNumbers,(int)streamSimulation, meanService,idNode,meanArrival,(int)numberServer);
                handlerEvent = new MultiServerHandler(server,rngs ,this);
            }

            JSONArray  totalConnectionList = (JSONArray ) jsonNode.get("totalConnection");
            JSONObject jsonConnection= (JSONObject) jsonNode.get("connection");
            Iterator<String> connectionIterator = totalConnectionList.iterator();

            //Creo la mappa contenente le probabilità di routing
            while (connectionIterator.hasNext()){
                String idServer = connectionIterator.next();
                double perc = (double) jsonConnection.get(idServer);
                allConnectionProbability.put(idServer,perc);
            }

            //Aggiungo un nodo della rete alla lista di tutti i nodi
            Node newNode = new Node(allConnectionProbability,handlerEvent,idNode) ;
            this.allNode.put(idNode,newNode);

        }

    }

    /*
    Questo metodo riceve in ingresso l'identificatore di un centro e restituisce in output il riferimento al server successivo.
    Ad esempio se un job è servito dal cento A e poi ha la possibilità di andare ai centri B,C,D con una certa probabilità,
    questo metodo individua il centro successivo a cui inviare il job tenendo conto delle probabilità di routing.
     */
    public Node getNextServer(String idServerPrev){
        Node prevNode = this.allNode.get(idServerPrev);
        String idNextNode = prevNode.getNextServerId();

        return this.allNode.get(idNextNode);
    }

    public HashMap<String,Node> getAllNode(){
        return this.allNode;
    }

    public Event getFirstEvent(){
        return new Event(allNode.get("id1"), Event.Type.arrival,1.0);
    }


    //Questo metodo si occupa di delegare la gestione dell'evento al nodo della rete(server) interessato.
    public void handleEvent(Event event,double currentTime){
        //Nodo che deve gestire l'evento
        Node node = event.getNode();
        node.handleEvent(event,currentTime);
    }

    public Node getNodeById(String id){
        return this.allNode.get(id);
    }


}
