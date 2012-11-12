package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Classe utilitaire regroupant un ensemble d'opérations utiles sur les dates
 * @author Dim
 *
 */
public final class DateOperations {
	
	/**
	 * Constructeur privé vide.
	 */
	private DateOperations(){
	}
	
	/**
	 * Timezone spécifié dans le String
	 * @param pattern Le pattern à utiliser pour représenter les dates :
	 * YYYY ou YY : year
	 * MM : month
	 * dd : day of the month
	 * HH : hour (0-23)
	 * mm : minutes
	 * z : timezone
	 * @param date La date sous format texte à renvoyer sous forme de Date
	 * @return Une Date correspondant à la date texte donnée sous la forme
	 * du pattern.
	 * @throws ParseException 
	 */
	public static Date getDateFromPattern(final String pattern,
			final String date) throws ParseException{
		DateFormat df = new SimpleDateFormat(pattern);
		Date d = df.parse(date);
		return d;
	}
	
	/**
	 * Timezone non spécifié dans le String
	 * @param pattern Le pattern à utiliser pour représenter les dates :
	 * YYYY ou YY : year
	 * MM : month
	 * dd : day of the month
	 * HH : hour (0-23)
	 * mm : minutes
	 * @param date La date sous format texte à renvoyer sous forme de Date.
	 * @param tz La timezone à utiliser.
	 * @return Une Date correspondant à la date texte donnée sous la forme
	 * du pattern.
	 * @throws ParseException 
	 */
	public static Date getDateFromPattern(final String pattern,
			final String date, final TimeZone tz) throws ParseException{
		DateFormat df = new SimpleDateFormat(pattern);
		df.setTimeZone(tz);
		Date d = df.parse(date);
		return d;
	}
	
	/**
	 * @param pattern pattern Le pattern à utiliser pour représenter les dates :
	 * YYYY ou YY : year
	 * MM : month
	 * dd : day of the month
	 * HH : hour (0-23)
	 * mm : minutes
	 * z : timezone.
	 * @param date La date à formater en String.
	 * @return Une représentation sous la forme du pattern de la date.
	 */
	public static String formatDate(final String pattern, final Date date){
		DateFormat df = new SimpleDateFormat(pattern);
		String d = df.format(date);
		return d;
	}
	
	/**
	 * @param d La date à vérifier.
	 * @param pattern Le pattern utilisé pour l'heure :
	 * HH : hour (0-23)
	 * mm : minutes.
	 * @param h1 L'heure min.
	 * @param h2 L'heure max.
	 * @return true si la date (Date Time) donnée est entre h1 et h2. 
	 * @throws ParseException 
	 */
	public static boolean isBetweenHours(final Date d, final String pattern,
			final String h1, final String h2) throws ParseException{
		DateFormat df = new SimpleDateFormat("ddMMYYYY");
		String day = df.format(d);
		df = new SimpleDateFormat("ddMMYYYY" + pattern);
		Date d1 = df.parse(day + h1);
		Date d2 = df.parse(day + h2);
		return (d.after(d1) && d.before(d2));
	}

	///////////////////////////////////////////
	/////////// DATE METHODS FOR DAO //////////
	///////////////////////////////////////////

    /**
     * Genere un objet Date en fonction des parametres lus sur le fichier
     * @param yyMMdd le jour selectionne
     * @param time l'heure a convertir
     * @param gmt le fuseau horaire GMT
     * @param offset le nombre de jours de decalage
     * @return la date correspondante (Java.util.Date)
     */
    public static Date generateDate(final String yyMMdd, final String time, 
            final String gmt, final String offset){
        int dd = Integer.parseInt(yyMMdd.substring(4, 6))
                + Integer.parseInt(offset);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmZZZZZ");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        try {
            return sdf.parse(yyMMdd.substring(0, 2)+yyMMdd.substring(2, 4)
                    +dd+time.substring(0, 2) + time.substring(2, 4) + gmt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Genere un objet Date en fonction des parametres lus sur le fichier
     * @param yyMMdd le jour selectionne
     * @param time l'heure a convertir
     * @param gmt le fuseau horaire GMT
     * @return la date correspondante (Java.util.Date)
     */
    public static Date generateDate(final String yyMMdd, 
            final String time, final String gmt){
        return generateDate(yyMMdd, time, gmt, "0");
    }
    
    /**
     * Genere un objet date comprenant uniquement l'heure et le fuseau gmt
     * @param time le string de l'heure
     * @param gmt le fuseau
     * @return la date (01/01/1970) + l'heure passee
     */
    public static Date generateHour(final String time, final String gmt){
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmZZZZZ");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        try {
            return sdf.parse(time + gmt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
