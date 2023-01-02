package Code.Server.Interface;

import Code.Server.Interface.Server;
import Code.Simulation.Event.Event;

public interface HandlerEvent {
    public void handle(double currentTime, Event event);
    public Server getServer();
}

