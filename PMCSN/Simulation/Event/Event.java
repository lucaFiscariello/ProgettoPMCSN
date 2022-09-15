package Simulation.Event;

import Network.Node;

public class Event {

    public enum Type {
        arrival,
        completion
    }

    private Node nextNode;
    private Type typeEvent;
    private double timeNext;
    private double service;
    private boolean feedbackFirstNode = false;

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

    public void setFeedbackFirstNode() {
        this.feedbackFirstNode = true;
    }

    public boolean isFeedbackFirstNode() {
        return feedbackFirstNode;
    }

    public void setService(double service){
        this.service = service;
    }

    public double getService(){
        return this.service;
    }

    @Override
    public String toString() {
        return "Event{" +
                "nextNode=" + ((nextNode==null)?"null":nextNode.getId()) +
                ", typeEvent=" + typeEvent +
                ", timeNext=" + timeNext +
                ", feedbackFirstNode=" + feedbackFirstNode +
                '}';
    }
}


