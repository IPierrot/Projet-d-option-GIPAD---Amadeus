package context.userConstraints.cve;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import solving.ComplexTripModel;
import utils.DateOperations;

import static utils.DateOperations.*;
import model.Airport;
import model.Flight;
import context.Context;
import context.userConstraints.UserConstraint;
import dao.DAO;

/**
 * Représente une contrainte liée à une étape
 * 
 * @author Dim
 * 
 */
public class CVE extends UserConstraint {
    
    /**
     * La durée minimale en heures par défaut
     */
    private static final int DMIN_DEFAULT = 3;
    
    /**
     * La durée maximale en heures par défaut
     */
    private static final int DMAX_DEFAULT = 72;
    
    
    /**
     * nom de la contrainte
     */
    private String nomCVE;

    
    /**
     * L'aeroport etape.
     */
    private Airport stage;

    /**
     * True si le vol est obligatoire.
     */
    private boolean mandatory;

    /**
     * La date d'arrivée au plus tôt et celle de départ au plus tard.
     */
    private Date arr, dep;
    
    /**
     * La durée minimale et maximal du séjour (en heures).
     */
    private int durMin, durMax;
    
    /**
     * L'intervalle d'heure pendant lequel on doit être à l'étape.
     */
    private int h1, h2;
    
    /**
     * Le nombre de fois où on doit être dans cet intervalle.
     */
    private int nbTimes;
    
    /**
     * Constructeur avec champs.
     * @param nNomCVE nom de la contrainte
     * @param airport L'aeroport étape.
     * @param mandat True si l'étape est obligatoire.
     * @param passageInterval L'intervalle max du séjour,
     * dates sous la forme : YYYY/MM/dd-HH:mm.
     * @param dur La durée min et max de séjour.
     * @param hours La borne inf de l'intervalle d'heures de passage.
     * @param nbtimes Le nombre de fois où on doit passer dans l'intervalle.
     */
    public CVE(final String nNomCVE, final String airport, final boolean mandat,
            final String[] passageInterval, final int[] dur,
            final String[] hours, final int nbtimes){
        this.nomCVE=nNomCVE;
        this.stage = Airport.valueOf(airport);
        this.mandatory = mandat;
        try {
            this.arr = getDateFromPattern("yyyy/MM/dd-HH:mm",
                    passageInterval[0]);
            this.dep = getDateFromPattern("yyyy/MM/dd-HH:mm",
                    passageInterval[1]);
        } catch (ParseException e) {
            System.out.println("Erreur dans la lecture des dates du"
                    + " fichier de requête (CVE)");
            e.printStackTrace();
        }
        if (dur[0] <= 0 || dur[1] <= 0){
            this.durMin = DMIN_DEFAULT;
            this.durMax = DMAX_DEFAULT;
        } else {
            this.durMin = dur[0];
            this.durMax = dur[1];
        }
        try {
            this.h1 = (int) (DateOperations.
                    getDateFromPattern("HH:mm", hours[0]).getTime());
            this.h2 = (int) (DateOperations.
                    getDateFromPattern("HH:mm", hours[1]).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.nbTimes = nbtimes;
    }

    /**
     * 
     * @return nom de la contrainte
     */
    public String getNom(){return this.nomCVE; }
    
    @Override
    public void apply(final Context context) {
        context.getComplexTripModel().addStage(
                stage, arr, dep,
                durMin, durMax,
                new int[] {h1, h2}, nbTimes, mandatory);
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
        ComplexTripModel cxtm = context.getComplexTripModel();
        DAO dao = context.getDao();

        // Ajout des vols
        for (int i = 0; i < cxtm.getStages().size(); i++) {
            List<Airport> stages = new ArrayList<Airport>();
            stages.add(cxtm.getStages().get(i));
            for (Flight f : dao.getFlightsFromAirportToList(stage,
                    stages, arr, dep)) {
                if(!this.remove(f) 
                        && !cxtm.getPossibleFlights().contains(f)
                        && !f.getDeparture().before(cxtm.getEarliestDeparture())
                        && !f.getArrival().after(cxtm.getLatestArrival())
                        && f.getArrival().after(
                                cxtm.getStagesIntervals().get(i)[0])
                        && f.getArrival().before(
                                cxtm.getStagesIntervals().get(i)[1])){
                    cxtm.addPossibleFlight(f);
                }    
            }

            for (Flight f : dao.getFlightsFromListToAirport(
                    stages, stage, arr, dep)) {
                if(!this.remove(f) 
                        && !cxtm.getPossibleFlights().contains(f)
                        && !f.getDeparture().before(cxtm.getEarliestDeparture())
                        && !f.getArrival().after(cxtm.getLatestArrival())
                        && f.getDeparture().after(
                                cxtm.getStagesIntervals().get(i)[0])
                        && f.getDeparture().before(
                                cxtm.getStagesIntervals().get(i)[1])){
                    cxtm.addPossibleFlight(f);
                }    
            }
        }
    }
}
