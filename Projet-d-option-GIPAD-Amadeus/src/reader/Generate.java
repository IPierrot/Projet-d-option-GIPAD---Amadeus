package reader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import model.Airport;

/**
 * Classe g�n�rant des fichiers types de requ�tes
 * @author Pierre Chouin
 *
 */
public final class Generate {


    /**
     * la base dans laquelle on est.
     */
    public static final int BASE=10;

    /**
     * Nombre de fichiers que l'on veut cr�er
     */
    public static final int NUMBER_OF_FILES = 100;

    /**
     * Nombre maximal d'�tapes possibles
     */
    public static final int NB_MAX_ETAPES=15;

    /**
     * Nombre de jours d'intervalle maximal d'arr�t dans une ville �tape,
     *  ou pour le d�part, ou pour l'arriv�e
     */
    public static final int JOURS_D_INTERVALLE = 5;

    /**
     * Nombre maximal de jours du voyage
     */
    public static final int JOURS_VOYAGE=20;

    /**
     * Nombre d'heures dans une journ�e
     */
    public static final int NB_HOURS = 24;

    /**
     * Nombre de minutes dans une heure
     */
    public static final int NB_MIN = 60;

    /**
     * granularit� en minutes
     */
    public static final int GRANULARITE = 5;

    /**
     * Nombre de grains dans une heure
     */
    public static final int GR_HOUR = (NB_MIN/GRANULARITE);

    /**
     * Nombre de grains dans une journ�e
     */
    public static final int GR_JOUR = NB_HOURS*GR_HOUR;

    /**
     * valeur enti�re par d�faut
     */
    public static final int INTEGER_VALUE_DEFAULT=1;
    
    /**
     * plage horaire par d�faut
     */
    public static final String PLAGE_DEFAULT = "08:00,22:00";
    
    /**
     * dur�e minimale plage horaire
     */
    public static final int PLAGE_MIN = 4*GR_HOUR;

    /**
     * Constructeur priv� vide
     */
    private Generate(){}


    /**
     * faire un panel de fichier
     * @param myDir dossier o� seront les fichiers
     * @param nameFiles nom qu'auront les fichiers, avec un petit index en plus
     */
    private static void createFiles(final Path myDir, final String nameFiles){

        for (int i=0; i<NUMBER_OF_FILES; i++){
            //use the above Path instance as an anchor
            String nameFile = nameFiles +i+".txt";
            createFile(myDir, nameFile);
        }

    }


    /**
     * Cr�er un fichier de requ�te
     * @param myDir : Path du dossier dans lequel on veut cr�er le fichier
     * @param nameFile : nom du fichier
     * @param nbEtapes : le nombre d'�tapes
     * @param plageAller : plage horaire du d�part
     * @param plageRetour : plage horaire du retour
     */
    private static void createFile(final Path myDir, final String nameFile, 
            final int nbEtapes, final String plageAller, 
            final String plageRetour){

        Path createFile = myDir.resolve(nameFile);

        List<Airport> alreadyTaken= new ArrayList<Airport>();


        try {

            OpenOption[] options = {StandardOpenOption.WRITE, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING};

            BufferedWriter br = Files.newBufferedWriter(createFile, 
                    Charset.forName("UTF-8"), options);



            Airport departAirport=pickAirport(alreadyTaken);

            br.write("CVO-00: "+departAirport.toString()+" \n");

            alreadyTaken.add(departAirport);

            br.write("CVO-01: "+getInterDateString(GR_JOUR)+"\n"); 
            //on part du principe qu'on part toujours le 01 d�cembre

            br.write("CVO-02: "+plageAller+"\n");

            //int nbEtapes=(int) (Math.random()*NB_MAX_ETAPES);
            
            int[] intArrivee = getInterDateTime(nbEtapes*2*GR_JOUR, 
                    JOURS_VOYAGE*GR_JOUR);

            br.write("\n");

           

            for(int j=0; j<nbEtapes; j++){
                br.write("CVE"+j+"\n");

                Airport etapAirport=pickAirport(alreadyTaken);

                br.write("CVE-00: "+etapAirport.toString()+"\n");

                alreadyTaken.add(etapAirport);

                br.write("CVE-01: "+INTEGER_VALUE_DEFAULT+"\n");

                int[] intEtape=getInterDateTime(intArrivee[0]);

                br.write("CVE-02: "
                        + getInterDateString(intEtape[0], intEtape[1])+"\n");

                int dmin= getRandomTime(intEtape[1]);
                int dmax = getRandomTime(intEtape[1]-dmin)+dmin;
                br.write("CVE-03: "+dmin/GR_HOUR+","+dmax/GR_HOUR+"\n");
                br.write("CVE-04: "+getPlageHoraire()+"\n");
                br.write("CVE-05: "+INTEGER_VALUE_DEFAULT+"\n");
                br.write("\n");
            }



            alreadyTaken.remove(departAirport);

            Airport arrivalAirport=pickAirport(alreadyTaken);

            br.write("CVF-00:"+arrivalAirport.toString()+"\n");

            alreadyTaken.add(arrivalAirport);

            br.write("CVF-01: "
                    +getInterDateString(intArrivee[0], intArrivee[1])+"\n");
            br.write("CVF-02: "+plageRetour+"\n");



            br.write("\n");
            br.write("CG-00: non implemented\n");
            br.write("CG-01: non implemented\n");

            br.flush();
            br.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
        }

    }
    
    /**
     * Cr�er un fichier de requ�te
     * @param myDir : Path du dossier dans lequel on veut cr�er le fichier
     * @param nameFile : nom du fichier
     * @param nbEtapes : le nombre d'�tapes
     */
    private static void createFile(final Path myDir, final String nameFile, 
            final int nbEtapes){

        createFile(myDir, nameFile, nbEtapes,
                getPlageHoraire(), getPlageHoraire());

    }
    
    
    
    /**
     * Cr�er un fichier de requ�te
     * @param myDir : Path du dossier dans lequel on veut cr�er le fichier
     * @param nameFile : nom du fichier
     */
    private static void createFile(final Path myDir, final String nameFile){

        createFile(myDir, nameFile, (int) (Math.random()*NB_MAX_ETAPES),
                getPlageHoraire(), getPlageHoraire());
        
       

    }


    /**
     * obtenir un intervalle dont le d�but est inf�rieur 
     * au temps pass� en param�tre
     * @param time temps en grains � l'int�rieur duquel 
     * on veut que le d�but de l'intervalle soit
     * @return tableau de deux entiers, dont 
     * le premier repr�sente le d�but de l'intervalle en grains, 
     * et le second la dur�e de l'intervalle
     */
    private static int[] getInterDateTime(final int time){
        return getInterDateTime(0, time);
    }
    
    /**
     * obtenir un intervalle dont le d�but est sup�rieur 
     * au temps pass� en param�tre 1, et inf�rieur � celui en param�tre 2
     * @param timeDebut temps en grains, valeur minimale 
     * que puisse prendre le d�but de l'intervalle
     * @param timeFin temps en grains � l'int�rieur duquel 
     * on veut que le d�but de l'intervalle soit
     * @return  tableau de deux entiers, dont 
     * le premier repr�sente le d�but de l'intervalle en grains, 
     * et le second la dur�e de l'intervalle
     */
    private static int[] getInterDateTime(final int timeDebut,
            final int timeFin){
        int time1 = getRandomTime(timeFin-timeDebut);
        int time2 = getRandomTime(JOURS_D_INTERVALLE*GR_JOUR);
        int[] toReturn ={time1+timeDebut, time2};
        return toReturn;
    }

    /**
     * obtenir sous la forme d'une String l'intervalle correspondant 
     * aux deux temps pass�s en param�tres
     * @param time1 premier temps, correspondant 
     * au point de d�part de l'intervalle
     * @param time2 deuxi�me temps correspondant � la dur�e de l'intervalle
     * @return String repr�sentant l'intervalle
     */
    private static String getInterDateString(final int time1, final int time2){
        String day1=getDay(time1);
        String sh1 = getHour(time1);

        String day2= getDay(time1+time2);
        String sh2 = getHour(time1+time2);

        return "2012/12/"+day1+"-"+sh1+",2012/12/"+day2+"-"+sh2;
    }

    /**
     * obtenir un intervalle dont le d�but est inf�rieur 
     * au temps pass� en param�tre
     * @param time time temps en grains � l'int�rieur duquel 
     * on veut que le d�but de l'intervalle soit
     * @return String repr�sentant l'intervalle
     */
    private static String getInterDateString(final int time){

        int []  times = getInterDateTime(0, time);

        return getInterDateString(times[0], times[1]);
    }

    /**
     * permet d'obtenir une plage horaire al�atoire sur un jour
     * @return String repr�sentant la plage horaire
     */
    private static String getPlageHoraire(){

        int timeDepart1 = getRandomTime(GR_JOUR-PLAGE_MIN);
        String sh1 = getHour(timeDepart1);

        int timeDepart2 = (int) (Math.random()*(GR_JOUR-timeDepart1-PLAGE_MIN))
                +timeDepart1+PLAGE_MIN;
        String sh2=getHour(timeDepart2);

        return sh1+","+sh2;
    }


    /**
     * permet d'obtenir l'heure en partant d'un temps en nombre de grains
     * @param time temps en grains dont on veut conna�tre l'heure 
     * @return String repr�sentant l'heure
     */
    private static  String getHour(final int time){

        int timeDay=time%GR_JOUR;

        int min= (timeDay%(GR_HOUR))*GRANULARITE;

        int hour=(timeDay/GR_HOUR);

        return toStringwith0(hour)+":"+toStringwith0(min);

    }


    /**
     * permet d'obtenir un temps al�atoire, 
     * inf�rieur au temps pass� en param�tre.
     * @param time temps maximal possible
     * @return temps al�atoire
     */
    private static int getRandomTime(final int time){
        return (int) (Math.random()*time);
    }

    /**
     * permet d'obtenir le jour du mois en partant d'un temps en grains.
     * @param time temps
     * @return le jour du mois correspondant au temps
     */
    private static String getDay(final int time){
        return  toStringwith0(time/GR_JOUR +1);
    }

    /**
     * permet de transformer tout entier n inf�rieur � 10 en 0n (7 en 07)
     * @param n entier que l'on veut transformer
     * @return entier transform�
     */
    private static String toStringwith0(final int n){
        if(n<BASE){return "0"+n; }
        return ""+n;
    }

    /**
     * permet d'obtenir un a�roport de la liste des a�roports,
     *  mais n'ayant pas d�j� �t� pris
     * @param airports a�roports d�j� pris
     * @return un a�roport non pris
     */
    private static Airport pickAirport(final List<Airport> airports){

        Airport airport;
        do{
            int numAirport = (int) (Math.random()*Airport.values().length);
            airport = Airport.values()[numAirport];
        }
        while(airports.contains(airport));

        return airport;


    }



    /**
     * main
     * @param args param�tre d'un main
     */
    public static void main(final String[] args) {
        Path myDir = Paths.get("res/requests");

        String nameFiles = "request";


        createFiles(myDir, nameFiles);
        
        createFile(myDir, "Etapes5", 5, PLAGE_DEFAULT, PLAGE_DEFAULT);



    }



}
