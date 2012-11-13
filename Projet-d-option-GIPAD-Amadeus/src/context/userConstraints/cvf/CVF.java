package context.userConstraints.cvf;

import static utils.DateOperations.getDateFromPattern;
import static utils.DateOperations.isBetweenHours;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import model.Airport;
import model.Flight;
import context.Context;
import context.userConstraints.UserConstraint;

/**
 * Représente une contrainte sur la ville finale du voyage
 * @author Dim
 *
 */
public class CVF extends UserConstraint {
    
    /**
     * La ville finale.
     */
    private Airport end;
    
    /**
     * L'intervalle de dates pour l'arrivée.
     */
    private Date arr1, arr2;
    
    /**
     * La tranche horaire pendant laquelle peut avoir lieu l'arrivée.
     */
    private String h1, h2;
    
    /**
     * Constructeur avec champs.
     * @param airport L'aeroport d'origine.
     * @param depInterval Les dates min et max locale du départ,
     * sous la forme : YYYY/MM/dd-HH:mm
     * @param hourInterval Les heures min et max du départ (HH:mm)
     */
    public CVF(final String airport, final String[] depInterval,
            final String[] hourInterval){
        this.end = Airport.valueOf(airport);
        String pattern = "YYYY/MM/dd-HH:mm";
        try {
            TimeZone tz = this.end.getTimeZone();
            this.arr1 = getDateFromPattern(pattern, depInterval[0], tz);
            this.arr2 = getDateFromPattern(pattern, depInterval[1], tz);
        } catch (ParseException e) {
            System.out.println("Erreur dans la lecture des dates du"
                    + " fichier de requête (CVF)");
            e.printStackTrace();
        }
        this.h1 = hourInterval[0];
        this.h2 = hourInterval[1];
    }
    
	@Override
	public void apply(final Context context) {
	    context.getComplexTripModel().setEnd(end, arr1, arr2);
	}

	@Override
	public boolean remove(final Flight flight) {
	    boolean b = false;
        try {
            b = isBetweenHours(flight.getArrival(), "HH:mm", h1, h2);
        } catch (ParseException e) {
            System.out.println("Erreur de lecture du format de l'heure dans le"
                    + " fichier de requêtes (CVF)");
            e.printStackTrace();
        }
        return b;
	}

}
