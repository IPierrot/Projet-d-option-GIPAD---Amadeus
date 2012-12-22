package io.dao.csv;

import model.*;
import utils.*;

import io.dao.DAO;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * DAOImpl est une classe implementant DAO.
 */
public class DAOImplCSV implements DAO {
	
    
        
	//////////////////////////////////
	///////// FILE GENERATION ////////
	//////////////////////////////////
	

	@Override
	public List<Flight> getFlightsFromAirportToList(final Airport origin,
			final List<Airport> destinations, final Date d1, final Date d2) {
		
	    List<Flight> ret = new ArrayList<Flight>();
        try{
            String folder = DaoConstants.DEP_FOLDER;
            
            InputStream fstream = DAOImplCSV.class.getClassLoader().
                   getResourceAsStream(
                           "ressources/"+folder+"/"+origin.toString()+".csv");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String[] line;
            String s;
            
            //Line loop
            while((s = br.readLine()) != null){
                line = s.split(DaoConstants.SEPARATOR);
                if(destinations.contains(Airport.valueOf(line[DaoConstants.DESTINATION]))){
                    
                    //Calcul dates extremes en fonction de l'horaire
                    Date depHoraire = DateOperations.generateHour(
                            line[DaoConstants.DEP_TIME], line[DaoConstants.DEP_GMT]);
                    Date arrHoraire = DateOperations.generateHour(
                            line[DaoConstants.ARR_TIME], line[DaoConstants.ARR_GMT]);
                    
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
                        int dayOffset = Integer.parseInt(line[DaoConstants.ARR_OFFSET]);
                        currentArr.setTime(currentArr.getTime()
                                +dayOffset*DateOperations.MS_IN_ONE_DAY);
                        
                        Flight f = new Flight(origin, 
                                Airport.valueOf(line[DaoConstants.DESTINATION]), 
                                new Date(currentDep.getTime()), 
                                new Date(currentArr.getTime()), line[DaoConstants.ID]);
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
            String folder = DaoConstants.ARR_FOLDER;
            
            InputStream fstream = DAOImplCSV.class.getClassLoader().
                    getResourceAsStream(
                        "ressources/"+folder+"/"+destination.toString()+".csv");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String[] line;
            String s;
            
            //Line loop
            while((s = br.readLine()) != null){
                line = s.split(DaoConstants.SEPARATOR);
                if(origins.contains(Airport.valueOf(line[DaoConstants.DESTINATION]))){
                    
                    //Calcul dates extremes en fonction de l'horaire
                    Date depHoraire = DateOperations.generateHour(
                            line[DaoConstants.DEP_TIME], line[DaoConstants.DEP_GMT]);
                    Date arrHoraire = DateOperations.generateHour(
                            line[DaoConstants.ARR_TIME], line[DaoConstants.ARR_GMT]);
                    
                    Date currentArr = DateOperations.dateDep(
                      DateOperations.getDay(d1), arrHoraire);
                    int dayOffset = Integer.parseInt(line[DaoConstants.ARR_OFFSET]);
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
                                line[DaoConstants.DESTINATION]), destination, 
                                new Date(currentDep.getTime()),
                                new Date(currentArr.getTime()), line[DaoConstants.ID]);
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
	//TODO meilleure algo implementee avant conflit, a refaire
}
