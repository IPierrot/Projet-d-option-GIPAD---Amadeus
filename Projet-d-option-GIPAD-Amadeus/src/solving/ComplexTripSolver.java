package solving;

import java.util.List;

import model.Flight;

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
    List<Flight> getFirstTripFound();
    
    /**
     * Réinitialise le solveur.
     */
    void reset();
}
