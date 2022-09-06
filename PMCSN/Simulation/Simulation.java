package Simulation;

import Simulation.Event.BinaryHeap;
import Simulation.Event.Event;

public class Simulation {
    public static double currentTime=0;
    public static BinaryHeap allEvents = new BinaryHeap(20000);

    public static void advanceTime(double nextTime){
        currentTime=nextTime;
    }

    public static double  getCurrentTime(){
        return currentTime;
    }

    public static void insertEvent(Event e){
        allEvents.insertEvent(e);
        System.out.println("Evento inserito: " + e.getTimeNext() + " " + e.getTypeEvent() +" "+ e.getNode().getId());
    }

    public static Event extractMinEvent(){
        return allEvents.extractMin();
    }

}
