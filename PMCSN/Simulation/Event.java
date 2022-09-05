package Simulation;

import Server.Server;

public class Event {

    public enum Type {
        arrival,
        completion
    }

    private Server nextServer;
    private Type typeEvent;
    private double timeNext;

    public Event(Server nextServer, Type typeEvent, double time) {
        this.nextServer = nextServer;
        this.typeEvent = typeEvent;
        this.timeNext = time;
    }

    public Server getNextServer() {
        return nextServer;
    }

    public void setNextServer(Server nextServer) {
        this.nextServer = nextServer;
    }

    public Type getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(Type typeEvent) {
        this.typeEvent = typeEvent;
    }

    public double getTimeNext() {
        return timeNext;
    }

    public void setTime(long time) {
        this.timeNext = time;
    }

}


