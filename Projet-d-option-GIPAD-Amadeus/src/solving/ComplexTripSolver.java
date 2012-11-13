package solving;

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
     * Lit le mod�le et ajoute les contraintes.
     * @param ctxModel Le mod�le � lire.
     */
    void read(ComplexTripModel ctxModel);
}
