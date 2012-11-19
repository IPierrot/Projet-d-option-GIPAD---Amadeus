package context;

import java.util.ArrayList;
import java.util.List;


import context.userConstraints.UserConstraint;
import context.userConstraints.cg.CG;
import context.userConstraints.cve.CVE;
import context.userConstraints.cvf.CVF;
import context.userConstraints.cvo.CVO;
import reader.RequestLoader;

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
		this.userConstraints = new ArrayList<UserConstraint>();
	}
	
	/**
	 * Charge une requ�te dans le client.
	 * @param dir Le chemin auquel se trouve le fichier de requ�te.
	 */
	public void loadRequest(final String dir){
		
	    System.out.println("- CHARGEMENT DE LA REQUETE - ");
	    long t = System.currentTimeMillis();
		// Lecture du fichier de requ�te.
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
		System.out.print(" Ok ! "+"("+(System.currentTimeMillis()-t)+"ms)");
		
	    t = System.currentTimeMillis();
		System.out.println("\n" + "\n"  + "- INITIALISATION DU MODELE -");
		// Application des contraintes.
		for(UserConstraint c : this.userConstraints){
			c.apply(this.context);
			System.out.print(".....");
		}
		System.out.print(" Ok ! "+"("+(System.currentTimeMillis()-t)+"ms)");
		
		t = System.currentTimeMillis();
		System.out.println("\n" + "\n"  
		        + "- CHARGEMENT DES VOLS DANS LA BASE DE DONNEES -");
		// Chargement des vols
		this.loadPossibleFlights();
		
	    System.out.print(" Ok ! "+"("+(System.currentTimeMillis()-t)+"ms)");

//	    // Filtrage final
//	    t = System.currentTimeMillis();
//        System.out.println("\n" + "\n"  
//                + "- FILTRAGE FINAL DES VOLS -");
//        
//        int i = 0;
//        for(UserConstraint c : this.userConstraints){
//            c.filter(context.getComplexTripModel().getPossibleFlights());
//            System.out.println(
//	    context.getComplexTripModel().getPossibleFlights().size());
//            if(i >= this.userConstraints.size()/5){
//                i = 0;
//                System.out.print(".....");
//            } else {
//                i++;
//            }
//        }
//        System.out.print(" Ok ! "+"("+(System.currentTimeMillis()-t)+"ms)");

		
		t = System.currentTimeMillis();
		System.out.println("\n" + "\n"  + "- CONSTRUCTION DU MODELE -");
		// Initialisation du complex trip model
		this.context.getComplexTripModel().build();
		System.out.print(" Ok ! "+"("+(System.currentTimeMillis()-t)+"ms)");
	}
	
	/**
	 * Charge les vols possibles susceptibles de satisfaire les contraintes
	 * du probl�me.
	 */
	private void loadPossibleFlights(){
			    
        int i = 1;
        for(UserConstraint c : this.userConstraints){
            c.loadFlights(context);
            
            System.out.println("Nombre de vols apr�s chargement " + i 
              + "(" + c.getClass().getSimpleName() + ") : " 
              + context.getComplexTripModel().getPossibleFlights().size());
            
            i++;
        }
	}
}
