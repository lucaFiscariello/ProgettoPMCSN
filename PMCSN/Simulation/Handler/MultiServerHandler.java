package Simulation.Handler;

import Generator.Rngs;
import Network.Network;
import Network.Node;
import Server.Server;
import Server.MultiServer;
import Simulation.Event.Event;
import Simulation.Simulation;

public class MultiServerHandler implements HandlerEvent{

    private Rngs rngs;
    private Network network;
    private MultiServer multiserver;

    public MultiServerHandler(Server multiServer, Rngs rngs, Network network){
        this.rngs = rngs;
        this.network = network;
        this.multiserver = (MultiServer) multiServer;
    }


    public void handle(double currentTime, Event event){

        Event nextEvent;
        double nextTime;
        Node nextNode;

        //processo un arrivo
        if(event.getTypeEvent().equals(Event.Type.arrival)) {
            multiserver.processArrival(event.getTimeNext(), currentTime);

            //avanzo tempo simulazione
            Simulation.advanceTime(event.getTimeNext());

            //Se arriva un nuovo job al primo centro creo un nuovo arrivo
            if(multiserver.getId().equals("id1")  && !Simulation.stopSimulation){
                rngs.selectStream(multiserver.getStreamSimulation());
                nextTime = Simulation.getCurrentTime() + rngs.exponential(multiserver.getMeanArrival());
                nextNode = network.getNodeById(multiserver.getId());
                nextEvent = new Event(nextNode, Event.Type.arrival,nextTime);

                Simulation.insertEvent(nextEvent);
            }

            // Se ci sono server liberi è possibile processare un nuovo job
            if(multiserver.hasAnyServerIdle()){
                rngs.selectStream(multiserver.getStreamSimulation());
                nextTime = Simulation.getCurrentTime() + rngs.exponential(multiserver.getMeanService());
                nextNode = network.getNodeById(multiserver.getId());
                nextEvent = new Event(nextNode, Event.Type.completion,nextTime);

                Simulation.insertEvent(nextEvent);
            }

        }

        //processo un completamento
        else{

            multiserver.processCompletition(event.getTimeNext(),currentTime);

            //avanzo tempo simulazione
            Simulation.advanceTime(event.getTimeNext());

            //Dopo aver servito il job lo inoltro a un altro centro. Se il job esce dalla rete non faccio nessun operazione
            nextNode = network.getNextServer(multiserver.getId());

            if(nextNode != null){
                nextEvent = new Event(nextNode, Event.Type.arrival,Simulation.getCurrentTime());
                Simulation.insertEvent(nextEvent);
            }

            //Se il centro contiene altri job in attesa genero un nuovo evento di completamento
            if(multiserver.getJobNumbers() > 0){
                rngs.selectStream(multiserver.getStreamSimulation());
                nextTime = Simulation.getCurrentTime() + rngs.exponential(multiserver.getMeanService());
                nextNode = network.getNodeById(multiserver.getId());
                nextEvent = new Event(nextNode, Event.Type.completion,nextTime);

                Simulation.insertEvent(nextEvent);

            }
        }

    }

    public Server getServer(){
        return this.multiserver;
    }
}
