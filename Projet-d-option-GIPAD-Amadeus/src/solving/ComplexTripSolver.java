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
     * Lit le modèle et ajoute les contraintes.
     * @param ctxModel Le modèle à lire.
     */
    void read(ComplexTripModel ctxModel);
}
