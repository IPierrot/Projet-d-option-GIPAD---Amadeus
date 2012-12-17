package reader;

/**
 * Classe contenant les constantes pour le package reader
 * @author Marc
 *
 */
public final class ReaderConstants {

    /**
     * la base dans laquelle on est.
     */
    public static final int BASE=10;

    /**
     * Nombre de fichiers que l'on veut créer
     */
    public static final int NUMBER_OF_FILES = 10;

    /**
     * Nombre maximal d'étapes possibles
     */
    public static final int NB_MAX_ETAPES=15;

    /**
     * Nombre de jours d'intervalle maximal d'arrêt dans une ville étape,
     *  ou pour le départ, ou pour l'arrivée
     */
    public static final int JOURS_D_INTERVALLE = 5;

    /**
     * Nombre maximal de jours du voyage
     */
    public static final int JOURS_VOYAGE=25;

    /**
     * Nombre d'heures dans une journée
     */
    public static final int NB_HOURS = 24;

    /**
     * Nombre de minutes dans une heure
     */
    public static final int NB_MIN = 60;

    /**
     * granularité en minutes
     */
    public static final int GRANULARITE = 5;

    /**
     * Nombre de grains dans une heure
     */
    public static final int GR_HOUR = (NB_MIN/GRANULARITE);

    /**
     * Nombre de grains dans une journée
     */
    public static final int GR_JOUR = NB_HOURS*GR_HOUR;

    /**
     * valeur entière par défaut
     */
    public static final int INTEGER_VALUE_DEFAULT=1;

    /**
     * plage horaire par défaut
     */
    public static final String PLAGE_DEFAULT = "08:00,22:00";

    /**
     * durée minimale plage horaire
     */
    public static final int PLAGE_MIN = 4*GR_HOUR;

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

}
