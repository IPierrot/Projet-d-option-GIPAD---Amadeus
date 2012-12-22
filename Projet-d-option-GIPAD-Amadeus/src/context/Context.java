package context;

import io.dao.DAO;
import solving.IComplexTripSolver;

/**
 * Représente un contexte qui regroupe les services de la DAO
 * et du moteur de contraintes.
 * @author Dim
 *
 */
public class Context {

	/**
	 * Le ComplexTripModel.
	 */
	private IComplexTripSolver cxtSolver;
	
	/**
	 * La DAO.
	 */
	private DAO dao;
	
	/**
	 * Constructeur avec paramètres.
	 * @param cxtripSolver Le ComplexTripSolver.
	 * @param daoObject La DAO.
	 */
	public Context(final IComplexTripSolver cxtripSolver, final DAO daoObject){
		this.cxtSolver = cxtripSolver;
		this.dao = daoObject;
	}
	
	/**
	 * @return Le ComplexTripModel du contexte.
	 */
	public IComplexTripSolver getComplexTripSolver(){
		return this.cxtSolver;
	}
	
	/**
	 * @return La DAO du contexte.
	 */
	public DAO getDao(){
		return this.dao;
	}
}
