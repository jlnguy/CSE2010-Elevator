package project;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MultiplePersonElevator extends Elevator {
	
	public MultiplePersonElevator(int capacity, int timeMoveOneFloor, 
			int floors, int doorDelta, boolean verbose) {
		super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);

	}
	
	private int targetFloor;
	
	public void initialize(ArrayList<Queue<PassengerRequest>> requestsUp, 
										ArrayList<Queue<PassengerRequest>> requestsDown) {
		servingQueueUp = requestsUp;
		servingQueueDown = requestsDown;
		//riders = new ArrayList<PassengerRequest>();
	}
	
	public ArrayList<PassengerReleased> move() {
		
		Time aux = new Time(23, 59, 59);
		long earlyTime = aux.getTime();
		int earlyFloor = currentFloor;
		boolean earlyDirection = true;  /* true is up and false is down */
		int i;
		long currentTimeInMiliseconds;
		PassengerRequest curr, request;

		if (!continueOperate()) return null;
		
		ArrayList<PassengerReleased> released =
					new ArrayList<PassengerReleased>();
		
		/* find the first request: compare the timePressedButton 
		 * of the first person of each queue */
		for (i = 0; i < floors; i++)	{
			curr = servingQueueUp.get(i).peek();
			if (curr != null){
				if (curr.getTimePressedButton().getTime() < earlyTime)	{
					earlyTime = curr.getTimePressedButton().getTime();
					earlyFloor = curr.getFloorFrom();
					earlyDirection = true;
				}
			}
			
			curr = servingQueueDown.get(i).peek();
			if (curr != null) {
				if (curr.getTimePressedButton().getTime() < earlyTime)	{
					earlyTime = curr.getTimePressedButton().getTime();
					earlyFloor = curr.getFloorFrom();
					earlyDirection = false;
				}
			}
		}
		
		System.out.println("EarlyTime/EarlyFloor = " + new Time(earlyTime) + "/" + earlyFloor);
		
		/* the elevator goes to that floor without stoping */
		if (earlyDirection)	{
			request = this.servingQueueUp.get(earlyFloor-1).remove();
		}	else	{
			request = this.servingQueueDown.get(earlyFloor-1).remove();
		}			
		currentTimeInMiliseconds = Math.max(this.currentTime.getTime(),
								request.getTimePressedButton().getTime()); // whichever happened latest
		currentTimeInMiliseconds = currentTimeInMiliseconds + 
		this.doorDelta*1000 +  // delta to open the door
		1000*this.timeMoveOneFloor* (Math.abs(currentFloor -request.getFloorFrom()));
		// time to get to the passenger's floor
		
		/* checks if there are more people in the same floor who want
		 * to go to the same direction
		 */
		riders.add(request);
		targetFloor = request.getFloorTo();
		if (earlyDirection)	{
			/* check if the currentTime < the time the first person in the queue pressed the button */
			while (!this.servingQueueUp.get(earlyFloor-1).isEmpty() && 
						this.servingQueueUp.get(earlyFloor-1).peek().getTimePressedButton().getTime() < 
																			currentTimeInMiliseconds) 	{
				request = this.servingQueueUp.get(earlyFloor-1).remove();
				// TODO finish this
				
			}
				
		}
		
		currentTimeInMiliseconds += this.doorDelta*1000; // delta to close the door
		
		/* checks if there are people between the current floor and the earlyFloor
		 * that are going to the same direction as the elevator
		 */

		
		
//		
//		PassengerRequest request = this.servingQueue.remove();
//		
//		long currentTime = Math.max(this.currentTime.getTime(),
//				request.getTimePressedButton().getTime()); // whichever happened latest
//		
//		long timeInMiliseconds = currentTime + 
//				this.doorDelta*1000 +  // delta to open the door
//				1000*this.timeMoveOneFloor* (Math.abs(currentFloor -request.getFloorFrom()))+
//				// time to get to the passenger's floor
//				1000*this.timeMoveOneFloor* (Math.abs(request.getFloorFrom() -request.getFloorTo()))+
//				// time to move to the destination
//				this.doorDelta*1000; // delta to close the door
//
//		PassengerReleased requestReleased = new PassengerReleased(request, 
//				new Time(timeInMiliseconds));
//		
//
//		
//		released.add(requestReleased);
//		if (verbose) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(currentFloor+" / " + request.getFloorFrom() + " / " +request.getFloorTo()+" | ");
//			sb.append(request.getTimePressedButton() + " / " +
//					new Time(timeInMiliseconds));
//			
//			System.out.println(new String(sb));
//		}
//		this.currentTime.setTime(timeInMiliseconds);
//		currentFloor = request.getFloorTo();
		return released;
	}

	public boolean continueOperate() {
		// TODO maybe this doesnt work! Needs to check if all queues are empty
		if(this.servingQueueUp.isEmpty() && servingQueueDown.isEmpty()) {
			return false;
		} else{
			return true;
		}
	}
	
	public ArrayList<PassengerReleased> operate() {
		ArrayList<PassengerReleased> released = new ArrayList<PassengerReleased>();
		if (verbose) {
			System.out.println("Floor at / floor from / floor to | Requested / arrived");
		}
		
		ArrayList<PassengerReleased> moved = this.move();

//		
//		while (this.continueOperate()) {
//			ArrayList<PassengerReleased> moved = this.move();
//			released.addAll(moved);
//		}
		return released;
	}

	
	public static void main(String[] args) {
		Random rnd= new Random(0);
		int maxWeight = 250;                   // max is 250 lbs
		int capacity = maxWeight*4;
		Time startingTime = new Time(8,30,0);   // 8 am
		int floors = 9;
		boolean verbose = true;
		int secondsPerFloor = 10;
		int timeOpenDoors = 15; // 15 seconds to open the door
		long currentTime = startingTime.getTime();
		int maxSeconds = 200;
		int numberOfPassangers = 40;  		   // number of passengers to generate
		ArrayList<Queue<PassengerRequest>> elevatorQueueUp = new ArrayList<Queue<PassengerRequest>>();
		ArrayList<Queue<PassengerRequest>> elevatorQueueDown = new ArrayList<Queue<PassengerRequest>>();
		int i;

		/* For each floor there will be two queues: one for people who want to go up and other
		 * for people who want to go down. Here it is the initialization of these queues
		 */
		for (i = 0; i < floors; i++)	{
			elevatorQueueUp.add(new LinkedList<PassengerRequest>());
			elevatorQueueDown.add(new LinkedList<PassengerRequest>());
		}
		
		for (i = 0; i < numberOfPassangers; i++) {
			int floor_from =rnd.nextInt(floors) + 1; // to generate numbers from [1, floors]
			int floor_to = floor_from;
			
			/* guarantees that floor_to != floor_from */
			while (floor_to == floor_from)	{		
				floor_to = rnd.nextInt(floors) + 1; 
			}
			
			int weight = rnd.nextInt(maxWeight);
			
			int seconds = rnd.nextInt(maxSeconds);
			currentTime+=seconds * 1000;
			Time time = new Time(currentTime);
			PassengerRequest request = new PassengerRequest(time, floor_from, 
					floor_to, weight);
			if (floor_from < floor_to)	{
				elevatorQueueUp.get(floor_from-1).add(request);
			}	else	{
				elevatorQueueDown.get(floor_from-1).add(request);
			}
		}
		
		/* PRINT THE QUEUES */
		for (i = 0; i < floors; i++){
			System.out.println("Floor " + (i + 1));
			System.out.println("Up");
			for (PassengerRequest ps : elevatorQueueUp.get(i)){
				System.out.println(ps.toString());
			}
			System.out.println("Down");
			for (PassengerRequest ps : elevatorQueueDown.get(i))	{
				System.out.println(ps.toString());
			}
			System.out.println("\n");
		}
		
		
		Elevator elevator = new MultiplePersonElevator(capacity,
				secondsPerFloor,floors, timeOpenDoors, verbose);
		
		elevator.initialize(elevatorQueueUp, elevatorQueueDown);
		
		ArrayList<PassengerReleased> output = elevator.operate();
		
		long cost = 0;
		
		for (i = 0; i < output.size(); i++) {
			
			PassengerReleased passenger = output.get(i);
			Time timeRequested = passenger.getTimeArrived();
			Time timeLeft = passenger.getPassengerRequest().getTimePressedButton();

			cost+=Math.abs(timeLeft.getTime() - timeRequested.getTime());
		}
		
		System.out.println("Total cost (in seconds): " + cost/ 1000);
	}

	
}
