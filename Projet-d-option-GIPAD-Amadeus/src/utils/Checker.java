package utils;

import static utils.DateOperations.getDateFromPattern;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimeZone;

import junit.framework.TestCase;

import model.Trip;

/**
 * classe vérifiant si la solution proposée est juste
 * @author Pierre Chouin
 *
 */
public class Checker extends TestCase{

    
    /**
     * nombre d'heures par jour
     */
    public static final int HOURS_PER_DAY=24;
    
    /**
     * le nombre de millisecondes par heure
     */
    public static final int MILLIS_PER_H=1000*60*60;
    
    /**
     * double-point
     */
    public static final String DOUBLE_POINT = ":";

    /**
     * caractère du commentaire
     */
    public static final String COMMENTAIRE = "#";

    /**
     * caractère du séparateur de dates
     */
    public static final String SEPARATEUR = ",";

    /**
     * String utilisée pour représenter une contrainte sur la ville de départ
     */
    public static final String CVO="CVO";

    /**
     * String utilisée pour représenter une contrainte sur la ville d'arrivée
     */
    public static final String CVF="CVF";

    /**
     * String utilisée pour représenter une contrainte sur une ville-étape
     */
    public static final String CVE="CVE";

    /**
     * String utilisée pour représenter une contrainte générale 
     * sur la durée max
     */
    public static final String CG00="CG-00";

    /**
     * String utilisée pour représenter une contrainte générale 
     * sur l'ordre
     */
    public static final String CG01="CG-01";


    /**
     * taille dans le fichier du nom simple des contraintes
     */
    public static final int TAILLE_CV=3;

    /**
     * taille dans le fichier du nom des contraintes générales
     */
    public static final int TAILLE_CG=5;

    /**
     * taille dans le fichier du nom complet des contraintes
     */
    public static final int TAILLE_CV_COMPLET=7;

    /**
     * taille dans le fichier du nom complet des contraintes générales
     */
    public static final int TAILLE_CG_COMPLET=6;

    /**
     * méthode vérifiant la solution
     * @param sol la solution que l'on veut vérifier
     * @param path le fichier de requêtes original
     * @return si c'est vérifié, ou nan
     */
    public static boolean checkSolution(final Trip sol, final String path){

        HashMap<String, Integer> etapesRang = new HashMap<String, Integer>();

        File file= new File(path);
        Scanner sc;
        try {
            sc = new Scanner(file);
            String s="";
            while(sc.hasNextLine()){

                s=sc.nextLine();
                if(s.length()>0&&s.substring(0, TAILLE_CV).equals(CVO)){
                    String CVO0 = getStringPropre(s, TAILLE_CV_COMPLET);
                    assertEquals(CVO0, sol.getStart().name());
                    s=sc.nextLine();
                    String[] CVO1 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    assertIntDate(CVO1[0], CVO1[1], 
                            sol.getStart().getTimeZone(), 
                            sol.getStartDeparture());
                    s=sc.nextLine();
                    String[] CVO2 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    assertIntHour(CVO2[0], CVO2[1], 
                            sol.getStart().getTimeZone(), 
                            sol.getStartDeparture());
                }

                if(s.length()>0&&s.substring(0, TAILLE_CV).equals(CVE)){
                    String nomCVE = getStringPropre(s, 0);
                    s=sc.nextLine();
                    String CVE0 = getStringPropre(s, TAILLE_CV_COMPLET);
                    int id=-1;
                    for(int i=0; i<sol.getStages().size(); i++){
                        if(sol.getStages().get(i).name().equals(CVE0)) {
                            id=i; break; 
                            }
                    }
                    etapesRang.put(nomCVE, id);
                    s=sc.nextLine();
                    String CVE1s = getStringPropre(s, TAILLE_CV_COMPLET);
                    boolean CVE1=true;
                    if(Integer.parseInt(CVE1s)==0){ CVE1=false; }
                    if(CVE1){
                        assertNotSame(-1, id);
                    }
                    s=sc.nextLine();
                    String[] CVE2 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    if(id!=-1){
                        assertIntDate(CVE2[0], CVE2[1], 
                                sol.getStages().get(id).getTimeZone(), 
                                sol.getFlights().get(id).getArrival());
                        assertIntDate(CVE2[0], CVE2[1], 
                                sol.getStages().get(id).getTimeZone(), 
                                sol.getFlights().get(id+1).getDeparture());
                    }
                    s=sc.nextLine();
                    String[] CVE3s = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    int [] CVE3 = {Integer.parseInt(CVE3s[0]), 
                            Integer.parseInt(CVE3s[1])};
                    if(id!=-1){
                        assertDurInt(CVE3[0], CVE3[1], 
                                sol.getFlights().get(id).getArrival(), 
                                sol.getFlights().get(id+1).getDeparture());
                    }
                    s=sc.nextLine();
                    String[] CVE4 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    s=sc.nextLine();
                    int CVE5 = Integer.parseInt(getStringPropre(s,
                            TAILLE_CV_COMPLET));
                    if(id!=-1){
                        assertPresInt(CVE4[0], CVE4[1], CVE5, 
                                sol.getFlights().get(id).getArrival(), 
                                sol.getFlights().get(id+1).getDeparture());
                    }
                }

                if (s.length()>0&&s.substring(0, TAILLE_CV).equals(CVF)){
                    String CVF0 = getStringPropre(s, TAILLE_CV_COMPLET);
                    assertEquals(CVF0, sol.getEnd().name());
                    s=sc.nextLine();
                    String[] CVF1 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    assertIntDate(CVF1[0], CVF1[1], 
                            sol.getEnd().getTimeZone(), sol.getEndArrival());
                    s=sc.nextLine();
                    String[] CVF2 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    assertIntHour(CVF2[0], CVF2[1], 
                            sol.getEnd().getTimeZone(), sol.getEndArrival());
                }

                if (s.length()>0&&s.substring(0, TAILLE_CG).equals(CG00)){
                    String CG0s = getStringPropre(s, TAILLE_CG_COMPLET);
                    String[] CG0 = CG0s.split(",");
                    if(CG0.length==2){
                        int CG0Min= Integer.parseInt(CG0[0].trim());
                        int CG0Max= Integer.parseInt(CG0[1].trim());
                        assertDurInt(CG0Min, CG0Max, 
                                sol.getStartDeparture(), sol.getEndArrival());
                    }
                }

                if (s.length()>0&&s.substring(0, TAILLE_CG).equals(CG01)){
                    String CG1s = getStringPropre(s, TAILLE_CG_COMPLET);
                    String[] CG1 = CG1s.split("<");
                    if(CG1.length==2){
                        assertInfStr(etapesRang.get(CG1[0]),
                                etapesRang.get(CG1[1]));
                    }
                }

            }
            sc.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }




    }

    /**
     * retourne la String sans commentaires, sans la contrainte avant, 
     * et sans espaces, ni avant, ni après
     * @param s la String que l'on veut modifier
     * @param nbCarInutiles le nombre de caractères que l'on supprime 
     * au début de la chaîne de caractères
     * @return la String modifiée
     */
    private static String getStringPropre(final String s, 
            final int nbCarInutiles){
        return s.substring(nbCarInutiles).split(COMMENTAIRE)[0].trim();
    }

    /**
     * Vérifie si la date actual est bien située entre dateDeb et dateFin
     * @param dateDeb date de début
     * @param dateFin date de fin
     * @param tz la time zone
     * @param actual la date que l'on vérifie
     */
    private static void assertIntDate(final String dateDeb, 
            final String dateFin, final TimeZone tz, final Date actual){
        String pattern = "yyyy/MM/dd-HH:mm";
        try {
            Date deb = getDateFromPattern(pattern, dateDeb, tz);
            Date fin = getDateFromPattern(pattern, dateFin, tz);
            if(!actual.after(deb)||!actual.before(fin)){ 
                throw new AssertionError("Erreur au niveau des "
                		+ "intervalles de dates");
                }
        } catch (ParseException e) {
            System.out.println("Erreur dans la lecture des dates du"
                    + " fichier de requête (CVF)");
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si l'heure de la date actual est entre hourDeb et hourFin
     * @param hourDeb heure de début
     * @param hourFin heure de fin
     * @param tz la time zone
     * @param actual la date que l'on veut vérifier
     */
    private static void assertIntHour(final String hourDeb, 
            final String hourFin, final TimeZone tz, final Date actual){
        String pattern = "yyyy/MM/dd-HH:mm";
        try {
            long deb = getDateFromPattern(pattern, "2012/01/01-"+hourDeb, tz)
                    .getTime()%(MILLIS_PER_H*HOURS_PER_DAY);
            long fin = getDateFromPattern(pattern, "2012/01/01-"+hourFin, tz)
                    .getTime()%(MILLIS_PER_H*HOURS_PER_DAY);
            long newActual = actual.getTime()%(MILLIS_PER_H*HOURS_PER_DAY);
            if(!(deb<=newActual)||!(newActual<=fin)){
                throw new AssertionError("Erreur au niveau des heures de"
                		+ " départ, ou d'arrivée");
                }
        } catch (ParseException e) {
            System.out.println("Erreur dans la lecture des dates du"
                    + " fichier de requête (CVF)");
            e.printStackTrace();
        }


    }

    /**
     * Vérifie si la durée entre la date deb et la date fin est comprise
     *  entre minHour et maxHour
     * @param minHour durée minimum
     * @param maxHour durée maximum
     * @param deb date de début
     * @param fin date de fin
     */
    private static void assertDurInt(final int minHour, final int maxHour,
            final Date deb, final Date fin){
        int actual = (int) (fin.getTime()-deb.getTime())/(MILLIS_PER_H);
        if(!(minHour<=actual)||!(actual<=maxHour)){ 
            throw new AssertionError("Erreur au niveau des durées");
            }
    }

    /**
     * vérifie que entre la date deb et la date fin il existe au minimum 
     * nbTimes fois l'intervalle [hourDeb,hourFin]
     * @param hourDeb heure de début de l'intervalle
     * @param hourFin heure de fin de l'intervalle
     * @param nbTimes le nombre de fois
     * @param deb date de début
     * @param fin date de fin
     */
    private static void assertPresInt(final String hourDeb, 
            final String hourFin, final int nbTimes, 
            final Date deb, final Date fin){
        //TODO mais c'est difficile !
    }

    /**
     * vérifie que a est strictement inférieur à b
     * @param a 
     * @param b 
     */
    private static void assertInfStr(final int a, final int b){
        if(a>=b) {
            throw new AssertionError("Erreur sur une contrainte d'ordre"); 
            }
    }

}
