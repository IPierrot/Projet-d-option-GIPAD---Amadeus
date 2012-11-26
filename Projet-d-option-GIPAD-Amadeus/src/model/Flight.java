package model;

import java.util.Date;

/**
 * @author Dim
 */
public class Flight {
	
	/**
	 * The origin's airport and the destination
	 */
	private Airport origin, destination;
	
	/**
	 * The departure and arrival dates and times
	 */
	private Date departure, arrival;
	
	/**
	 * The unique id of the flight
	 */
	private String id;

	// CONSTRUCTORS

	/**
	 * Constructeur avec parametres
	 * @param originAirport L'aeroport d'origine
	 * @param destAirport L'aeroport de destination
	 * @param depDate La date time de depart
	 * @param arrDate La date time d'arrivee
	 * @param flightId l'identifiant unique du vol
	 */
	public Flight(
			final Airport originAirport, final Airport destAirport,
			final Date depDate, final Date arrDate,
			final String flightId) {
		super();
		this.origin = originAirport;
		this.destination = destAirport;
		this.departure = depDate;
		this.arrival = arrDate;
		this.id = flightId;
	}
	
	/**
	 * Constructeur par recopie partielle :
	 * Modifie uniquement la date de depart et la date d'arrivee
	 * @param f Le vol a recopier partiellement
	 * @param dep La nouvelle date de depart
	 * @param arr La nouvelle date d'arrivee
	 */
	public Flight(final Flight f, final Date dep, final Date arr){
		this.origin = f.getOrigin();
		this.destination = f.getDestination();
		this.departure = dep;
		this.arrival = arr;
		this.id = f.getId();
	}
	
	// CONSTRUCTORS - END
	
	// GETTERS AND SETTERS
	
	/**
	 * @return L'aeroport d'origine du vol
	 */
	public Airport getOrigin() {
		return origin;
	}

	/**
	 * @return La destination du vol
	 */
	public Airport getDestination() {
		return destination;
	}

	/**
	 * @return La date time de depart du vol
	 */
	public Date getDeparture() {
		return departure;
	}

	/**
	 * @return La date time d'arrivee du vol
	 */
	public Date getArrival() {
		return arrival;
	}

	/**
	 * @return L'identifiant unique du vol
	 */
	public String getId() {
		return id;
	}
	
	// GETTERS AND SETTERS - END
	
	/**
	 * Surcharge de la methode toString pour representer
	 * un vol sous forme de chaine de caracteres
	 * @return Une representation sous forme de chaine de 
	 * caracteres du vol
	 */
	public String toString(){
		return this.origin.name() 
				+ " " + this.departure 
				+ " -> " 
				+ this.destination.name() 
				+ " " + this.arrival 
				+ " " + this.id;
	}
}
