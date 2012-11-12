package model;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Pierrot Papi Dim
 */
public enum Airport {
	/** Paris */
	PAR(0, TimeZone.getTimeZone("Europe/Paris")),
	/** Barcelone */
	BCN(1, TimeZone.getTimeZone("Europe/Madrid")),
	/** Madrid */
	MAD(2, TimeZone.getTimeZone("Europe/Madrid")),
	/** Munich */
	MUC(3, TimeZone.getTimeZone("Europe/Berlin")),
	/** Londres */
	LON(4, TimeZone.getTimeZone("Europe/London")),
	/** Stockholm */
	STO(5, TimeZone.getTimeZone("Europe/Stockholm")),
	/** Copenhague */
	CPH(6, TimeZone.getTimeZone("Europe/Copenhagen")),
	/** Rome */
	ROM(7, TimeZone.getTimeZone("Europe/Rome")),
	/** Lisbonne */
	LIS(8, TimeZone.getTimeZone("Europe/Lisbon")),
	/** Frankfort */
	FRA(9, TimeZone.getTimeZone("Europe/Berlin")),
	/** Amsterdam */
	AMS(10, TimeZone.getTimeZone("Europe/Amsterdam")),
	/** Milan */
	MIL(11, TimeZone.getTimeZone("Europe/Rome")),
	/** Berlin */
	BER(12, TimeZone.getTimeZone("Europe/Berlin")),
	/** Oslo */
	OSL(13, TimeZone.getTimeZone("Europe/Oslo")),
	/** Dublin */
	DUB(14, TimeZone.getTimeZone("Europe/Dublin")),
	/** Venise */
	VCE(15, TimeZone.getTimeZone("Europe/Rome")),
	/** Dusseldorf */
	DUS(16, TimeZone.getTimeZone("Europe/Berlin")),
	/** Prague */
	PRG(17, TimeZone.getTimeZone("Europe/Prague")),
	/** Vienne */
	VIE(18, TimeZone.getTimeZone("Europe/Vienna")),
	/** Hamburg */
	HAM(19, TimeZone.getTimeZone("Europe/Berlin")),
	/** Geneve */
	GVA(20, TimeZone.getTimeZone("Europe/Zurich")),
	/** Helsinki */
	HEL(21, TimeZone.getTimeZone("Europe/Helsinki")),
	/** Toulouse */
	TLS(22, TimeZone.getTimeZone("Europe/Paris")),
	/** Zurich */
	ZRH(23, TimeZone.getTimeZone("Europe/Zurich")),
	/** Nice */
	NCE(24, TimeZone.getTimeZone("Europe/Paris")),
	/** Bruxelle */
	BRU(25, TimeZone.getTimeZone("Europe/Brussels")),
	/** Manchester */
	MAN(26, TimeZone.getTimeZone("Europe/London")),
	/** Lyon */
	LYS(27, TimeZone.getTimeZone("Europe/Paris")),
	/** Strasbourg */
	SXB(28, TimeZone.getTimeZone("Europe/Paris")),
	/** Nantes */
	NTE(29, TimeZone.getTimeZone("Europe/Paris"));
	
	/**
	 * The identification number of the airport.
	 */
	private int id;
	
	/**
	 * La TimeZone de l'Aeroport.
	 */
	private TimeZone timezone;
	
	/**
	 * @return L'identifiant unique de l'aeroport
	 */
	public int getId(){
		return this.id;
	}
	
	/**
	 * @return La TimeZone dans laquelle est l'aeroport.
	 */
	public TimeZone getTimeZone(){
		return this.timezone;
	}
	
	/**
	 * Constructeur
	 * @param identifier L'identifiant unique de l'aeroport
	 * @param timeZone la TimeZone de l'aeroport.
	 */
	private Airport(final int identifier, final TimeZone timeZone){
		this.id = identifier;
		this.timezone = timeZone;
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