package context.userConstraints.cg;

import model.Flight;
import context.Context;

/**
 * Repr�sente la contrainte de la dur�e totale du s�jour. 
 * @author Papi
 *
 */
public class CG00 extends CG {

    /**
     * Valeur minimale par d�faut
     */
    public static final int DEFAULT_MIN = 0;
    
    /**
     * Valeur maximale par d�faut
     */
    public static final int DEFAULT_MAX = 240;
    
    /**
     * valeurs extr�mes de l'intervalle de dur�e total
     */
    private int hmin, hmax;
    
    /**
     * Constructeur
     * @param min valeur minimale
     * @param max valeur maximale
     */
    public CG00(final int min, final int max){
        if(min<max && min>0){
            hmin = min;
            hmax = max;
        } else {
            hmin = DEFAULT_MIN;
            hmax = DEFAULT_MAX;
            System.out.println("Format de la contrainte CG00 incorrect - "
            		+"intervalle pass� � ["+DEFAULT_MIN+", "+DEFAULT_MAX+"]");
        }
        
    }
    
    @Override
    public void apply(final Context context) {
        context.getComplexTripModel().setTotalDuration(hmin, hmax);
    }

    @Override
    public boolean remove(final Flight flight) {
        return false;
    }

    @Override
    public void loadFlights(final Context context) {     
    }

}
