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
     * @return L'aeroport de départ du voyage.
     */
    Airport getStartAirport();
    
    /**
     * Définit l'aeroport de départ du voyage.
     * @param start l'aeroport de départ.
     * @param earliest Le dépat au plus tôt.
     * @param latest le départ au plus tard.
     */
    void setStart(Airport start, Date earliest, Date latest);
    
    /**
     * @return L'aeroport final du voyage.
     */
    Airport getEndAirport();
    
    /**
     * Définit l'aeroport final du voyage.
     * @param end l'aeroport final.
     * @param earliest L'arrivée au plus tôt.
     * @param latest L'arrivée au plus tard.
     */
    void setEnd(Airport end, Date earliest, Date latest);
    
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
     * @param h intervalle horaire du séjour.
     * @param nbTimes le nombre de fois où on doit rester 
     * dans l'intervalle [h1, h2].
     */
    void addStage(Airport stage, Date earliestArrival, Date latestDeparture,
            int minDuration, int maxDuration, int[] h, int nbTimes, boolean mandatory);
    
    /**
     * @return La date de début du voyage au plus tôt.
     */
    Date getEarliestDeparture();
    
    /**
     * @return La date de début du voyage au plus tard.
     */
    Date getLatestDeparture();
    
    /**
     * @return La date de fin du voyage au plus tôt.
     */
    Date getEarliestArrival();
    
    /**
     * @return La date de fin du voyage au plus tard.
     */
    Date getLatestArrival();
    
    /**
     * @return La liste des intervales de dates des étapes.
     */
    List<Date[]> getStagesIntervals();
    
    /**
     * @return Les intervalles de durée (en heures) des étapes.
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
     * Définit la durée totale du voyage entre les intervalles spécifiés
     * @param hmin la durée minimale du voyage en heures
     * @param hmax la durée maximale du voyage en heures
     */
    void setTotalDuration(int hmin, int hmax);

    // METHODES RELATIVES AUX VOLS //
    
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
     * @param d La date à mapper.
     * @return La date mappée.
     */
    int mapTime(Date d);
    
    /**
     * @param d La durée à demapper
     * @return La durée en ms.
     */
    int unmapDuration(int d);
    
    /**
     * @param t La date à demapper.
     * @return La date demappée.
     */
    Date unmapTime(int t);
    
    /**
     * @return True si le modèle est valide
     */
    boolean isValid();
    
    /**
     * Impose la CG01 sur les étapes d'index ant et pos
     * @param ant la première étape
     * @param pos l'étape postérieure à ant
     */
    void setOrder(int ant, int pos);
    
    /**
     * Construit le modèle.
     * @return true en cas de réussite, false sinon.
     */
    boolean build();
    
    /**
     * Contraint le modèle.
     * @return true en cas de réussite, false sinon.
     */
    boolean constraint();
    
    /**
     * La méthode build doit avoir été appellée avant.
     * @return La liste des vols composant la première solution trouvée.
     */
    Trip getFirstTripFound();

}
