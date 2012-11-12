package dao;

import model.*;
import utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAOImpl est une classe implementant DAO.
 */
public class DAOImpl implements DAO {
	
        
	//////////////////////////////////
	///////// FILE GENERATION ////////
	//////////////////////////////////
	

	@Override
	public List<Flight> getFlightsFromAirportToList(final Airport origin,
			final List<Airport> destinations, final Date d1, final Date d2) {
		return generateFlights(d1, d2, origin, true, destinations);
	}

	@Override
	public List<Flight> getFlightsFromListToAirport(final List<Airport> origins,
			final Airport destination, final Date d1, final Date d2) {
		return generateFlights(d1, d2, destination, false,  origins);
	}

	@Override
	/**
	 * Note: cette methode utilise la methode getFlightsFromAirportToList
	 * En boucle sur la liste d'aeroports de depart.
	 */
	public List<Flight> getFlightsFromListToList(final List<Airport> origins,
			final List<Airport> destinations, final Date d1, final Date d2) {
		List<Flight> ret = new ArrayList<Flight>();
		for(Airport a: origins){
		    ret.addAll(getFlightsFromAirportToList(a, destinations, d1, d2));
		}
		return ret;
	}
	
	/**
	 * Methode generique de creation de listes de vols
	 * Cette methode part d'un aeroport unitaire pour analyser une liste 
	 * d'aeroports
	 * @param d1 la date de depart au plus tot
	 * @param d2 la date d'arrivee au plus tard
	 * @param single l'aeroport unique considere
	 * @param isDep vaut true si single represente l'aeroport de depart, false
	 * si single represente celui d'arrivee
	 * @param list la liste d'aeroports a comparer
	 * @return la liste des vols generee
	 */
	public List<Flight> generateFlights(final Date d1, final Date d2,
            final Airport single, final boolean isDep, 
            final List<Airport> list){
	    
        List<Flight> ret = new ArrayList<Flight>();
        try{
            FileInputStream fstream = 
                    new FileInputStream(single.toString()+".csv");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String[] line;
            long msInOneDay = 1000*60*60*24;
            
            //Line loop
            while((line = br.readLine().split(SEPARATOR)) != null){
                if(list.contains(Airport.valueOf(line[DESTINATION]))){
                    
                    //Calcul dates extremes en fonction de l'horaire
                    Date currentDep = new Date((d1.getTime()/msInOneDay)
                            +DateOperations.generateHour(line[DEP_TIME], 
                            line[DEP_GMT]).getTime());
                    if(currentDep.before(d1)){
                        currentDep.setTime(currentDep.getTime()+msInOneDay);
                    }
                    
                    long msOffset = msInOneDay
                            *Integer.parseInt(line[ARR_OFFSET]);
                    Date currentArr = new Date((d1.getTime()/msInOneDay)
                            +DateOperations.generateHour(line[ARR_TIME], 
                            line[ARR_GMT]).getTime()+msOffset);
                    
                    //Ajout du vol tant qu'il est dans les limites temporelles
                    while(!currentArr.after(d2)){
                        if(isDep){
                            ret.add(new Flight(single, 
                                    Airport.valueOf(line[DESTINATION]), 
                                    currentDep, currentArr, line[ID]));
                        } else {
                            ret.add(new Flight(Airport.valueOf
                                    (line[DESTINATION]), single, currentDep,
                                    currentArr, line[ID]));
                        }
                    }
                }
            }
            br.close();
        } catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        
        return ret;
    }
	

    /**
     * Genere une liste de vols pour un jour donne, en partance 
     * d'une ville donnee
     * @param yyMMdd le jour choisi
     * @param departure la ville de depart
     * @return la liste des vols (objet Flight)
     */
    public static List<Flight> getAllFlightsOfDayFrom(final String yyMMdd, 
            final Airport departure){
        List<Flight> ret = new ArrayList<Flight>();
        
        try{
            FileInputStream fstream = 
                    new FileInputStream(departure.toString()+".csv");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String[] line;
            
            while((line = br.readLine().split(SEPARATOR)) != null){
                Airport dest = Airport.valueOf(line[DESTINATION]);
                Date dep = DateOperations.generateDate(yyMMdd, 
                        line[DEP_TIME], line[DEP_GMT]);
                Date arr = DateOperations.generateDate(yyMMdd, 
                        line[ARR_TIME], line[ARR_GMT], line[ARR_OFFSET]);
                ret.add(new Flight(departure, dest, dep, arr, line[ID]));
            }
            br.close();
        } catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        
        return ret;
    }
}
