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

    private double arc = 0;
    private double perc = 0;

    public MultiServerHandler(Server multiServer, Rngs rngs, Network network){
        this.rngs = rngs;
        this.network = network;
        this.multiserver = (MultiServer) multiServer;
    }


    public void handle(double currentTime, Event event){

        Event nextEvent;
        Node nextNode;
        double nextTime;
        double service;

        //processo un arrivo
        if(event.getTypeEvent().equals(Event.Type.arrival)) {

            multiserver.processArrival(event.getTimeNext(), currentTime);

            //avanzo tempo simulazione
            Simulation.advanceTime(event.getTimeNext());

            // Se ci sono server liberi è possibile processare un nuovo job
            if(multiserver.getJobNumbers()-1<multiserver.getServerNumber()){
                service = this.getService();
                nextTime = Simulation.getCurrentTime() + service ;
                nextNode = network.getNodeById(multiserver.getId());
                nextEvent = new Event(nextNode, Event.Type.completion,nextTime);
                nextEvent.setService(service);

                Simulation.insertEvent(nextEvent);
            }

            //Se arriva un nuovo job al primo centro creo un nuovo arrivo
            if(multiserver.getId().equals("id1")  && !Simulation.stopSimulation && !event.isFeedbackFirstNode()){

                rngs.selectStream(multiserver.getStreamSimulation());
                nextTime = Simulation.getCurrentTime() + rngs.exponential(multiserver.getMeanArrival());
                nextNode = network.getNodeById(multiserver.getId());
                nextEvent = new Event(nextNode, Event.Type.arrival,nextTime);

                Simulation.insertEvent(nextEvent);
            }

        }

        //processo un completamento
        else{
            if(multiserver.getJobNumbers()>0){

                int jobsQueue = multiserver.getQueueJobs();

                multiserver.processCompletition(event.getTimeNext(),event.getService());
                //avanzo tempo simulazione
                Simulation.advanceTime(event.getTimeNext());

                //Dopo aver servito il job lo inoltro a un altro centro. Se il job esce dalla rete non faccio nessun operazione
                nextNode = network.getNextServer(multiserver.getId());
                nextEvent = new Event(nextNode, Event.Type.arrival,Simulation.getCurrentTime());

                if(nextNode != null && nextNode.getId().equals("id1"))
                    nextEvent.setFeedbackFirstNode();

                Simulation.insertEvent(nextEvent);


                //Se il centro contiene altri job in attesa genero un nuovo evento di completamento
                if(jobsQueue> 0){
                    service = this.getService();
                    nextTime = Simulation.getCurrentTime() + service ;
                    nextNode = network.getNodeById(multiserver.getId());
                    nextEvent = new Event(nextNode, Event.Type.completion,nextTime);
                    nextEvent.setService(service);

                    Simulation.insertEvent(nextEvent);

                }
            }

        }

    }

    public Server getServer(){
        return this.multiserver;
    }

    private double getService(){

        double service=0;

        rngs.selectStream(multiserver.getStreamSimulation());

        switch (this.multiserver.getDistribution()){
            case Exponential -> service = rngs.exponential(multiserver.getMeanService());
            case Pareto -> service = rngs.pareto(multiserver.getMeanService());
            case Deterministic -> service = rngs.deterministic(multiserver.getMeanService());
            case KErlang -> service = rngs.erlang(3,multiserver.getMeanService());
        }

        return service;
    }
}
