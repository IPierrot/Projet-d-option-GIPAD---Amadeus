package dao;

import java.util.Date;
import java.util.List;

import model.Airport;
import model.Flight;

public interface DAO {

	/**
	 * @deprecated
	 * @return Tous les vols partant le jour dd du mois MM de l'anée yy
	 * @param yyMMdd La date sous forme de chaine de caractères
	 */
	List<Flight> getAllFlights(String yyMMdd);

	/**
	 * @param origin La ville d'orgine.
	 * @param destinations Les destinations possibles.
	 * @param d1 La date de départ au plus tôt.
	 * @param d2 La date d'arrivée au plus tard.
	 * @return Tous les vols allant de origin à tous les vols contenus dans
	 * destinations, partant au plus tôt à la date d1 et au plus 
	 * tard à la date d2.
	 */
	List<Flight> getFlightsFromAirportToList(Airport origin,
			List<Airport> destinations, Date d1, Date d2);
	
	/**
	 * @param origins Les villes d'origine possibles.
	 * @param destination La destination.
	 * @param d1 La date de départ au plus tôt.
	 * @param d2 La date d'arrivée au plus tard.
	 * @return Tous les vols allant de toutes les villes contenus dans origins
	 * à tous destination, arrivant au plus tôt à la date d1 et au plus
	 * tard à la date d2.
	 */
	List<Flight> getFlightsFromListToAirport(List<Airport> origins,
			Airport destination, Date d1, Date d2);
	
	/**
	 * @param origins Les villes d'origine possibles.
	 * @param destinations Les destination possible.
	 * @param d1 La date de départ au plus tôt.
	 * @param d2 La date d'arrivée au plus tard.
	 * @return Tous les vols allant de toutes les villes contenus dans origins
	 * à toutes celles contenue dans destination, partant au plus tôt à la
	 * date d1 et arrivant au plus tard à la date d2.
	 */
	List<Flight> getFlightsFromListToList(List<Airport> origins,
			List<Airport> destinations, Date d1, Date d2);
}
