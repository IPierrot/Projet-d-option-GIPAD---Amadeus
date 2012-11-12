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
     * Index of the destination in departure files
     */
    int DEP_DESTINATION = 0;
    
    /**
     * Index of the destination in arrival files
     */
    int ARR_DESTINATION = 0;
    
    /**
     * Index of the departure time in files
     */
    int DEP_TIME = 1;
    
    /**
     * Index of the departure GMT in files
     */
    int DEP_GMT = 2;
    
    /**
     * Index of the arrival time in files
     */
    int ARR_TIME = 3;
    
    /**
     * Index of the arrival GMT in files
     */
    int ARR_GMT = 4;
    
    /**
     * Index of the arrival day offset in files
     */
    int ARR_OFFSET = 5;
    
    /**
     * Index of the flight ID in files
     */
    int ID = 6;

    
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
