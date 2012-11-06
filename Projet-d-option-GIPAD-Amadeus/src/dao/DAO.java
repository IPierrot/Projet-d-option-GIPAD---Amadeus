package dao;

import java.util.List;

import model.Flight;

public interface DAO {

	/**
	 * @return Tous les vols partant le jour dd du mois MM de l'anée yy
	 */
	List<Flight> getAllFlights(String yyMMdd);
}
