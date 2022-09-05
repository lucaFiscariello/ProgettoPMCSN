package Network;

import Generator.Rng;
import Server.Server;
import Server.SingleServer;

import java.util.HashMap;

public class Node {
    private int initialjobNumbers ;
    private int streamSimulation;
    private double meanService;
    private String id;
    private Rng rng;
    private Server server;

    private HashMap<String,Double> mapNextNodes ;

    public Node(int initialjobNumbers, int streamSimulation, double meanService, String id, HashMap<String, Double> mapNextNodes) {
        this.initialjobNumbers = initialjobNumbers;
        this.streamSimulation = streamSimulation;
        this.meanService = meanService;
        this.id = id;
        this.mapNextNodes = mapNextNodes;

        this.rng = new Rng();
        this.server = new SingleServer(streamSimulation, meanService,id);

    }

    public int getInitialjobNumbers() {
        return initialjobNumbers;
    }

    public void setInitialjobNumbers(int initialjobNumbers) {
        this.initialjobNumbers = initialjobNumbers;
    }

    public int getStreamSimulation() {
        return streamSimulation;
    }

    public Server getServer() {
        return this.server;
    }

    public void setStreamSimulation(int streamSimulation) {
        this.streamSimulation = streamSimulation;
    }

    public double getMeanService() {
        return meanService;
    }

    public void setMeanService(double meanService) {
        this.meanService = meanService;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Double> getMapNextNodes() {
        return mapNextNodes;
    }

    public void setMapNextNodes(HashMap<String, Double> mapNextNodes) {
        this.mapNextNodes = mapNextNodes;
    }

    public String getNextServerId(){
        double perc;
        double prevPerc = 1;
        double minPerc = 1;
        double maxPerc = 0;
        double actualPerc;

        String nextNode = null;
        String nextNodeMax = null;

        rng.selectStream(this.id.hashCode());
        perc = rng.random();

        /*Genero un numero random compreso tra 0 e 1 e lo confronto con le probabilità di routing
          per individuare il centro succesivo. Ad esempio se le probabilità di routing sono solo due
          con percentuali 0.7 e 0.3 e se viene generato come numero random 0.5 un job verrà inoltrato al
          centro con probabilità di routing pari a 0.7 */
        for (String idNode: mapNextNodes.keySet()){
            actualPerc = mapNextNodes.get(idNode);

            if( actualPerc<minPerc)
                minPerc = actualPerc;

            if( actualPerc>maxPerc){
                maxPerc = actualPerc;
                nextNodeMax = idNode;
            }

            if(perc<minPerc && actualPerc<prevPerc){
                 nextNode = idNode;
            }

            prevPerc = actualPerc;

        }

        return (nextNode==null)? nextNodeMax:nextNode;

    }

}
