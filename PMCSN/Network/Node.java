package Network;

import Generator.Rng;
import Server.Server;
import Simulation.Event.Event;
import Simulation.Handler.HandlerEvent;

import java.util.HashMap;

import static java.lang.Math.abs;

public class Node {

    private String idNode;
    private HashMap<String,Double> mapNextNodes ;
    private HandlerEvent handlerEvent;
    private Rng rng;

    public Node(HashMap<String, Double> mapNextNodes, HandlerEvent handlerEvent,String idNode) {
        this.mapNextNodes = mapNextNodes;
        this.handlerEvent = handlerEvent;
        this.idNode= idNode;
        this.rng = new Rng();
    }


    public HashMap<String, Double> getMapNextNodes() {
        return mapNextNodes;
    }

    public String getNextServerId(){
        double perc;
        double prevPerc = 1;
        double minPerc = 1;
        double maxPerc = 0;
        double actualPerc;

        String nextNode = null;
        String nextNodeMax = null;

        rng.selectStream((abs(this.mapNextNodes.hashCode())));
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

    public void handleEvent(Event event,double currentTime){
         this.handlerEvent.handle(currentTime,event);
    }

    public Server getServer(){
        return this.handlerEvent.getServer();
    }

    public String getId(){
        return this.idNode;
    }

}
