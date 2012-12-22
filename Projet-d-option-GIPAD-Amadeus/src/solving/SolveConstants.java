package solving;

/**
 * Constantes pour le package solving
 * @author Marc
 *
 */
public final class SolveConstants {

    /**
     * Constructeur priv� vide.
     */
    private SolveConstants() {
  
    }
    
    /**
     * L'option des Variables
     */
    static final String VARIABLES_OPTION = "cp:blist";
    
    /**
     * La granularit� de l'�chelle de temps, ici 5 minutes soit 300 000 ms.
     */
    static final int GRANULARITE = 300000;

    /**
     * Le nombre de miliseconds dans une heure.
     */
    static final int NB_MS_IN_ONE_HOUR = 3600000;
    
    /**
     * La dur�e d'un jour en grains.
     */
    static final int DUR_DAY = (NB_MS_IN_ONE_HOUR*24)/GRANULARITE;
    
    /**
     * Pas de maximisation, ni de minimisation.
     */
    public static final String NO_OPTION = "";
    
    /**
     * Option correspondant � la maximisation du temps total pass� dans les
     * �tapes.
     */
    public static final String MAXIMIZE_TRIP_DURATION = "obj:stages";
    
    /**
     * Option correspondant � la maximisation du temps total pass� dans les
     * �tapes.
     */
    public static final String MINIMIZE_FLIGHT_TIME = "obj:flight";
}
