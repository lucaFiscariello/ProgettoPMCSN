package Simulation.Handler;

import Generator.Rngs;
import Network.Network;
import Network.Node;
import Server.Server;
import Simulation.Event.Event;
import Simulation.Simulation;

public class SingleServerHandler implements HandlerEvent{

    private Rngs rngs;
    private Network network;
    private Server singleServer;

    public SingleServerHandler(Server singleServer, Rngs rngs, Network network){
        this.rngs = rngs;
        this.network = network;
        this.singleServer = singleServer;
    }

    public void handle(double currentTime, Event event){

        Event nextEvent ;
        double nextTime;
        Node nextNode;

        //processo un arrivo
        if(event.getTypeEvent().equals(Event.Type.arrival) ) {
            singleServer.processArrival(event.getTimeNext(), currentTime);

            //avanzo tempo simulazione
            Simulation.advanceTime(event.getTimeNext());

            //Se arriva un nuovo job al primo centro creo un nuovo arrivo
            if(singleServer.getId().equals("id1") && !Simulation.stopSimulation && !event.isFeedbackFirstNode()){
                rngs.selectStream(singleServer.getStreamSimulation());

                nextTime = Simulation.getCurrentTime() + rngs.exponential(singleServer.getMeanArrival());
                nextNode = network.getNodeById(singleServer.getId());
                nextEvent = new Event(nextNode, Event.Type.arrival,nextTime);

                Simulation.insertEvent(nextEvent);
            }

            //Se il server ha un solo job posso generare l'evento del suo completamento
            if(singleServer.getJobNumbers() == 1){
                rngs.selectStream(singleServer.getStreamSimulation());
                nextTime = Simulation.getCurrentTime()+ this.getService();
                nextNode = network.getNodeById(singleServer.getId());
                nextEvent = new Event(nextNode, Event.Type.completion,nextTime);

                Simulation.insertEvent(nextEvent);
            }

        }

        //processo un completamento
        else{
            singleServer.processCompletition(event.getTimeNext(),currentTime);

            //avanzo tempo simulazione
            Simulation.advanceTime(event.getTimeNext());

            //Dopo aver servito il job lo inoltro a un altro centro. Se il job esce dalla rete non faccio nessun operazione
            nextNode = network.getNextServer(singleServer.getId());
            nextEvent = new Event(nextNode, Event.Type.arrival,Simulation.getCurrentTime());

            if(nextNode != null && nextNode.getId().equals("id1"))
                nextEvent.setFeedbackFirstNode();

            Simulation.insertEvent(nextEvent);


            //Se il centro contiene altri job in attesa genero un nuovo evento di completamento
            if(singleServer.getJobNumbers() > 0){

                nextTime = Simulation.getCurrentTime() + getService();
                nextNode = network.getNodeById(singleServer.getId());
                nextEvent = new Event(nextNode, Event.Type.completion,nextTime);

                Simulation.insertEvent(nextEvent);

            }
        }


    }

    public Server getServer(){
        return this.singleServer;
    }

    private double getService(){

        double service=0;

        rngs.selectStream(singleServer.getStreamSimulation());

        switch (this.singleServer.getDistribution()){
            case Exponential -> service = rngs.exponential(singleServer.getMeanService());
            case Pareto -> service = rngs.pareto(singleServer.getMeanService());
            case Deterministic -> service = rngs.deterministic(singleServer.getMeanService());
            case KErlang -> service = rngs.erlang(3,singleServer.getMeanService());

        }

        return service;
    }
}
