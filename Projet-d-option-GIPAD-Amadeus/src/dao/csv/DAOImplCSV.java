package dao.csv;

import model.*;
import utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dao.DAO;

/**
 * DAOImpl est une classe implementant DAO.
 */
public class DAOImplCSV implements DAO {
	
    
    /**
     * Separator in csv file
     */
    static final String SEPARATOR = ";";
    
    /**
     * Index of the destination in departure or arrival files
     * (must be the same !)
     */
    static final int DESTINATION = 0;
    
    /**
     * Index of the departure time in files
     */
    static final int DEP_TIME = 1;
    
    /**
     * Index of the departure GMT in files
     */
    static final int DEP_GMT = 2;
    
    /**
     * Index of the arrival time in files
     */
    static final int ARR_TIME = 3;
    
    /**
     * Index of the arrival GMT in files
     */
    static final int ARR_GMT = 4;
    
    /**
     * Index of the arrival day offset in files
     */
    static final int ARR_OFFSET = 5;
    
    /**
     * Index of the flight ID in files
     */
    static final int ID = 6;
        
	//////////////////////////////////
	///////// FILE GENERATION ////////
	//////////////////////////////////
	

	@Override
	public List<Flight> getFlightsFromAirportToList(final Airport origin,
			final List<Airport> destinations, final Date d1, final Date d2) {
		
	    List<Flight> ret = new ArrayList<Flight>();
        try{
            String folder = GenerationCSV.DEP_FOLDER;
            
            FileInputStream fstream = 
                    new FileInputStream(folder+"/"+origin.toString()+".csv");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String[] line;
            String s;
            
            //Line loop
            while((s = br.readLine()) != null){
                line = s.split(SEPARATOR);
                if(destinations.contains(Airport.valueOf(line[DESTINATION]))){
                    
                    //Calcul dates extremes en fonction de l'horaire
                    Date depHoraire = DateOperations.generateHour(
                            line[DEP_TIME], line[DEP_GMT]);
                    Date arrHoraire = DateOperations.generateHour(
                            line[ARR_TIME], line[ARR_GMT]);
                    
                    Date currentDep = DateOperations.dateDep(
                      DateOperations.getDay(d1), depHoraire);
                    if(currentDep.before(d1)){
                        currentDep.setTime(currentDep.getTime()
                                +DateOperations.MS_IN_ONE_DAY);
                    }
                          
                    Date dEnd = DateOperations.dateDep(
                            DateOperations.getDay(d2), depHoraire);
                    
                    //Ajout du vol tant qu'il est dans les limites temporelles
                    while(!currentDep.after(dEnd)){
                    
                        Date currentArr = DateOperations.dateDep(
                                DateOperations.getDay(currentDep), arrHoraire);
                        int dayOffset = Integer.parseInt(line[ARR_OFFSET]);
                        currentArr.setTime(currentArr.getTime()
                                +dayOffset*DateOperations.MS_IN_ONE_DAY);
                        
                        Flight f = new Flight(origin, 
                                Airport.valueOf(line[DESTINATION]), 
                                currentDep, currentArr, line[ID]);
                        if(!ret.contains(f)){
                            ret.add(f); 
                        }
                        currentDep.setTime(currentDep.getTime()
                                +DateOperations.MS_IN_ONE_DAY);
                    }
                }
            }
            br.close();
        } catch(Exception e){
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
        
        return ret;
    }

	
	@Override
	public List<Flight> getFlightsFromListToAirport(final List<Airport> origins,
			final Airport destination, final Date d1, final Date d2) {
	    
	    List<Flight> ret = new ArrayList<Flight>();
        try{
            String folder = GenerationCSV.ARR_FOLDER;
            
            FileInputStream fstream = 
                 new FileInputStream(folder+"/"+destination.toString()+".csv");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String[] line;
            String s;
            
            //Line loop
            while((s = br.readLine()) != null){
                line = s.split(SEPARATOR);
                if(origins.contains(Airport.valueOf(line[DESTINATION]))){
                    
                    //Calcul dates extremes en fonction de l'horaire
                    Date depHoraire = DateOperations.generateHour(
                            line[DEP_TIME], line[DEP_GMT]);
                    Date arrHoraire = DateOperations.generateHour(
                            line[ARR_TIME], line[ARR_GMT]);
                    
                    Date currentArr = DateOperations.dateDep(
                      DateOperations.getDay(d1), arrHoraire);
                    int dayOffset = Integer.parseInt(line[ARR_OFFSET]);
                    currentArr.setTime(currentArr.getTime()
                            +dayOffset*DateOperations.MS_IN_ONE_DAY);
                    
                          
                    Date dEnd = DateOperations.dateDep(
                            DateOperations.getDay(d2), arrHoraire);
                    if(dEnd.after(d2)){
                        dEnd.setTime(dEnd.getTime()
                                -DateOperations.MS_IN_ONE_DAY);
                    }
                    
                    //Ajout du vol tant qu'il est dans les limites temporelles
                    while(!currentArr.after(dEnd)){
                    
                        Date currentDep = DateOperations.dateDep(
                                DateOperations.getDay(currentArr), depHoraire);
                        currentDep.setTime(currentDep.getTime()
                                -dayOffset*DateOperations.MS_IN_ONE_DAY);
                        
                        Flight f = new Flight(Airport.valueOf(
                                line[DESTINATION]), destination, 
                                currentDep, currentArr, line[ID]);
                        if(!ret.contains(f)){
                            ret.add(f); 
                        }
                        currentArr.setTime(currentArr.getTime()
                                +DateOperations.MS_IN_ONE_DAY);
                    }
                }
            }
            br.close();
        } catch(Exception e){
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
        
        return ret;
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
		for(Flight f: ret){
		    if(f.getArrival().after(d2)){
		        ret.remove(f);
		    }
		}
		return ret;
	}
	
}
