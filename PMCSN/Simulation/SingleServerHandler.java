package Simulation;

import Generator.Rngs;
import Network.Network;
import Server.Server;

public class SingleServerHandler implements HandlerEvent{

    private Rngs rngs;
    private Network network;

    public SingleServerHandler(Rngs rngs, Network network){
        this.rngs = rngs;
        this.network = network;
    }

    public Event handle(Server singleServer, double currentTime, Event event){

        Event nextEvent = null;
        double nextTime;
        Server nextServer;

        //processo un arrivo
        if(event.getTypeEvent().equals(Event.Type.arrival)) {
            singleServer.processArrival(event.getTimeNext(), currentTime);

            //Se il server ha un solo job posso servirlo immediatamente
            if(singleServer.getJobNumbers() == 1){
                rngs.selectStream(singleServer.getStreamSimulation());
                nextTime = currentTime+ rngs.exponential(singleServer.getMeanService());
                nextServer = network.getNextServer(singleServer.getId());
                nextEvent = new Event(nextServer, Event.Type.arrival,nextTime);
            }

        }

        //processo un completamento
        else{
            singleServer.processCompletition(event.getTimeNext(),currentTime);

            if(singleServer.getJobNumbers() > 0){
                rngs.selectStream(singleServer.getStreamSimulation());
                nextTime = currentTime+ rngs.exponential(singleServer.getMeanService());
                nextServer = network.getNextServer(singleServer.getId());
                nextEvent = new Event(nextServer, Event.Type.completion,nextTime);
            }
        }

        //avanzo tempo simulazione
        currentTime = event.getTimeNext();

        return nextEvent;
    }
}
