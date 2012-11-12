package dao;

import java.util.Date;
import java.util.List;

import model.Airport;
import model.Flight;

public interface DAO {

	/**
	 * @deprecated
	 * @return Tous les vols partant le jour dd du mois MM de l'an�e yy
	 * @param yyMMdd La date sous forme de chaine de caract�res
	 */
	List<Flight> getAllFlights(String yyMMdd);

	/**
	 * @param origin La ville d'orgine.
	 * @param destinations Les destinations possibles.
	 * @param d1 La date de d�part au plus t�t.
	 * @param d2 La date d'arriv�e au plus tard.
	 * @return Tous les vols allant de origin � tous les vols contenus dans
	 * destinations, partant au plus t�t � la date d1 et au plus 
	 * tard � la date d2.
	 */
	List<Flight> getFlightsFromAirportToList(Airport origin,
			List<Airport> destinations, Date d1, Date d2);
	
	/**
	 * @param origins Les villes d'origine possibles.
	 * @param destination La destination.
	 * @param d1 La date de d�part au plus t�t.
	 * @param d2 La date d'arriv�e au plus tard.
	 * @return Tous les vols allant de toutes les villes contenus dans origins
	 * � tous destination, arrivant au plus t�t � la date d1 et au plus
	 * tard � la date d2.
	 */
	List<Flight> getFlightsFromListToAirport(List<Airport> origins,
			Airport destination, Date d1, Date d2);
	
	/**
	 * @param origins Les villes d'origine possibles.
	 * @param destinations Les destination possible.
	 * @param d1 La date de d�part au plus t�t.
	 * @param d2 La date d'arriv�e au plus tard.
	 * @return Tous les vols allant de toutes les villes contenus dans origins
	 * � toutes celles contenue dans destination, partant au plus t�t � la
	 * date d1 et arrivant au plus tard � la date d2.
	 */
	List<Flight> getFlightsFromListToList(List<Airport> origins,
			List<Airport> destinations, Date d1, Date d2);
}
