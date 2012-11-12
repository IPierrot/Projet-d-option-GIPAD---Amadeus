package context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Airport;
import model.Flight;

import context.userConstraints.UserConstraint;
import context.userConstraints.cg.CG;
import context.userConstraints.cve.CVE;
import context.userConstraints.cvf.CVF;
import context.userConstraints.cvo.CVO;
import dao.DAO;
import reader.RequestLoader;
import solving.ComplexTripModel;

/**
 * Repr�sente un client du g�n�rateur de voyages complexe.
 * Est le point de liaison entre la lecture de requ�te, le
 * moteur de contraintes et la couche d'acc�s au donn�es.
 * @author Dim
 *
 */
public class Client {

	/**
	 * Le contexte donnant acc�s � la dao et au moteur de contraintes.
	 */
	private Context context;
	
	/**
	 * Le chargeur de requ�tes.
	 */
	private RequestLoader requestLoader;
	
	/**
	 * La contrainte sur la ville d'origine.
	 */
	private CVO cvo;
	
	/**
	 * La contrainte sur la ville finale.
	 */
	private CVF cvf;
	
	/**
	 * Les contraintes sur les �tapes.
	 */
	private List<CVE> cves;
	
	/**
	 * Les contraintes g�n�rales
	 */
	private List<CG> cgs;
	
	/**
	 * L'ensemble de toutes les contraintes
	 */
	private List<UserConstraint> userConstraints;
	
	/**
	 * Constructeur avec param�tres.
	 * @param ctx Le context.
	 * @param rLoader Le loader de requ�tes.
	 */
	public Client(final Context ctx, final RequestLoader rLoader){
		this.context = ctx;
		this.requestLoader = rLoader;
	}
	
	/**
	 * Charge une requ�te dans le client.
	 * @param dir Le chemin auquel se trouve le fichier de requ�te.
	 */
	public void loadRequest(final String dir){
		
		// Lecture du fichier de requ�te.
		this.requestLoader.loadRequest(dir);
		this.cvo = this.requestLoader.getCVO();
		this.cvf = this.requestLoader.getCVF();
		this.cves = this.requestLoader.getCVEs();
		this.cgs = this.requestLoader.getCGs();
		
		// Hierarchisation des contraintes.
		this.userConstraints.add(cvo);
		this.userConstraints.add(cvf);
		this.userConstraints.addAll(cves);
		this.userConstraints.addAll(cgs);
		
		// Application des contraintes.
		for(UserConstraint c : this.userConstraints){
			c.apply(this.context);
		}
		
		// Chargement des vols
		this.loadPossibleFlights();
	}
	
	/**
	 * Charge les vols possibles susceptibles de satisfaire les contraintes
	 * du probl�me.
	 */
	private void loadPossibleFlights(){
		
		List<Flight> possibleFlights = new ArrayList<Flight>();
		ComplexTripModel ctxm = this.context.getComplexTripModel();
		DAO dao = this.context.getDao();
		
		// R�cup�ration des aeroports de d�part, de fin et des �tapes.
		Airport origin = ctxm.getStart();
		Airport end = ctxm.getEnd();
		List<Airport> stages = ctxm.getStages();
		
		// R�cup�ration des dates entre lesquel on va r�cup�rer des vols.
		Date d1 = ctxm.getEarliestDeparture();
		Date d2 = ctxm.getLatestArrival();
		
		// Ajout des vols
		possibleFlights.addAll(dao.getFlightsFromAirportToList(
				origin, stages, d1, d2));
		possibleFlights.addAll(dao.getFlightsFromListToAirport(
				stages, end, d1, d2));
		possibleFlights.addAll(dao.getFlightsFromListToList(
				stages, stages, d1, d2));
		
		// Filtrage des vols
		this.filterFlights(possibleFlights);
		
		// Injection des vols dans le mod�le
		for(Flight f : possibleFlights){
			ctxm.addPossibleFlight(f);
		}
	}
	
	/**
	 * Filtre les vols via la m�thode de filtrage locale de chacunes des
	 * contraintes utilisateur.
	 * @param flights La liste � filtrer.
	 */
	private void filterFlights(final List<Flight> flights){
		for(UserConstraint c : this.userConstraints){
			c.filter(flights);
		}
	}
}
