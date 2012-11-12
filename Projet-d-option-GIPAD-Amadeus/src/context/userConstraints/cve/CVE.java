package context.userConstraints.cve;

import java.util.Date;

import model.Airport;
import model.Flight;
import context.Context;
import context.userConstraints.UserConstraint;

/**
 * Repr�sente une contrainte li�e � une �tape
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
     * La date d'arriv�e au plus t�t et celle de d�part au plus tard.
     */
    private Date arr, dep;
    
    /**
     * La dur�e minimale et maximal du s�jour.
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
