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

public class Generate {

    public final static int NUMBER_OF_FILES = 100;

    public final static int NB_HOURS = 24;

    public final static int NB_MIN = 60;

    public final static int GRANULARITE = 5;

    public final static int JOURS_D_INTERVALLE = 5;

    public final static int JOURS_VOYAGE=20;

    public final static int GR_HOUR = (NB_MIN/GRANULARITE);

    public final static int GR_JOUR = NB_HOURS*(NB_MIN/GRANULARITE);
    
    public final static int NB_MAX_ETAPES=15;


    private static void createFile(Path myDir, int i){
        
        Path createFile = myDir.resolve("request"+i+".txt");

        List<Airport> alreadyTaken= new ArrayList<Airport>();


        try {

            OpenOption[] options = {StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

            BufferedWriter br = Files.newBufferedWriter(createFile, Charset.forName("UTF-8"), options);



            Airport departAirport=pickAirport(alreadyTaken);

            br.write("CVO-00: "+departAirport.toString()+" \n");

            alreadyTaken.add(departAirport);

            br.write("CVO-01: "+getIntervalleDateString(GR_JOUR)+"\n"); //on part du principe qu'on part toujours le 01 décembre, pour l'instant

            br.write("CVO-02: "+getPlageHoraire()+"\n");

            int[] intervalleArrivee = getIntervalleDateTime(JOURS_VOYAGE*GR_JOUR);

            br.write("\n");

            int nbEtapes = (int) (Math.random()*NB_MAX_ETAPES);
            
            for(int j=0;j<nbEtapes;j++){
                br.write("CVE"+j+"\n");
                
                Airport etapAirport=pickAirport(alreadyTaken);
                
                br.write("CVE-00: "+etapAirport.toString()+"\n");
                
                alreadyTaken.add(departAirport);
                
                br.write("CVE-01: non implemented \n");
                
                int[] intEtape=getIntervalleDateTime(intervalleArrivee[0]);
                
                br.write("CVE-02: "+getIntervalleDateString(intEtape[0],intEtape[1])+"\n");
                
                int dmin= getRandomTime(intEtape[1]);
                int dmax = getRandomTime(intEtape[1]-dmin)+dmin;
                br.write("CVE-03: "+dmin/GR_HOUR+","+dmax/GR_HOUR+"\n");
                br.write("CVE-04: non implemented\n");
                br.write("CVE-05: non implemented\n");
                br.write("\n");
            }

           



            Airport arrivalAirport=pickAirport(alreadyTaken);

            br.write("CVF-00:"+arrivalAirport.toString()+"\n");
            
            alreadyTaken.add(arrivalAirport);

            br.write("CVF-01: "+getIntervalleDateString(intervalleArrivee[0],intervalleArrivee[1])+"\n");
            br.write("CVF-02: "+getPlageHoraire()+"\n");



            br.write("\n");
            br.write("CG-00: non implemented\n");
            br.write("CG-01: non implemented\n");

            br.flush();
            br.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
        }
        
    }
    
    
    private static int[] getIntervalleDateTime(int time){
        int time1 = getRandomTime(time);
        int time2 = getRandomTime(JOURS_D_INTERVALLE*GR_JOUR);
        int[] toReturn ={time1,time2};
        return toReturn;
    }
    
    private static String getIntervalleDateString(int time1, int time2){
        String day1=getDay(time1);
        String sh1 = getHour(time1);

        String day2= getDay(time1+time2);
        String sh2 = getHour(time1+time2);
        
        return "2012/12/"+day1+"-"+sh1+",2012/12/"+day2+"-"+sh2;
    }
    
    private static String getIntervalleDateString(int time){
        
        int []  times = getIntervalleDateTime(time);
        
        return getIntervalleDateString(times[0],times[1]);
    }
    
    private static String getPlageHoraire(){
        
        int timeDepart1 = getRandomTime(GR_JOUR);
        String sh1 = getHour(timeDepart1);

        int timeDepart2 = (int)(Math.random()*(GR_JOUR-timeDepart1))+timeDepart1;
        String sh2=getHour(timeDepart2);
        
        return sh1+","+sh2;
    }
    
    
    private static  String getHour(int timeDepart1){

        timeDepart1=timeDepart1%GR_JOUR;
        
        int minDepart1= (timeDepart1%(GR_HOUR))*GRANULARITE;

        int hourDepart1=(timeDepart1/GR_HOUR);

        return toStringwith0(hourDepart1)+":"+toStringwith0(minDepart1);

    }


    
    private static int getRandomTime(int time){
        return (int) (Math.random()*time);
    }

    private static String getDay(int time){
        return  toStringwith0(time/GR_JOUR +1);
    }
    
    private static String toStringwith0(int n){
        if(n<10) return "0"+n;
        return ""+n;
    }

    private static Airport pickAirport(List<Airport> airports){

        Airport airport;
        do{
            int numAirport = (int) (Math.random()*Airport.values().length);
            airport = Airport.values()[numAirport];
        }
        while(airports.contains(airport));

        return airport;


    }



    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Path myDir = Paths.get("res/requests");

        for (int i=0; i<NUMBER_OF_FILES; i++){
            //use the above Path instance as an anchor
            
            createFile(myDir,i);
            
           
        }
    }


}
