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
 * Représente un client du générateur de voyages complexe.
 * Est le point de liaison entre la lecture de requête, le
 * moteur de contraintes et la couche d'accès au données.
 * @author Dim
 *
 */
public class Client {

	/**
	 * Le contexte donnant accès à la dao et au moteur de contraintes.
	 */
	private Context context;
	
	/**
	 * Le chargeur de requêtes.
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
	 * Les contraintes sur les étapes.
	 */
	private List<CVE> cves;
	
	/**
	 * Les contraintes générales
	 */
	private List<CG> cgs;
	
	/**
	 * L'ensemble de toutes les contraintes
	 */
	private List<UserConstraint> userConstraints;
	
	/**
	 * Constructeur avec paramètres.
	 * @param ctx Le context.
	 * @param rLoader Le loader de requêtes.
	 */
	public Client(final Context ctx, final RequestLoader rLoader){
		this.context = ctx;
		this.requestLoader = rLoader;
		this.userConstraints = new ArrayList<UserConstraint>();
	}
	
	/**
	 * Charge une requète dans le client.
	 * @param dir Le chemin auquel se trouve le fichier de requête.
	 */
	public void loadRequest(final String dir){
		
	    System.out.println("- CHARGEMENT DE LA REQUETE - ");
		// Lecture du fichier de requête.
		this.requestLoader.loadRequest(dir);
		System.out.print("..........");
		this.cvo = this.requestLoader.getCVO();
	    System.out.print("..........");
		this.cvf = this.requestLoader.getCVF();
		System.out.print("..........");
		this.cves = this.requestLoader.getCVEs();
		System.out.print("..........");
		this.cgs = this.requestLoader.getCGs();
		System.out.print("..........");
		
		// Hierarchisation des contraintes.
		this.userConstraints.add(cvo);
		this.userConstraints.add(cvf);
		this.userConstraints.addAll(cves);
		this.userConstraints.addAll(cgs);
		System.out.print(" Ok !");
		
		System.out.println("\n" + "\n"  + "- INITIALISATION DU MODELE -");
		// Application des contraintes.
		for(UserConstraint c : this.userConstraints){
			c.apply(this.context);
			System.out.print(".....");
		}
		System.out.print(" Ok !");
		
		System.out.println("\n" + "\n"  
		        + "- CHARGEMENT DES VOLS DANS LA BASE DE DONNEES -");
		// Chargement des vols
		this.loadPossibleFlights();
		System.out.print(" Ok !");
		
		System.out.println("\n" + "\n"  + "- CONSTRUCTION DU MODELE -");
		// Initialisation du complex trip model
		this.context.getComplexTripModel().build();
		System.out.print(" Ok !");
	}
	
	/**
	 * Charge les vols possibles susceptibles de satisfaire les contraintes
	 * du problème.
	 */
	private void loadPossibleFlights(){
		
		List<Flight> possibleFlights = new ArrayList<Flight>();
		ComplexTripModel cxtm = this.context.getComplexTripModel();
		DAO dao = this.context.getDao();
		
		// Récupération des aeroports de départ, de fin et des étapes.
		Airport origin = cxtm.getStartAirport();
		Airport end = cxtm.getEndAirport();
		List<Airport> stages = cxtm.getStages();
		
		// Récupération des dates entre lesquel on va récupérer des vols.
		Date d1 = cxtm.getEarliestDeparture();
		Date d2 = cxtm.getLatestDeparture();
		Date d3 = cxtm.getEarliestArrival();
		Date d4 = cxtm.getLatestArrival();
		
		System.out.print("........");
		
		// Ajout des vols
		possibleFlights.addAll(dao.getFlightsFromAirportToList(
				origin, stages, d1, d2));
		
		System.out.print("........");
		
		possibleFlights.addAll(dao.getFlightsFromListToAirport(
				stages, end, d3, d4));
		
		System.out.print("........");
		
		possibleFlights.addAll(dao.getFlightsFromListToList(
				stages, stages, d1, d4));
		
		System.out.print(" Ok !");
		
		
		// Filtrage des vols
	    System.out.println("\n" + "\n"  + "- FILTRAGE PRELIMINAIRE DES VOLS-");
		this.filterFlights(possibleFlights);
		
		System.out.print("Ok !");
		
		// Injection des vols dans le modèle
	    System.out.println("\n"+"\n"  + "-INJECTION DES VOLS DANS LE MODELE-");
		int i = 0;
	    for(Flight f : possibleFlights){
			cxtm.addPossibleFlight(f);
			i++;
			if(i >= possibleFlights.size()/50){
			    System.out.print(".");
			    i = 0;
			}
		}
	}
	
	/**
	 * Filtre les vols via la méthode de filtrage locale de chacunes des
	 * contraintes utilisateur.
	 * @param flights La liste à filtrer.
	 */
	private void filterFlights(final List<Flight> flights){
	    System.out.println("\n" + "Nombre de vols avant filtrage : " 
	                        + flights.size());
	    int i = 1;
		for(UserConstraint c : this.userConstraints){
			c.filter(flights);
		    System.out.println("Nombre de vols après filtrage " + i 
		      + "(" + c.getClass().getSimpleName() + ") : " + flights.size());
		    i++;
		}
	}
}
