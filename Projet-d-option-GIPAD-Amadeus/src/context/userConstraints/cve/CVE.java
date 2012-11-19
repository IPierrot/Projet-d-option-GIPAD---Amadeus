package context.userConstraints.cve;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import solving.ComplexTripModel;

import static utils.DateOperations.*;
import model.Airport;
import model.Flight;
import context.Context;
import context.userConstraints.UserConstraint;
import dao.DAO;

/**
 * Repr�sente une contrainte li�e � une �tape
 * 
 * @author Dim
 * 
 */
public class CVE extends UserConstraint {

    /**
     * La dur�e minimale en heures par d�faut
     */
    private static final int DMIN_DEFAULT = 3;
    
    /**
     * La dur�e maximale en heures par d�faut
     */
    private static final int DMAX_DEFAULT = 72;
    
    /**
     * L'aeroport etape.
     */
    private Airport stage;

    /**
     * True si le vol est obligatoire.
     */
    private boolean mandatory;

    /**
     * La date d'arriv�e au plus t�t et celle de d�part au plus tard.
     */
    private Date arr, dep;
    
    /**
     * La dur�e minimale et maximal du s�jour (en heures).
     */
    private int durMin, durMax;
    
    /**
     * L'intervalle d'heure pendant lequel on doit �tre � l'�tape.
     */
    private String h1, h2;
    
    /**
     * Le nombre de fois o� on doit �tre dans cet intervalle.
     */
    private int nbTimes;
    
    /**
     * Constructeur avec champs.
     * @param airport L'aeroport �tape.
     * @param mandat True si l'�tape est obligatoire.
     * @param passageInterval L'intervalle max du s�jour,
     * dates sous la forme : YYYY/MM/dd-HH:mm.
     * @param dur La dur�e min et max de s�jour.
     * @param hours La borne inf de l'intervalle d'heures de passage.
     * @param nbtimes Le nombre de fois o� on doit passer dans l'intervalle.
     */
    public CVE(final String airport, final boolean mandat,
            final String[] passageInterval, final int[] dur,
            final String[] hours, final int nbtimes){
        this.stage = Airport.valueOf(airport);
        this.mandatory = mandat;
        try {
            this.arr = getDateFromPattern("yyyy/MM/dd-HH:mm",
                    passageInterval[0]);
            this.dep = getDateFromPattern("yyyy/MM/dd-HH:mm",
                    passageInterval[1]);
        } catch (ParseException e) {
            System.out.println("Erreur dans la lecture des dates du"
                    + " fichier de requ�te (CVE)");
            e.printStackTrace();
        }
        this.durMin = dur[0] > 0 ? dur[0] : DMIN_DEFAULT;
        this.durMax = dur[1] > 0 ? dur[1] : DMAX_DEFAULT;
        this.h1 = hours[0];
        this.h2 = hours[1];
        this.nbTimes = nbtimes;
    }

    @Override
    public void apply(final Context context) {
        context.getComplexTripModel().addStage(
                stage, arr, dep, durMin, durMax);
    }

    @Override
    public boolean remove(final Flight flight) {
        boolean b = (flight.getDestination() == stage)
                  || (flight.getArrival().after(dep))
                  || (flight.getDeparture().before(arr)); 

        return b;
    }

    @Override
    public void loadFlights(final Context context) {
        List<Flight> possibleFlights = new ArrayList<Flight>();
        ComplexTripModel cxtm = context.getComplexTripModel();
        DAO dao = context.getDao();
        
        // R�cup�ration des �tapes.
        List<Airport> stages = cxtm.getStages();
        List<Airport> st = new ArrayList<Airport>();
        st.add(this.stage);
        
        // Ajout des vols
        possibleFlights.addAll(
                dao.getFlightsFromListToAirport(stages, stage, arr, dep));
        
        possibleFlights.addAll(
                dao.getFlightsFromAirportToList(stage, stages, arr, dep));

        // Filtrage et injection des vols dans le mod�le
        for(Flight f : possibleFlights){
            if(!this.remove(f) && !cxtm.getPossibleFlights().contains(f)
                   && !f.getDeparture().before(cxtm.getEarliestDeparture())
                   && !f.getArrival().after(cxtm.getLatestArrival())){
                cxtm.addPossibleFlight(f);
            }
        }
    }
}
