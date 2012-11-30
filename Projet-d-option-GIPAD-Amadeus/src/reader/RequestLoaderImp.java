package reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import context.userConstraints.cg.CG;
import context.userConstraints.cg.CG00;
import context.userConstraints.cve.CVE;
import context.userConstraints.cvf.CVF;
import context.userConstraints.cvo.CVO;

/**
 * impl�mentation de l'interface RequestLoader, 
 * qui permet de charger des fichiers de requ�tes
 * @author Pierre Chouin
 *
 */
public class RequestLoaderImp implements RequestLoader {

    /**
     * double-point
     */
    public static final String DOUBLE_POINT = ":";
    
    /**
     * caract�re du commentaire
     */
    public static final String COMMENTAIRE = "#";
    
    /**
     * caract�re du s�parateur de dates
     */
    public static final String SEPARATEUR = ",";
    
    /**
     * String utilis�e pour repr�senter une contrainte sur la ville de d�part
     */
    public static final String CVO="CVO";
    
    /**
     * String utilis�e pour repr�senter une contrainte sur la ville d'arriv�e
     */
    public static final String CVF="CVF";
    
    /**
     * String utilis�e pour repr�senter une contrainte sur une ville-�tape
     */
    public static final String CVE="CVE";
    
    /**
     * String utilis�e pour repr�senter une contrainte g�n�rale 
     * sur la dur�e max
     */
    public static final String CG00="CG-00";
    
    /**
     * taille dans le fichier du nom simple des contraintes
     */
    public static final int TAILLE_CV=3;
    
    /**
     * taille dans le fichier du nom des contraintes g�n�rales
     */
    public static final int TAILLE_CG=5;
    
    /**
     * taille dans le fichier du nom complet des contraintes
     */
    public static final int TAILLE_CV_COMPLET=7;
    
    /**
     * taille dans le fichier du nom complet des contraintes g�n�rales
     */
    public static final int TAILLE_CG_COMPLET=6;
    
    /**
     * Contraintes sur la ville d'origine
     */
    private CVO cvo;
    
    /**
     * COntraintes sur la ville d'arriv�e
     */
    private CVF cvf;
    
    /**
     * liste de contraintes sur les villes-�tapes
     */
    private List<CVE> cves;
    
    /**
     * contraintes g�n�rales
     */
    private List<CG> cgs;
    
    @Override
    public boolean loadRequest(final String dir) {
        return loadRequest(new File(dir));
    }
    
    @Override
    public boolean loadRequest(final File file) {
        cves = new ArrayList<CVE> ();
        cgs = new ArrayList<CG> ();
        Scanner sc;
        try {
            sc = new Scanner(file);
            String s="";
            while(sc.hasNextLine()){
                
                s=sc.nextLine();
                if(s.length()>0&&s.substring(0, TAILLE_CV).equals(CVO)){
                    String CVO0 = getStringPropre(s, TAILLE_CV_COMPLET);
                    s=sc.nextLine();
                    String[] CVO1 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    s=sc.nextLine();
                    String[] CVO2 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    cvo= new CVO(CVO0, CVO1, CVO2);
                }
                
                if(s.length()>0&&s.substring(0, TAILLE_CV).equals(CVE)){
                    s=sc.nextLine();
                    String CVE0 = getStringPropre(s, TAILLE_CV_COMPLET);
                    s=sc.nextLine();
                    String CVE1s = getStringPropre(s, TAILLE_CV_COMPLET);
                    boolean CVE1=true;
                    if(Integer.parseInt(CVE1s)==0){ CVE1=false; }
                    s=sc.nextLine();
                    String[] CVE2 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    s=sc.nextLine();
                    String[] CVE3s = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    int [] CVE3 = {Integer.parseInt(CVE3s[0]), 
                            Integer.parseInt(CVE3s[1])};
                    s=sc.nextLine();
                    String[] CVE4 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    s=sc.nextLine();
                    int CVE5 = Integer.parseInt(getStringPropre(s,
                            TAILLE_CV_COMPLET));
                    CVE cve= new CVE(CVE0, CVE1, CVE2, CVE3, CVE4, CVE5);
                    cves.add(cve);          
                }
                
                if (s.length()>0&&s.substring(0, TAILLE_CV).equals(CVF)){
                    String CVF0 = getStringPropre(s, TAILLE_CV_COMPLET);
                    s=sc.nextLine();
                    String[] CVF1 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    s=sc.nextLine();
                    String[] CVF2 = getStringPropre(s, TAILLE_CV_COMPLET)
                            .split(SEPARATEUR);
                    cvf= new CVF(CVF0, CVF1, CVF2);
                }
                
                if (s.length()>0&&s.substring(0, TAILLE_CG).equals(CG00)){
                    String CG0s = getStringPropre(s, TAILLE_CG_COMPLET);
                    String[] CG0 = CG0s.split(",");
                    if(CG0.length==2){
                        int CG0Min= Integer.parseInt(CG0[0].trim());
                        int CG0Max= Integer.parseInt(CG0[1].trim());
                        cgs.add(new CG00(CG0Min, CG0Max));
                    }
                }
            }
            sc.close();
            return (cvo != null && cvf != null)? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        

    }
    
    

    @Override
    public CVO getCVO() {
        return cvo;
    }

    @Override
    public CVF getCVF() {
        return cvf;
    }

    @Override
    public List<CVE> getCVEs() {
       return cves;
    }

    @Override
    public List<CG> getCGs() {
        return cgs;
    }

    /**
     * retourne la String sans commentaires, sans la contrainte avant, 
     * et sans espaces, ni avant, ni apr�s
     * @param s la String que l'on veut modifier
     * @param nbCarInutiles le nombre de caract�res que l'on supprime 
     * au d�but de la cha�ne de caract�res
     * @return la String modifi�e
     */
    private String getStringPropre(final String s, final int nbCarInutiles){
        return s.substring(nbCarInutiles).split(COMMENTAIRE)[0].trim();
    }

}
