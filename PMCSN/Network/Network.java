package Network;

import Server.Server;
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

    public Network(String configurationPath) throws IOException, ParseException{
        this.allNode = new HashMap();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(configurationPath));
        JSONObject jsonConfiguration =  (JSONObject) obj;

        JSONArray allIdList = (JSONArray) jsonConfiguration.get("totalId");
        Iterator<String> idIterator = allIdList.iterator();

        while (idIterator.hasNext()){
            HashMap<String,Double> allConnectionProbability = new HashMap<>();
            String idNode = idIterator.next();
            JSONObject jsonNode = (JSONObject) jsonConfiguration.get(idNode);

            long initialjobNumbers = (long) jsonNode.get("initialjobNumbers");
            long streamSimulation = (long) jsonNode.get("streamSimulation");
            double meanService = (double) jsonNode.get("meanService");

            JSONArray  totalConnectionList = (JSONArray ) jsonNode.get("totalConnection");
            JSONObject jsonConnection= (JSONObject) jsonNode.get("connection");
            Iterator<String> connectionIterator = totalConnectionList.iterator();

            while (connectionIterator.hasNext()){
                String idServer = connectionIterator.next();
                double perc = (double) jsonConnection.get(idServer);
                allConnectionProbability.put(idServer,perc);
            }

            Node newNode = new Node((int)initialjobNumbers,(int)streamSimulation,(double)meanService,idNode,allConnectionProbability) ;
            this.allNode.put(idNode,newNode);

        }

    }

    public Server getNextServer(String idServerPrev){
        Node prevNode = this.allNode.get(idServerPrev);
        String idNextNode = prevNode.getNextServerId();

        return this.allNode.get(idNextNode).getServer();
    }

}
