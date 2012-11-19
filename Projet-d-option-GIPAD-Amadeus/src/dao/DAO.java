package dao;

import java.util.Date;
import java.util.List;

import model.Airport;
import model.Flight;

/**
 * La DAO permet l'acces aux donnees sauvegardees sur les fichiers csv.
 * Elle genere ensuite les vols en tant qu'objets java directement
 * manipulables par le modele.
 */
public interface DAO {

    
	/**
	 * @param origin La ville d'orgine.
	 * @param destinations Les destinations possibles.
	 * @param d1 La date de d�part au plus t�t.
	 * @param d2 La date de d�part au plus tard.
	 * @return Tous les vols allant de origin � toutes les villes contenues dans
	 * destinations, partant au plus t�t � la date d1 et au plus 
	 * tard � la date d2.
	 */
	List<Flight> getFlightsFromAirportToList(Airport origin,
			List<Airport> destinations, Date d1, Date d2);
	
	/**
	 * @param origins Les villes d'origine possibles.
	 * @param destination La destination.
	 * @param d1 La date d'arriv�e au plus t�t.
	 * @param d2 La date d'arriv�e au plus tard.
	 * @return Tous les vols allant de toutes les villes contenues dans origins
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
	 * @return Tous les vols allant de toutes les villes contenues dans origins
	 * � toutes celles contenue dans destination, partant au plus t�t � la
	 * date d1 et arrivant au plus tard � la date d2.
	 */
	List<Flight> getFlightsFromListToList(List<Airport> origins,
			List<Airport> destinations, Date d1, Date d2);
}
