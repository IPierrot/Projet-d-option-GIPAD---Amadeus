package solving;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;

/**
 * Solver
 * @author Dimitri Justeau
 *
 */
public interface ComplexTripSolver {

    /**
     * @return Le solveur Choco.
     */
    CPSolver getCPSolver();
    
    /**
     * @return Le mod�le Choco.
     */
    CPModel getCPModel();
    
    /**
     * Lit le mod�le pour pouvoir r�soudre le probl�me.
     * @param ctxModel Le mod�le � lire.
     */
    void read(ComplexTripModel ctxModel);
}
