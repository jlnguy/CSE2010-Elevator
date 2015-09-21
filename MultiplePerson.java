package project;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MultiplePerson extends Elevator {

    public MultiplePerson(int capacity, int timeMoveOneFloor,
                          int floors, int doorDelta, boolean verbose) {
        super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);
    }

    public boolean continueOperate() {
        return true;
    }

    public ArrayList<PassengerReleased> move() {
        ArrayList<PassengerReleased> released =
                new ArrayList<PassengerReleased>();
        return released;
    }

    public void initialize(Queue<PassengerRequest> requests) {
        servingQueue = requests;
    }

    public static void main(final String[] args) {
        Random rnd = new Random(0);
        Time startingTime = new Time(8,0,0);   // 8 am

        int maxWeight = 250;
        int capacity = maxWeight * 4;
        int floors = 9;
        int secondsPerFloor = 10; // Between each floor, there is a 10 second wait.
        int timeOpenDoors = 15; // 15 seconds to open the door
        long currentTime = startingTime.getTime();
        int maxSeconds = 200;
        boolean verbose = true;
        int numberOfPassangers = 15;  		   // number of passengers to generate
        Queue<PassengerRequest> elevatorQueue = new LinkedList<PassengerRequest>();

        int tempPassenger = numberOfPassangers;
        
    }
}
