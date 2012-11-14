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
     * Lit le mod�le et ajoute les contraintes.
     * @param ctxModel Le mod�le � lire.
     */
    void read(ComplexTripModel ctxModel);
    
    /**
     * La m�thode read doit avoir �t� appell�e avant.
     * @return La liste des vols composant la premi�re solution trouv�e.
     */
    List<Flight> getFirstTripFound();
    
    /**
     * R�initialise le solveur.
     */
    void reset();
}
