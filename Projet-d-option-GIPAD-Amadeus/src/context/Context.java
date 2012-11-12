package context;

import dao.DAO;
import solving.ComplexTripModel;

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
	private ComplexTripModel cxtModel;
	
	/**
	 * La DAO.
	 */
	private DAO dao;
	
	/**
	 * Constructeur avec paramètres.
	 * @param cxtripModel Le ComplexTripModel.
	 * @param daoObject La DAO.
	 */
	public Context(final ComplexTripModel cxtripModel, final DAO daoObject){
		this.cxtModel = cxtripModel;
		this.dao = daoObject;
	}
	
	/**
	 * @return Le ComplexTripModel du contexte.
	 */
	public ComplexTripModel getComplexTripModel(){
		return this.cxtModel;
	}
	
	/**
	 * @return La DAO du contexte.
	 */
	public DAO getDao(){
		return this.dao;
	}
}
