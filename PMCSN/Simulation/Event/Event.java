package Simulation.Event;

import Network.Node;
import Server.Server;

public class Event {

    public enum Type {
        arrival,
        completion
    }

    private Node nextNode;
    private Type typeEvent;
    private double timeNext;

    public Event(Node nextNode, Type typeEvent, double time) {
        this.nextNode = nextNode;
        this.typeEvent = typeEvent;
        this.timeNext = time;
    }

    public Node getNode() {
        return nextNode;
    }

    public Type getTypeEvent() {
        return typeEvent;
    }

    public double getTimeNext() {
        return timeNext;
    }

    public void setTime(long time) {
        this.timeNext = time;
    }

}


