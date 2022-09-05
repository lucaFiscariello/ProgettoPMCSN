package Simulation;

import Server.Server;

public interface HandlerEvent {
    public Event handle(Server singleServer, double currentTime, Event event);
}

