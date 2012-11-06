package model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pierrot Papi Dim
 */
public enum Airport {
	PAR(0),
	BCN(1),
	MAD(2),
	MUC(3),
	LON(4),
	STO(5),
	CPH(6),
	ROM(7),
	LIS(8),
	FRA(9),
	AMS(10),
	MIL(11),
	BER(12),
	OSL(13),
	DUB(14),
	VCE(15),
	DUS(16),
	PRG(17),
	VIE(18),
	HAM(19),
	GVA(20),
	HEL(21),
	TLS(22),
	ZRH(23),
	NCE(24),
	BRU(25),
	MAN(26),
	LYS(27),
	SXB(28),
	NTE(29);
	
	/**
	 * The identification number of the airport
	 */
	private int id;
	
	public int getId(){
		return this.id;
	}
	
	private Airport(int id){
		this.id = id;
	}
	
	public static List<Integer> getDomain(List<Airport> list){
		List<Integer> retour = new ArrayList<Integer>();
		for(Airport a : list){
			retour.add(a.getId());
		}
		return retour;
	}
}