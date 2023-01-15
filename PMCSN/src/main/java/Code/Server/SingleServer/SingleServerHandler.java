package Code.Server.SingleServer;

import Code.Server.Interface.Server;
import Code.Generator.Rngs;
import Code.Network.Network;
import Code.Network.Node;
import Code.Simulation.Event.Event;
import Code.Server.Interface.HandlerEvent;
import Code.Simulation.Handler.SimulationHandler;

public class SingleServerHandler implements HandlerEvent {

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
            SimulationHandler.advanceTime(event.getTimeNext());

            //Se arriva un nuovo job al primo centro creo un nuovo arrivo
            if(singleServer.getId().contains("procura") && !SimulationHandler.stopSimulation && !event.isFeedbackFirstNode()){
                rngs.selectStream(singleServer.getStreamSimulation());

                nextTime = SimulationHandler.getCurrentTime() + rngs.exponential(singleServer.getMeanArrival());
                nextNode = network.getNodeById(singleServer.getId());
                nextEvent = new Event(nextNode, Event.Type.arrival,nextTime);

                SimulationHandler.insertEvent(nextEvent);
            }

            //Se il server ha un solo job posso generare l'evento del suo completamento
            if(singleServer.getJobNumbers() == 1){
                rngs.selectStream(singleServer.getStreamSimulation());
                nextTime = SimulationHandler.getCurrentTime()+ this.getService();
                nextNode = network.getNodeById(singleServer.getId());
                nextEvent = new Event(nextNode, Event.Type.completion,nextTime);

                SimulationHandler.insertEvent(nextEvent);
            }

        }

        //processo un completamento
        else{
            singleServer.processCompletition(event.getTimeNext(),currentTime);

            //avanzo tempo simulazione
            SimulationHandler.advanceTime(event.getTimeNext());

            //Dopo aver servito il job lo inoltro a un altro centro. Se il job esce dalla rete non faccio nessun operazione
            nextNode = network.getNextServer(singleServer.getId());
            nextEvent = new Event(nextNode, Event.Type.arrival, SimulationHandler.getCurrentTime());

            if(nextNode == null)
                this.singleServer.incrementEndJobNumber();
            else if(nextNode.getId().contains("procura"))
                nextEvent.setFeedbackFirstNode();

            SimulationHandler.insertEvent(nextEvent);


            //Se il centro contiene altri job in attesa genero un nuovo evento di completamento
            if(singleServer.getJobNumbers() > 0){

                nextTime = SimulationHandler.getCurrentTime() + getService();
                nextNode = network.getNodeById(singleServer.getId());
                nextEvent = new Event(nextNode, Event.Type.completion,nextTime);

                SimulationHandler.insertEvent(nextEvent);

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
