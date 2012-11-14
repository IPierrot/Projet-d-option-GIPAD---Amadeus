package context.userConstraints.cvo;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import static utils.DateOperations.*;

import model.Airport;
import model.Flight;
import context.Context;
import context.userConstraints.UserConstraint;

/**
 * Représente une contrainte sur la ville d'origine du voyage
 * @author Dim
 *
 */
public class CVO extends UserConstraint {
	
	/**
	 * La ville d'origine.
	 */
	private Airport origin;
	
	/**
	 * L'intervalle de dates pour le départ.
	 */
	private Date dep1, dep2;
	
	/**
	 * La tranche horaire pendant laquelle peut avoir lieu le départ.
	 */
	private String h1, h2;
	
	/**
	 * Constructeur avec champs.
	 * @param airport L'aeroport d'origine.
	 * @param depInterval La date min locale du départ,
	 * sous la forme : YYYY/MM/dd-HH:mm.
	 * @param hourInterval L'heure min du départ (HH:mm).
	 */
	public CVO(final String airport, final String[] depInterval,
			final String[] hourInterval){
		this.origin = Airport.valueOf(airport);
		String pattern = "yyyy/MM/d-HH:mm";
		try {
			TimeZone tz = this.origin.getTimeZone();
			this.dep1 = getDateFromPattern(pattern, depInterval[0], tz);
			this.dep2 = getDateFromPattern(pattern, depInterval[1], tz);
		} catch (ParseException e) {
			System.out.println("Erreur dans la lecture des dates du"
					+ " fichier de requête (CVO)");
			e.printStackTrace();
		}		
        
		this.h1 = hourInterval[0];
		this.h2 = hourInterval[1];
	}

	@Override
	public void apply(final Context context) {
		context.getComplexTripModel().setStart(origin, dep1, dep2);
	}

	@Override
	public boolean remove(final Flight flight) {
		boolean b = false;
		try {
			b = (flight.getOrigin() == origin
			       && isBetweenHours(flight.getDeparture(), "HH:mm", h1, h2))
			    || (flight.getDeparture().before(dep1));
		} catch (ParseException e) {
			System.out.println("Erreur de lecture du format de l'heure dans le"
					+ " fichier de requêtes (CVO)");
			e.printStackTrace();
		}
		return b;
	}

}
