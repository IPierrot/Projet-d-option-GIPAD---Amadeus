package reader;

import java.io.File;
import java.util.List;

import context.userConstraints.cg.CG;
import context.userConstraints.cve.CVE;
import context.userConstraints.cvf.CVF;
import context.userConstraints.cvo.CVO;

/**
 * Chargeur de requ�tes
 * @author Dimitri, Marc et Pierre
 *
 */
public interface RequestLoader {

	/**
	 * Charge la requ�te pr�sente � l'adresse donn�e.
	 * @param dir Le chemin de la requ�te � charger.
	 * @return true si le chargement a r�ussi.
	 */
	boolean loadRequest(String dir);
	
	/**
     * Charge la requ�te pr�sente � l'adresse donn�e.
     * @param file La requ�te � charger sous forme de fichier.
     * @return true si le chargement a r�ussi.
     */
    boolean loadRequest(File file);

	/**
	 * @return La contrainte CVO charg�e (si requ�te charg�e, null sinon).
	 */
	CVO getCVO();

	/**
	 * @return La contrainte CVF charg�e (si requ�te charg�e, null sinon).
	 */
	CVF getCVF();

	/**
	 * @return La liste de contraintes CVE charg�es (si requ�te charg�e,
	 *  null sinon).
	 */
	List<CVE> getCVEs();

	/**
	 * @return La liste des contraintes CVF charg�es (si requ�te charg�e,
	 *  null sinon).
	 */
	List<CG> getCGs();

}
