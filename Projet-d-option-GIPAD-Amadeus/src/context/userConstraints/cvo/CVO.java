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
	 * @param dateMin La date min locale du départ,
	 * sous la forme : YYYY/MM/dd-HH:mm
	 * @param dateMax La date max locale du départ,
	 * sous la forme : YYYY/MM/dd-HH:mm
	 * @param hourMin L'heure min du départ (HH:mm)
	 * @param hourMax L'heure max du départ (HH:mm)
	 */
	public CVO(final String airport, final String dateMin, final String dateMax,
			final String hourMin, final String hourMax){
		this.origin = Airport.valueOf(airport);
		String pattern = "YYYY/MM/dd-HH:mm";
		try {
			TimeZone tz = this.origin.getTimeZone();
			this.dep1 = getDateFromPattern(pattern, dateMin, tz);
			this.dep2 = getDateFromPattern(pattern, dateMax, tz);
		} catch (ParseException e) {
			System.out.println("Erreur dans la lecture des dates du"
					+ " fichier de requête (CVO)");
			e.printStackTrace();
		}
		this.h1 = hourMin;
		this.h2 = hourMax;
	}

	@Override
	public void apply(final Context context) {
		context.getComplexTripModel().setStart(origin);
		context.getComplexTripModel().setEarliestDeparture(dep1);
		context.getComplexTripModel().setLatestArrival(dep2);
	}

	@Override
	public boolean remove(final Flight flight) {
		boolean b = false;
		try {
			b = isBetweenHours(flight.getDeparture(), "HH:mm", h1, h2);
		} catch (ParseException e) {
			System.out.println("Erreur de lecture du format de l'heure dans le"
					+ " fichier de requêtes (CVO)");
			e.printStackTrace();
		}
		return b;
	}

}
