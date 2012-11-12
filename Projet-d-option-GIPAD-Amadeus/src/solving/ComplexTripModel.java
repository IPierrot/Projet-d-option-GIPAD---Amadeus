package solving;

import java.util.Date;
import java.util.List;

import model.Airport;
import model.Flight;


import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

/**
 * 
 * @author Dimitri Justeau
 *
 */
public interface ComplexTripModel {
	
	/**
	 * @return L'aeroport de d�part du voyage.
	 */
	Airport getStart();
	
	/**
	 * D�finit l'aeroport de d�part du voyage.
	 * @param start l'aeroport de d�part.
	 */
	void setStart(Airport start);
	
	/**
	 * @return L'aeroport final du voyage.
	 */
	Airport getEnd();
	
	/**
	 * D�finit l'aeroport final du voyage.
	 * @param end l'aeroport final.
	 */
	void setEnd(Airport end);
	
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
	 * D�finit le d�part au plus t�t du voyage.
	 * @param d Le d�part au plus t�t du voyage.
	 */
	void setEarliestDeparture(Date d);
	
	/**
     * D�finit le d�part au plus tard du voyage.
     * @param d Le d�part au plus tard du voyage.
     */
	void setLatestDeparture(Date d);
	
	/**
	 * @return La date de fin du voyage au plus t�t.
	 */
	Date getEarliestArrival();
	
	/**
	 * @return La date de fin du voyage au plus tard.
	 */
	Date getLatestArrival();
	
	/**
     * D�finit la date de fin du voyage au plus t�t.
     * @param d La date de fin au plus t�t du voyage.
     */
    void setEarliestArrival(Date d);
	
	/**
	 * D�finit la date de fin du voyage au plus tard.
	 * @param d La date de fin au plus tard du voyage.
	 */
	void setLatestArrival(Date d);
	
	/**
     * @return La liste des intervales de dates mapp�es des �tapes.
     */
    List<Date[]> getStagesIntervals();
	
	
	// ELEMENTS CHOCO
	
	// METHODES RELATIVES A L'AEROPORT DE DEPART
	
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
	
	// METHODES RELATIVES AU ETAPES
	
	/**
	 * @return Les task variables du mod�le, une task variable
	 * correspond � une �tape du voyage.
	 */
	List<TaskVariable> getStagesTaskVariables();
	
	/**
	 * Ajoute une taskVariable d'�tape.
	 * @param stageVar la taskVariable � ajouter.
	 */
	void addStageTaskVariable(TaskVariable stageVar);
	
	/**
	 * @return Les variables correspondant au identifiants des �tapes
	 * du voyage.
	 */
	List<IntegerVariable> getStagesVariables();
	
	/**
	 * Ajoute une variable d'identifiant d'�tape.
	 * @param stageVar la variable.
	 */
	void addStageVariable(IntegerVariable stageVar);
	
	// METHODES RELATIVES A L'ARRIVEE
	
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
	
	// METHODES RELATIVES AUX VOLS
	
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
	 * @return Les variables correspondant aux index des vols du voyage.
	 */
	List<IntegerVariable> getIndexVariables();
	
	/**
	 * Ajoute une variable d'index de vol.
	 * @param indexVar la variable.
	 */
	void addIndexVariable(IntegerVariable indexVar);
	
	/**
	 * @return true si le mod�le est valide (ie toutes les informations
	 * n�cessaire sur le d�part et l'arriv�ee sont remplies, ...).
	 */
	boolean isValid();
	
	/**
	 * Initialise les variables.
	 * @return true en cas de r�ussite, false sinon.
	 */
	boolean initialize();
	
	/**
	 * @param d la date � mapper.
	 * @return mappe la date d � l'�chelle de temps du mod�le.
	 */
	int mapTime(Date d);
	
	/**
	 * @param t La date mapp�e � d�mapper.
	 * @return La date correspondant � t.
	 */
	Date unmapTime(int t);

}
