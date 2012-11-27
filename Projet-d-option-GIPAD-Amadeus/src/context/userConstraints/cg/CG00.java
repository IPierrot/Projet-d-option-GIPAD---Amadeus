package context.userConstraints.cg;

import model.Flight;
import context.Context;

/**
 * Représente la contrainte de la durée totale du séjour. 
 * @author Papi
 *
 */
public class CG00 extends CG {

    /**
     * Valeur minimale par défaut
     */
    public static final int DEFAULT_MIN = 0;
    
    /**
     * Valeur maximale par défaut
     */
    public static final int DEFAULT_MAX = 240;
    
    /**
     * valeurs extrêmes de l'intervalle de durée total
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
            		+"intervalle passé à ["+DEFAULT_MIN+", "+DEFAULT_MAX+"]");
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
