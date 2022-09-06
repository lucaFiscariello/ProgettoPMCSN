package Simulation.Handler;

import Server.Server;
import Simulation.Event.Event;

public interface HandlerEvent {
    public void handle(double currentTime, Event event);
    public Server getServer();
}

