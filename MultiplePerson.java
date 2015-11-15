package project;

import java.sql.Time;
import java.util.*;

public class MultiplePerson extends Elevator{

    Queue<PassengerRequest> elevatorQueue = new LinkedList<PassengerRequest>();
    private int count = 0;

        public MultiplePerson(int capacity, int timeMoveOneFloor,
                                 int floors, int doorDelta, boolean verbose) {
            super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);

        }

        public void initialize(Queue<PassengerRequest> requests) {
            servingQueue = requests;
        }

        public boolean continueOperate() {
            if(this.servingQueue.isEmpty()) {
			    return false;
		    } else {
                return true;
            }
    }

        public ArrayList<PassengerReleased> operate() {
            ArrayList<PassengerReleased> released = new ArrayList<PassengerReleased>();
            if (verbose) {
                System.out.println("Floor at / floor from / floor to | Requested / arrived");
            }

            while (this.continueOperate()) {
                ArrayList<PassengerReleased> moved = this.move();
                released.addAll(moved);
            }
            return released;
        }

    public ArrayList<PassengerReleased> move() {
        ArrayList<PassengerReleased> released =
                new ArrayList<PassengerReleased>();

        PassengerRequest request = this.servingQueue.remove();

        long currentTime = Math.max(this.currentTime.getTime(),
                request.getTimePressedButton().getTime()); // whichever happened latest

        long timeInMiliseconds = currentTime +
                this.doorDelta*1000 +  // delta to open AND close the door to let the passenger in
                1000*this.timeMoveOneFloor* (Math.abs(currentFloor - request.getFloorFrom()))+
                // time to get to the passenger's floor
                1000*this.timeMoveOneFloor* (Math.abs(request.getFloorFrom() - request.getFloorTo()))+
                // time to move to the destination
                this.doorDelta*1000; // delta to open AND close the door to let the passenger out

        /* PassengerReleased requestReleased = new PassengerReleased(request,
                new Time(timeInMiliseconds));

        released.add(requestReleased); */
        for (int j = request.getFloorFrom(); j < request.getFloorTo(); j++) {

            for (PassengerRequest item : servingQueue) {
                if (j == item.getFloorTo()) {
                    timeInMiliseconds = timeInMiliseconds + this.doorDelta * 1000 +
                            1000 * this.timeMoveOneFloor * (Math.abs(currentFloor - request.getFloorFrom())) +
                            1000*this.timeMoveOneFloor* (Math.abs(request.getFloorFrom() - request.getFloorTo()))+
                            this.doorDelta*1000;

                    PassengerReleased requestReleased = new PassengerReleased(request,
                            new Time(timeInMiliseconds));

                    released.add(requestReleased);

                    if (verbose) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(currentFloor+" / " + request.getFloorFrom() + " / " +request.getFloorTo()+" | ");
                        sb.append(request.getTimePressedButton() + " / " +
                                new Time(timeInMiliseconds));

                        System.out.println(new String(sb));
                    }
                    this.currentTime.setTime(timeInMiliseconds);
                    currentFloor = request.getFloorTo();
                    servingQueue.remove(item);
                    return released;
                }
            }

        }

        /*if (verbose) {
            StringBuilder sb = new StringBuilder();
            sb.append(currentFloor+" / " + request.getFloorFrom() + " / " +request.getFloorTo()+" | ");
            sb.append(request.getTimePressedButton() + " / " +
                    new Time(timeInMiliseconds));

            System.out.println(new String(sb));
        }
        this.currentTime.setTime(timeInMiliseconds);
        currentFloor = request.getFloorTo();*/
        return released;
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
        int count = numberOfPassangers;
        Queue<PassengerRequest> elevatorQueue = new LinkedList<PassengerRequest>();

        int tempPassenger = numberOfPassangers;

        for (int i = 0; i < numberOfPassangers; i++) {
            int floor_from = rnd.nextInt(floors) + 1; // to generate numbers from [1, floords]
            int floor_to   = rnd.nextInt(floors) + 1;

            int weight = rnd.nextInt(maxWeight);

            int seconds = rnd.nextInt(maxSeconds);
            currentTime+=seconds * 1000;
            Time time = new Time(currentTime);
            PassengerRequest request = new PassengerRequest(time, floor_from,
                    floor_to, weight);
            elevatorQueue.add(request);
        }

        Elevator elevator = new OnePersonElevator(capacity,
                secondsPerFloor,floors, timeOpenDoors, verbose);

        elevator.initialize(elevatorQueue);

        ArrayList<PassengerReleased> output = elevator.operate();

        long cost = 0;

        for (int i = 0; i < output.size(); i++) {

            PassengerReleased passenger = output.get(i);
            Time timeRequested = passenger.getTimeArrived();
            Time timeLeft = passenger.getPassengerRequest().getTimePressedButton();

            cost+=Math.abs(timeLeft.getTime() - timeRequested.getTime());
        }

        System.out.println("Total cost (in seconds): " + cost/ 1000);
    }


