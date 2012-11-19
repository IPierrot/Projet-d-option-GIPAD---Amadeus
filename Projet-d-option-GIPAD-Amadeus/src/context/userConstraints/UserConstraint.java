package context.userConstraints;

//import java.util.ArrayList;
//import java.util.List;

import java.util.ArrayList;
import java.util.List;

import model.Flight;
import context.Context;

/**
 * Classe abstraite représentant une contrainte utilisateur (CV01, ...)
 * @author Dim
 *
 */
public abstract class UserConstraint {

	/**
	 * Applique la contrainte dans le contexte.
	 * @param context le contexte.
	 */
	public abstract void apply(Context context);
	
	/**
	 * @param flight Le vol à tester.
	 * @return true si le vol est compatible avec la contrainte; false sinon.
	 */
	public abstract boolean remove(Flight flight);
	
	/**
	 * Charge les vols susceptibles de satisfaire la contrainte.
	 * @param context Le contexte.
	 */
	public abstract void loadFlights(Context context);
	
	/**
	 * Parcourt la liste de vols et retire ceux qui ne sont
	 * pas compatibles avec la user constraint.
	 * @param flights Les vols à vérifier.
	 */
	public void filter(final List<Flight> flights){
	    List<Flight> toRemove = new ArrayList<Flight>();
	    for(Flight f : flights){
			if(this.remove(f)){
				toRemove.add(f);
			}
		}
	    flights.removeAll(toRemove);

	}
}
