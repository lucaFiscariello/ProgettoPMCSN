package Simulation;

import Simulation.Event.BinaryHeap;
import Simulation.Event.Event;

public class Simulation {

    public static boolean stopSimulation= false;
    public static double currentTime=0.0;
    public static BinaryHeap allEvents = new BinaryHeap(200000000);

    public static void advanceTime(double nextTime){
        currentTime=nextTime;
    }

    public static double  getCurrentTime(){
        return currentTime;
    }

    public static void insertEvent(Event e){
        allEvents.insertEvent(e);

    }

    public static Event extractMinEvent(){
        return allEvents.extractMin();
    }

    public static void stopSimulation(){
        stopSimulation = true;
    }

    public static int getRemainEvents(){
        return allEvents.getHeapSize();
    }

}
