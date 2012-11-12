package context.userConstraints.cve;

import java.util.Date;

import model.Airport;
import model.Flight;
import context.Context;
import context.userConstraints.UserConstraint;

/**
 * Représente une contrainte liée à une étape
 * 
 * @author Dim
 * 
 */
public class CVE extends UserConstraint {

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
     * La durée minimale et maximal du séjour.
     */
    private int durMin, durMax;
    
    /**
     * L'intervalle d'heure pendant lequel on doit être à l'étape.
     */
    private String h1, h2;
    
    /**
     * Le nombre de fois où on doit être dans cet intervalle.
     */
    private int nbTimes;
    
    /**
     * Constructeur avec champs.
     * @param airport L'aeroport étape.
     * @param mandat True si l'étape est obligatoire.
     * @param passageInterval L'intervalle max du séjour,
     * dates sous la forme : YYYY/MM/dd-HH:mm.
     * @param dur La durée min et max de séjour.
     * @param hours La borne inf de l'intervalle d'heures de passage.
     * @param nbtimes Le nombre de fois où on doit passer dans l'intervalle.
     */
    public CVE(final String airport, final boolean mandat,
            final String[] passageInterval, final int[] dur,
            final String[] hours, final int nbtimes){
        
    }

    @Override
    public void apply(final Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean remove(final Flight flight) {
        // TODO Auto-generated method stub
        return false;
    }

}
