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
	 * @return L'aeroport de départ du voyage.
	 */
	Airport getStart();
	
	/**
	 * Définit l'aeroport de départ du voyage.
	 * @param start l'aeroport de départ.
	 */
	void setStart(Airport start);
	
	/**
	 * @return L'aeroport final du voyage.
	 */
	Airport getEnd();
	
	/**
	 * Définit l'aeroport final du voyage.
	 * @param end l'aeroport final.
	 */
	void setEnd(Airport end);
	
	/**
	 * @return La liste des étapes définie.
	 */
	List<Airport> getStages();
	
	/**
	 * Ajoute une étape au voyage.
	 * @param stage l'étape.
	 * @param earliestArrival L'arrivé dans l'étape au plus tôt.
	 * @param latestDeparture Le départ au plus tard de l'étape.
	 * @param minDuration Durée min du séjour.
	 * @param maxDuration Durée max du séjour.
	 */
	void addStage(Airport stage, Date earliestArrival, Date latestDeparture,
			int minDuration, int maxDuration);
	
	/**
	 * @return La date de début du voyage au plus tôt.
	 */
	Date getEarliestDeparture();
	
	/**
	 * @return La date de début du voyage au plus tard.
	 */
	Date getLatestDeparture();
	
	/**
	 * Définit le départ au plus tôt du voyage.
	 * @param d Le départ au plus tôt du voyage.
	 */
	void setEarliestDeparture(Date d);
	
	/**
     * Définit le départ au plus tard du voyage.
     * @param d Le départ au plus tard du voyage.
     */
	void setLatestDeparture(Date d);
	
	/**
	 * @return La date de fin du voyage au plus tôt.
	 */
	Date getEarliestArrival();
	
	/**
	 * @return La date de fin du voyage au plus tard.
	 */
	Date getLatestArrival();
	
	/**
     * Définit la date de fin du voyage au plus tôt.
     * @param d La date de fin au plus tôt du voyage.
     */
    void setEarliestArrival(Date d);
	
	/**
	 * Définit la date de fin du voyage au plus tard.
	 * @param d La date de fin au plus tard du voyage.
	 */
	void setLatestArrival(Date d);
	
	/**
     * @return La liste des intervales de dates mappées des étapes.
     */
    List<Date[]> getStagesIntervals();
	
	
	// ELEMENTS CHOCO
	
	// METHODES RELATIVES A L'AEROPORT DE DEPART
	
	/**
	 * @return La variable correspondant à l'id de l'aeroport
	 * de départ du voyage.
	 */
	IntegerVariable getStartVariable();
	
	/**
	 * @return La variable correspondant à la date de départ de 
	 * l'aéroport de départ du voyage.
	 */
	IntegerVariable getStartDeparture();
	
	// METHODES RELATIVES AU ETAPES
	
	/**
	 * @return Les task variables du modèle, une task variable
	 * correspond à une étape du voyage.
	 */
	List<TaskVariable> getStagesTaskVariables();
	
	/**
	 * Ajoute une taskVariable d'étape.
	 * @param stageVar la taskVariable à ajouter.
	 */
	void addStageTaskVariable(TaskVariable stageVar);
	
	/**
	 * @return Les variables correspondant au identifiants des étapes
	 * du voyage.
	 */
	List<IntegerVariable> getStagesVariables();
	
	/**
	 * Ajoute une variable d'identifiant d'étape.
	 * @param stageVar la variable.
	 */
	void addStageVariable(IntegerVariable stageVar);
	
	// METHODES RELATIVES A L'ARRIVEE
	
	/**
	 * @return La variable correspondant à l'index de l'aeroport final
	 * du voyage.
	 */
	IntegerVariable getEndVariable();
	
	/**
	 * @return La variable correspondant à la date d'arrivée dans l'aeroport
	 * final du voyage.
	 */
	IntegerVariable getEndArrival();
	
	// METHODES RELATIVES AUX VOLS
	
	/**
	 * @return La liste des vols susceptibles de répondre au problème.
	 */
	List<Flight> getPossibleFlights();
	
	/**
	 * Ajoute un vol susceptible d'être solution au problème.
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
	 * @return true si le modèle est valide (ie toutes les informations
	 * nécessaire sur le départ et l'arrivéee sont remplies, ...).
	 */
	boolean isValid();
	
	/**
	 * Initialise les variables.
	 * @return true en cas de réussite, false sinon.
	 */
	boolean initialize();
	
	/**
	 * @param d la date à mapper.
	 * @return mappe la date d à l'échelle de temps du modèle.
	 */
	int mapTime(Date d);
	
	/**
	 * @param t La date mappée à démapper.
	 * @return La date correspondant à t.
	 */
	Date unmapTime(int t);

}
