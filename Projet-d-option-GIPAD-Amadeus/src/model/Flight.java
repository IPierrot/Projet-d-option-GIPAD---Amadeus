package model;

import java.util.Date;

/**
 * @author Dim
 */
public class Flight {
	
	/**
	 * The origin's airport and the destination
	 */
	Airport origin, destination;
	
	/**
	 * The departure and arrival dates and times
	 */
	Date departure, arrival;
	
	/**
	 * The unique id of the flight
	 */
	String id;

	// CONSTRUCTORS
	
	public Flight(Airport origin, Airport destination, Date departure,
			Date arrival, String id) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.departure = departure;
		this.arrival = arrival;
		this.id = id;
	}
	
	public Flight(Flight f, Date departure, Date arrival){
		this.origin = f.getOrigin();
		this.destination = f.getDestination();
		this.departure = departure;
		this.arrival = arrival;
		this.id = f.getId();
	}
	
	// CONSTRUCTORS - END
	
	// GETTERS AND SETTERS
	
	public Airport getOrigin() {
		return origin;
	}

	public Airport getDestination() {
		return destination;
	}

	public Date getDeparture() {
		return departure;
	}

	public Date getArrival() {
		return arrival;
	}

	public String getId() {
		return id;
	}
	
	// GETTERS AND SETTERS - END
	
	public String toString(){
		return this.origin.name() +
				" " + this.departure +
				" -> " +
				this.destination.name() +
				" " + this.arrival +
				" " + this.id;
	}
}
