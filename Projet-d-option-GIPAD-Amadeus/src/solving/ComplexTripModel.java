package solving;

import java.util.Date;
import java.util.List;

import model.Airport;
import model.Flight;


import choco.cp.model.CPModel;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

/**
 * 
 * @author Dimitri Justeau
 *
 */
public interface ComplexTripModel {
	
    
    //--------------------//
    // ELEMENTS DU MODELE //
    //--------------------//
    
	/**
	 * @return L'aeroport de d�part du voyage.
	 */
	Airport getStartAirport();
	
	/**
	 * D�finit l'aeroport de d�part du voyage.
	 * @param start l'aeroport de d�part.
	 * @param earliest Le d�pat au plus t�t.
	 * @param latest le d�part au plus tard.
	 */
	void setStart(Airport start, Date earliest, Date latest);
	
	/**
	 * @return L'aeroport final du voyage.
	 */
	Airport getEndAirport();
	
	/**
	 * D�finit l'aeroport final du voyage.
	 * @param end l'aeroport final.
	 * @param earliest L'arriv�e au plus t�t.
	 * @param latest L'arriv�e au plus tard.
	 */
	void setEnd(Airport end, Date earliest, Date latest);
	
	/**
	 * @return La liste des �tapes d�finie.
	 */
	List<Airport> getStages();
	
	/**
	 * Ajoute une �tape au voyage.
	 * @param stage l'�tape.
	 * @param earliestArrival L'arriv� dans l'�tape au plus t�t.
	 * @param latestDeparture Le d�part au plus tard de l'�tape.
	 * @param minDuration Dur�e min du s�jour.
	 * @param maxDuration Dur�e max du s�jour.
	 */
	void addStage(Airport stage, Date earliestArrival, Date latestDeparture,
			int minDuration, int maxDuration);
	
	/**
	 * @return La date de d�but du voyage au plus t�t.
	 */
	Date getEarliestDeparture();
	
	/**
	 * @return La date de d�but du voyage au plus tard.
	 */
	Date getLatestDeparture();
	
	/**
	 * @return La date de fin du voyage au plus t�t.
	 */
	Date getEarliestArrival();
	
	/**
	 * @return La date de fin du voyage au plus tard.
	 */
	Date getLatestArrival();
	
	/**
     * @return La liste des intervales de dates des �tapes.
     */
    List<Date[]> getStagesIntervals();
    
    /**
     * @return Les intervalles de dur�e (en heures) des �tapes.
     */
    List<int[]> getStagesDurations();
	
	//----------------//
	// ELEMENTS CHOCO //
    //----------------//
    
    /**
     * @return Le CPModel li� au ComplexTripModel.
     */
    CPModel getCPModel();
	
	// METHODES RELATIVES A L'AEROPORT DE DEPART //
	
	/**
	 * @return La variable correspondant � l'id de l'aeroport
	 * de d�part du voyage.
	 */
	IntegerVariable getStartVariable();
	
	/**
	 * @return La variable correspondant � la date de d�part de 
	 * l'a�roport de d�part du voyage.
	 */
	IntegerVariable getStartDeparture();
	
	/**
	 * @return L'index du vol de d�part du voyage.
	 */
	IntegerVariable getStartIndex();
	
	
	// METHODES RELATIVES AU ETAPES //
	
	/**
	 * @return Les task variables du mod�le, une task variable
	 * correspond � une �tape du voyage.
	 */
	TaskVariable[] getStagesTaskVariables();
	
	/**
	 * @return Les variables correspondant au identifiants des �tapes
	 * du voyage.
	 */
	IntegerVariable[] getStagesVariables();
	
	/**
     * @return Les variables correspondant aux index des vols 
     * du voyage li�s aux �tapes.
     */
    IntegerVariable[][] getStagesIndexes();
    
	// METHODES RELATIVES A L'ARRIVEE //
    
	/**
	 * @return La variable correspondant � l'index de l'aeroport final
	 * du voyage.
	 */
	IntegerVariable getEndVariable();
	
	/**
	 * @return La variable correspondant � la date d'arriv�e dans l'aeroport
	 * final du voyage.
	 */
	IntegerVariable getEndArrival();
	
	   /**
     * @return L'index du vol d'arriv�e du voyage.
     */
    IntegerVariable getEndIndex();
	
	// METHODES RELATIVES AUX VOLS //
	
	/**
	 * @return La liste des vols susceptibles de r�pondre au probl�me.
	 */
	List<Flight> getPossibleFlights();
	
	/**
	 * Ajoute un vol susceptible d'�tre solution au probl�me.
	 * @param flight le vol.
	 */
	void addPossibleFlight(Flight flight);
	
	/**
	 * Construit le mod�le.
	 * @return true en cas de r�ussite, false sinon.
	 */
	boolean build();
	
	/**
	 * @param d La date � mapper.
	 * @return La date mapp�e.
	 */
	int mapTime(Date d);
	
	/**
	 * @param d La dur�e � demapper
	 * @return La dur�e en heures.
	 */
	double unmapDuration(int d);

}
