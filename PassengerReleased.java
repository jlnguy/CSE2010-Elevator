package project;

import java.sql.Time;

public class PassengerReleased {
	public PassengerRequest getPassengerRequest() {
		return passengerRequest;
	}
	public void setPassengerRequest(PassengerRequest passengerRequest) {
		this.passengerRequest = passengerRequest;
	}

	public Time getTimeArrived() {
		return timeArrived;
	}
	public void setTimeArrived(Time timeArrived) {
		this.timeArrived = timeArrived;
	}
	private PassengerRequest passengerRequest; 
	private Time timeArrived;  // time when the passenger was arrived 
	public PassengerReleased(PassengerRequest passengerRequest, Time timeArrived) {

		this.passengerRequest = passengerRequest;
		this.timeArrived = timeArrived;
	}
}
