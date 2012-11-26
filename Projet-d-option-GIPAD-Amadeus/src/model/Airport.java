package model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pierrot Papi Dim
 */
public enum Airport {
	/** Paris */
	PAR(0),
	/** Barcelone */
	BCN(1),
	/** Madrid */
	MAD(2),
	/** Munich */
	MUC(3),
	/** Londres */
	LON(4),
	/** Stockholm */
	STO(5),
	/** Copenhague */
	CPH(6),
	/** Rome */
	ROM(7),
	/** Lisbonne */
	LIS(8),
	/** Frankfort */
	FRA(9),
	/** Amsterdam */
	AMS(10),
	/** Milan */
	MIL(11),
	/** Berlin */
	BER(12),
	/** Oslo */
	OSL(13),
	/** Dublin */
	DUB(14),
	/** Venise */
	VCE(15),
	/** Dusseldorf */
	DUS(16),
	/** Prague */
	PRG(17),
	/** Vienne */
	VIE(18),
	/** Hamburg */
	HAM(19),
	/** Geneve */
	GVA(20),
	/** Helsinki */
	HEL(21),
	/** Toulouse */
	TLS(22),
	/** Zurich */
	ZRH(23),
	/** Nice */
	NCE(24),
	/** Bruxelle */
	BRU(25),
	/** Manchester */
	MAN(26),
	/** -- */
	LYS(27),
	/** -- */
	SXB(28),
	/** Nantes */
	NTE(29);
	
	/**
	 * The identification number of the airport
	 */
	private int id;
	
	/**
	 * @return L'identifiant unique de l'aeroport
	 */
	public int getId(){
		return this.id;
	}
	
	/**
	 * Constructeur
	 * @param identifier L'identifiant unique de l'aeroport
	 */
	private Airport(final int identifier){
		this.id = identifier;
	}
	
	/**
	 * 
	 * @param list Une liste d'Airport
	 * @return Une liste d'Integer correspondant au id des Airport presents dans
	 * list
	 */
	public static List<Integer> getDomain(final List<Airport> list){
		List<Integer> retour = new ArrayList<Integer>();
		for(Airport a : list){
			retour.add(a.getId());
		}
		return retour;
	}
}