package solving.constraints;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Contrainte assurant (� partir de la date d'arriv�e dans l'�tape et la dur�e
 * de celle-ci qu'on recouvre bien un intervalle horaire particulier, et un 
 * certain nombre de fois).
 * @author Dimitri Justeau
 *
 */
public class MustBeBetween extends AbstractBinIntSConstraint{

    /**
     * La granularit� de l'�chelle de temps, ici 5 minutes soit 300 000 ms.
     */
    static final int GRANULARITE = 300000;

    /**
     * Le nombre de miliseconds dans une heure.
     */
    static final int NB_MS_IN_ONE_HOUR = 3600000;
    
    static final int DUR_DAY = (NB_MS_IN_ONE_HOUR*24)/GRANULARITE;
        
    private IntDomainVar start, duration;
    
    /**
     * L'intervalle d'heures qu'on veut recouvrir.
     */
    private int[] hours;
    
    /**
     * Le nombre de fois o� l'intervalle doit �tre recouvert.
     */
    private int nbTimes;
    
    /**
     * L'heure de r�f�rence (L'heure du d�part du voyage au plus t�t).
     */
    private int ref;
    
    /**
     * Constructeur avec param�tres.
     * @param st La date d'arriv�e dans l'�tape.
     * @param dur La dur�e du s�jour dans l'�tape.
     * @param h L'intervalle d'heure qu'on veut recouvrir.
     * @param nb Le nombre de fois qu'on veut recouvrir l'intervalle.
     * @param r L'heure du d�part du voyage au plus t�t.
     */
    public MustBeBetween(final IntDomainVar st,
            final IntDomainVar dur, final int[] h, final int nb, final int r) {
        super(st, dur);
        this.ref = r;
        this.start = st;
        this.duration = dur;
        this.hours = h;
        this.nbTimes = nb;
    }

    @Override
    public void propagate() throws ContradictionException {
        if (start.getSup() != -1) {    
            int durMin;
            // Cas start instanci�
            if (start.isInstantiated()) {
                int t0 = start.getVal();
                int hr = t0 % DUR_DAY + ref;
                if (hr > hours[0]) {
                    durMin = hours[1] + DUR_DAY*nbTimes-hr;
                } else {
                    durMin = hours[1] + DUR_DAY*(nbTimes-1)-hr;
                }
            } else {
                // Cas o� start n'est pas instanci� : on essaye quand m�me de
                // supprimer des valeurs.
                durMin = hours[1] + DUR_DAY*(nbTimes-1)-hours[0];
            }
            int durSup = duration.getSup();
            // On s'assure que la contrainte est satisfaisable (avec la
            // borne sup).
            if (durSup < durMin) {
                fail();
            } else {
                int durInf = duration.getInf();
                // Si la contrainte est r�alisable on met � jour 
                //la borne inf (� moins que ca ne soit pas n�c�ssaire.
                if (durInf < durMin) {
                    durInf = durMin;
                }
            }
        }    
    }
}
