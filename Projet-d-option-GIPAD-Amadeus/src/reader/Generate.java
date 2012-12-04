package reader;

import java.io.BufferedWriter;
import java.io.File;
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
 * Classe générant des fichiers types de requêtes
 * @author Pierre Chouin
 *
 */
public final class Generate {


    /**
     * Constructeur privé vide
     */
    private Generate(){}


    /**
     * faire un panel de fichier
     * @param myDir dossier où seront les fichiers
     * @param nameFiles nom qu'auront les fichiers, avec un petit index en plus
     */
    private static void createFiles(final Path myDir, final String nameFiles){

        for (int i=0; i<ReaderConstants.NUMBER_OF_FILES; i++){
            //use the above Path instance as an anchor
            String nameFile = nameFiles +i+".txt";
            createFile(myDir, nameFile);
        }

    }
    
    /**
     * faire un panel de fichier
     * @param myDir dossier où seront les fichiers
     * @param nameFiles nom qu'auront les fichiers, avec un petit index en plus
     * @param nbEtapes : le nombre d'étapes
     * @param plageAller : plage horaire du départ
     * @param plageRetour : plage horaire du retour
     */
    private static void createFiles(final Path myDir,
            final String plageAller, final String plageRetour,
            final int nbEtapes, final String nameFiles){

        for (int i=0; i<ReaderConstants.NUMBER_OF_FILES; i++){
            //use the above Path instance as an anchor
            String nameFile = nameFiles +i+".txt";
            createFile(myDir, nameFile, plageAller, plageRetour, nbEtapes);
        }

    }
    
    /**
     * Créer un fichier de requête
     * @param myDir : Path du dossier dans lequel on veut créer le fichier
     * @param nameFile : nom du fichier
     * @param nbEtapes : le nombre d'étapes
     * @param plageAller : plage horaire du départ
     * @param plageRetour : plage horaire du retour
     * @param nbJours : durée du voyage
     */
    public static void createFile(final Path myDir, final String nameFile, 
            final String plageAller, final String plageRetour, 
            final int nbEtapes, final int nbJours){

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

            br.write("CVO-01: "+getInterDateString(ReaderConstants.GR_JOUR)+"\n"); 
            //on part du principe qu'on part toujours le 01 décembre

            br.write("CVO-02: "+plageAller+"\n");

            //int nbEtapes=(int) (Math.random()*NB_MAX_ETAPES);
            
            int[] intArrivee = {nbJours*ReaderConstants.GR_JOUR,
                    getRandomTime(ReaderConstants.JOURS_D_INTERVALLE*ReaderConstants.GR_JOUR)};

            br.write("\n");

           

            for(int j=0; j<nbEtapes; j++){
                br.write("CVE"+j+"\n");

                Airport etapAirport=pickAirport(alreadyTaken);

                br.write("CVE-00: "+etapAirport.toString()+"\n");

                alreadyTaken.add(etapAirport);

                br.write("CVE-01: "+ReaderConstants.INTEGER_VALUE_DEFAULT+"\n");

                int[] intEtape=getInterDateTime(intArrivee[0]);

                br.write("CVE-02: "
                        + getInterDateString(intEtape[0], intEtape[1])+"\n");

                int dmin= getRandomTime(intEtape[1]);
                int dmax = getRandomTime(intEtape[1]-dmin)+dmin;
                br.write("CVE-03: "+dmin/ReaderConstants.GR_HOUR+","+dmax/ReaderConstants.GR_HOUR+"\n");
                br.write("CVE-04: "+getPlageHoraire()+"\n");
                br.write("CVE-05: "+ReaderConstants.INTEGER_VALUE_DEFAULT+"\n");
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
            
            int min=nbJours-ReaderConstants.JOURS_D_INTERVALLE;
            if(min<0) {min=0; }
            int[] intervCG00 = getInterDateTime(min*ReaderConstants.GR_JOUR, 
                    (nbJours+ReaderConstants.JOURS_D_INTERVALLE)*ReaderConstants.GR_JOUR);
            
            br.write("CG-00: "+intervCG00[0]/ReaderConstants.GR_HOUR+","
            +(intervCG00[1]/ReaderConstants.GR_HOUR+intervCG00[0]/ReaderConstants.GR_HOUR)+"\n");
            br.write("CG-01: non implemented\n");

            br.flush();
            br.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
        }

    }


    /**
     * Créer un fichier de requête
     * @param myDir : Path du dossier dans lequel on veut créer le fichier
     * @param nameFile : nom du fichier
     * @param nbEtapes : le nombre d'étapes
     * @param plageAller : plage horaire du départ
     * @param plageRetour : plage horaire du retour
     */
    private static void createFile(final Path myDir, final String nameFile, 
            final String plageAller, final String plageRetour, 
            final int nbEtapes){

        createFile(myDir, nameFile, getPlageHoraire(),
                getPlageHoraire(), nbEtapes, nbEtapes*2);

    }
    
    /**
     * Créer un fichier de requête
     * @param myDir : Path du dossier dans lequel on veut créer le fichier
     * @param nameFile : nom du fichier
     * @param plageAller : plage horaire du départ
     * @param plageRetour : plage horaire du retour
     */
    private static void createFile(final Path myDir, final String nameFile, 
            final String plageAller, final String plageRetour){

        createFile(myDir, nameFile, getPlageHoraire(), 
                getPlageHoraire(), (int) (Math.random()*ReaderConstants.NB_MAX_ETAPES));

    }
    
    
    
    /**
     * Créer un fichier de requête
     * @param myDir : Path du dossier dans lequel on veut créer le fichier
     * @param nameFile : nom du fichier
     */
    private static void createFile(final Path myDir, final String nameFile){

        createFile(myDir, nameFile, getPlageHoraire(), 
                getPlageHoraire());
        
       

    }


    /**
     * obtenir un intervalle dont le début est inférieur 
     * au temps passé en paramètre
     * @param time temps en grains à l'intérieur duquel 
     * on veut que le début de l'intervalle soit
     * @return tableau de deux entiers, dont 
     * le premier représente le début de l'intervalle en grains, 
     * et le second la durée de l'intervalle
     */
    private static int[] getInterDateTime(final int time){
        return getInterDateTime(0, time);
    }
    
    /**
     * obtenir un intervalle dont le début est supérieur 
     * au temps passé en paramètre 1, et inférieur à celui en paramètre 2
     * @param timeDebut temps en grains, valeur minimale 
     * que puisse prendre le début de l'intervalle
     * @param timeFin temps en grains à l'intérieur duquel 
     * on veut que le début de l'intervalle soit
     * @return  tableau de deux entiers, dont 
     * le premier représente le début de l'intervalle en grains, 
     * et le second la durée de l'intervalle
     */
    private static int[] getInterDateTime(final int timeDebut,
            final int timeFin){
        int time1 = getRandomTime(timeFin-timeDebut);
        int time2 = getRandomTime(ReaderConstants.JOURS_D_INTERVALLE*ReaderConstants.GR_JOUR);
        int[] toReturn ={time1+timeDebut, time2};
        return toReturn;
    }

    /**
     * obtenir sous la forme d'une String l'intervalle correspondant 
     * aux deux temps passés en paramètres
     * @param time1 premier temps, correspondant 
     * au point de départ de l'intervalle
     * @param time2 deuxième temps correspondant à la durée de l'intervalle
     * @return String représentant l'intervalle
     */
    private static String getInterDateString(final int time1, final int time2){
        String day1=getDay(time1);
        String sh1 = getHour(time1);

        String day2= getDay(time1+time2);
        String sh2 = getHour(time1+time2);

        return "2012/12/"+day1+"-"+sh1+",2012/12/"+day2+"-"+sh2;
    }

    /**
     * obtenir un intervalle dont le début est inférieur 
     * au temps passé en paramètre
     * @param time time temps en grains à l'intérieur duquel 
     * on veut que le début de l'intervalle soit
     * @return String représentant l'intervalle
     */
    private static String getInterDateString(final int time){

        int []  times = getInterDateTime(0, time);

        return getInterDateString(times[0], times[1]);
    }

    /**
     * permet d'obtenir une plage horaire aléatoire sur un jour
     * @return String représentant la plage horaire
     */
    private static String getPlageHoraire(){

        int timeDepart1 = getRandomTime(ReaderConstants.GR_JOUR-ReaderConstants.PLAGE_MIN);
        String sh1 = getHour(timeDepart1);

        int timeDepart2 = (int) (Math.random()*(ReaderConstants.GR_JOUR-timeDepart1-ReaderConstants.PLAGE_MIN))
                +timeDepart1+ReaderConstants.PLAGE_MIN;
        String sh2=getHour(timeDepart2);

        return sh1+","+sh2;
    }


    /**
     * permet d'obtenir l'heure en partant d'un temps en nombre de grains
     * @param time temps en grains dont on veut connaître l'heure 
     * @return String représentant l'heure
     */
    private static  String getHour(final int time){

        int timeDay=time%ReaderConstants.GR_JOUR;

        int min= (timeDay%(ReaderConstants.GR_HOUR))*ReaderConstants.GRANULARITE;

        int hour=(timeDay/ReaderConstants.GR_HOUR);

        return toStringwith0(hour)+":"+toStringwith0(min);

    }


    /**
     * permet d'obtenir un temps aléatoire, 
     * inférieur au temps passé en paramètre.
     * @param time temps maximal possible
     * @return temps aléatoire
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
        return  toStringwith0(time/ReaderConstants.GR_JOUR +1);
    }

    /**
     * permet de transformer tout entier n inférieur à 10 en 0n (7 en 07)
     * @param n entier que l'on veut transformer
     * @return entier transformé
     */
    private static String toStringwith0(final int n){
        if(n<ReaderConstants.BASE){return "0"+n; }
        return ""+n;
    }

    /**
     * permet d'obtenir un aéroport de la liste des aéroports,
     *  mais n'ayant pas déjà été pris
     * @param airports aéroports déjà pris
     * @return un aéroport non pris
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
     * @param args paramètre d'un main
     */
    public static void main(final String[] args) {
        Path myDir = Paths.get("res/requests");

//        createFiles(myDir, 1, PLAGE_DEFAULT, PLAGE_DEFAULT, nameFiles);

//        createFiles(myDir, nameFiles);
//        
//        createFile(myDir, "Etapes5", PLAGE_DEFAULT, PLAGE_DEFAULT, 5);
        
        for(int i = 0; i < 100; i++){
            createFile(myDir, "request " + i + ".txt");
        }    



    }



}
