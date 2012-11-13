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
     * @return Le modèle Choco.
     */
    CPModel getCPModel();
    
    /**
     * Lit le modèle pour pouvoir résoudre le problème.
     * @param ctxModel Le modèle à lire.
     */
    void read(ComplexTripModel ctxModel);
}
