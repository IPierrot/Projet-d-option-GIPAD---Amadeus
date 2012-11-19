package context.userConstraints.cvf;

import static utils.DateOperations.getDateFromPattern;
import static utils.DateOperations.isBetweenHours;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import solving.ComplexTripModel;

import model.Airport;
import model.Flight;
import context.Context;
import context.userConstraints.UserConstraint;
import dao.DAO;

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
     * @param arrInterval Les dates min et max locale du départ,
     * sous la forme : YYYY/MM/dd-HH:mm
     * @param hourInterval Les heures min et max du départ (HH:mm)
     */
    public CVF(final String airport, final String[] arrInterval,
            final String[] hourInterval){
        this.end = Airport.valueOf(airport);
        String pattern = "yyyy/MM/dd-HH:mm";
        try {
            TimeZone tz = this.end.getTimeZone();
            this.arr1 = getDateFromPattern(pattern, arrInterval[0], tz);
            this.arr2 = getDateFromPattern(pattern, arrInterval[1], tz);
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
            b = (flight.getDestination() == end
                    && !isBetweenHours(flight.getArrival(), "HH:mm", h1, h2));
        } catch (ParseException e) {
            System.out.println("Erreur de lecture du format de l'heure dans le"
                    + " fichier de requêtes (CVF)");
            e.printStackTrace();
        }
        return b;
	}

    @Override
    public void loadFlights(final Context context) {
        List<Flight> possibleFlights = new ArrayList<Flight>();
        ComplexTripModel cxtm = context.getComplexTripModel();
        DAO dao = context.getDao();
        
        // Récupération des aeroports de départ, de fin et des étapes.
        List<Airport> stages = cxtm.getStages();
        
        // Récupération des dates entre lesquel on va récupérer des vols.
        Date d3 = cxtm.getEarliestArrival();
        Date d4 = cxtm.getLatestArrival();
        
        // Ajout des vols
        possibleFlights.addAll(dao.getFlightsFromListToAirport(
                stages, end, d3, d4));
                
        // Filtrage et injection des vols dans le modèle
        for(Flight f : possibleFlights){
            if(!this.remove(f) && !cxtm.getPossibleFlights().contains(f)
                    && !f.getDeparture().before(cxtm.getEarliestDeparture())){
                cxtm.addPossibleFlight(f);
            }
        }
    }

}
