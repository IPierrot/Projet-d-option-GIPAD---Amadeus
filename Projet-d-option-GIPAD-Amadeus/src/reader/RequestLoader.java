package reader;

import java.util.List;

import context.userConstraints.cg.CG;
import context.userConstraints.cve.CVE;
import context.userConstraints.cvf.CVF;
import context.userConstraints.cvo.CVO;

public interface RequestLoader {

	/**
	 * Charge la requ�te pr�sente � l'adresse donn�e.
	 * @param dir Le chemin de la requ�te � charger.
	 */
	void loadRequest(String dir);

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
