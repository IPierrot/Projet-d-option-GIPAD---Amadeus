package solving;

import java.util.Date;
import java.util.List;

import model.Airport;
import model.Flight;
import model.Trip;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

/**
 * 
 * @author Dim
 *
 */
public interface IComplexTripSolver {

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
     * @param h intervalle horaire du s�jour.
     * @param nbTimes le nombre de fois o� on doit rester 
     * dans l'intervalle [h1, h2].
     */
    void addStage(Airport stage, Date earliestArrival, Date latestDeparture,
            int minDuration, int maxDuration, int[] h, int nbTimes, boolean mandatory);
    
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
    
    /**
     * @return les intervalles des horaires de presence dans les etapes.
     */
    List<int[]> getStagesHours();
    
    /**
     * @return les nombres de fois ou les horaires de presence 
     * doivent etre verifies
     */
    List<Integer> getNbTimes();
    
    /**
     * D�finit la dur�e totale du voyage entre les intervalles sp�cifi�s
     * @param hmin la dur�e minimale du voyage en heures
     * @param hmax la dur�e maximale du voyage en heures
     */
    void setTotalDuration(int hmin, int hmax);

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
     * @param d La date � mapper.
     * @return La date mapp�e.
     */
    int mapTime(Date d);
    
    /**
     * @param d La dur�e � demapper
     * @return La dur�e en ms.
     */
    int unmapDuration(int d);
    
    /**
     * @param t La date � demapper.
     * @return La date demapp�e.
     */
    Date unmapTime(int t);
    
    /**
     * @return True si le mod�le est valide
     */
    boolean isValid();
    
    /**
     * Impose la CG01 sur les �tapes d'index ant et pos
     * @param ant la premi�re �tape
     * @param pos l'�tape post�rieure � ant
     */
    void setOrder(int ant, int pos);
    
    /**
     * Construit le mod�le.
     * @return true en cas de r�ussite, false sinon.
     */
    boolean build();
    
    /**
     * Contraint le mod�le.
     * @return true en cas de r�ussite, false sinon.
     */
    boolean constraint();
    
    /**
     * La m�thode build doit avoir �t� appell�e avant.
     * @return La liste des vols composant la premi�re solution trouv�e.
     */
    Trip getFirstTripFound();

}
