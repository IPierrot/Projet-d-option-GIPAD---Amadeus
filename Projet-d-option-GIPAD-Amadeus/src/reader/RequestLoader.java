package reader;

import java.util.List;

import context.userConstraints.cg.CG;
import context.userConstraints.cve.CVE;
import context.userConstraints.cvf.CVF;
import context.userConstraints.cvo.CVO;

public interface RequestLoader {

	/**
	 * Charge la requête présente à l'adresse donnée.
	 * @param dir Le chemin de la requête à charger.
	 */
	void loadRequest(String dir);

	/**
	 * @return La contrainte CVO chargée (si requête chargée, null sinon).
	 */
	CVO getCVO();

	/**
	 * @return La contrainte CVF chargée (si requête chargée, null sinon).
	 */
	CVF getCVF();

	/**
	 * @return La liste de contraintes CVE chargées (si requête chargée,
	 *  null sinon).
	 */
	List<CVE> getCVEs();

	/**
	 * @return La liste des contraintes CVF chargées (si requête chargée,
	 *  null sinon).
	 */
	List<CG> getCGs();

}
