package dao;

import model.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * DAOImpl est une classe implementant DAO.
 */
public class DAOImpl implements DAO {
	
        
	//////////////////////////////////
	///////// FILE GENERATION ////////
	//////////////////////////////////
	
	/**
	 * Genere une liste de vols pour un jour donne, en partance 
	 * d'une ville donnee
	 * @param yyMMdd le jour choisi
	 * @param departure la ville de depart
	 * @return la liste des vols (objet Flight)
	 */
	public static List<Flight> getAllFlights(final String yyMMdd, 
	        final Airport departure){
		List<Flight> ret = new ArrayList<Flight>();
		
		try{
			FileInputStream fstream = 
			        new FileInputStream(departure.toString()+".csv");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String[] line;
			
			while((line = br.readLine().split(";")) != null){
				Airport dest = Airport.valueOf(line[DEP_DESTINATION]);
				Date dep = generateDate(yyMMdd, line[DEP_TIME], line[DEP_GMT]);
				Date arr = generateDate(yyMMdd, line[ARR_TIME], line[ARR_GMT], 
				        line[ARR_OFFSET]);
				ret.add(new Flight(departure, dest, dep, arr, line[ID]));
			}
			br.close();
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		
		return ret;
	}
	


	@Override
	public List<Flight> getFlightsFromAirportToList(final Airport origin,
			final List<Airport> destinations, final Date d1, final Date d2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Flight> getFlightsFromListToAirport(final List<Airport> origins,
			final Airport destination, final Date d1, final Date d2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Flight> getFlightsFromListToList(final List<Airport> origins,
			final List<Airport> destinations, final Date d1, final Date d2) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	//////////////////////////////////
	////////// DATE METHODS //////////
	//////////////////////////////////
	
	/**
	 * Genere un objet Date en fonction des parametres lus sur le fichier
	 * @param yyMMdd le jour selectionne
	 * @param time l'heure a convertir
	 * @param gmt le fuseau horaire GMT
	 * @param offset le nombre de jours de decalage
	 * @return la date correspondante (Java.util.Date)
	 */
	public static Date generateDate(final String yyMMdd, final String time, 
	        final String gmt, final String offset){
		int dd = Integer.parseInt(yyMMdd.substring(4, 6))
		        + Integer.parseInt(offset);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmZZZZZ");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		try {
			return sdf.parse("20"+yyMMdd.substring(0, 2)+yyMMdd.substring(2, 4)
					+dd+time.substring(0, 2) + time.substring(2, 4) + gmt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Genere un objet Date en fonction des parametres lus sur le fichier
	 * @param yyMMdd le jour selectionne
	 * @param time l'heure a convertir
	 * @param gmt le fuseau horaire GMT
	 * @return la date correspondante (Java.util.Date)
	 */
	public static Date generateDate(final String yyMMdd, 
            final String time, final String gmt){
        return generateDate(yyMMdd, time, gmt, "0");
    }
}
