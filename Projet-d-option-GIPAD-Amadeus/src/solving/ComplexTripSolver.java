package solving;

import model.Trip;

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
    
    /**
     * La méthode read doit avoir été appellée avant.
     * @return La liste des vols composant la première solution trouvée.
     */
    Trip getFirstTripFound();
    
    /**
     * Réinitialise le solveur.
     */
    void reset();
}
