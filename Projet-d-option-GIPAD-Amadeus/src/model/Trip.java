package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repr�sente un voyage complet avec une origine, une
 * arriv�e et des �tapes.
 * @author Pierrot Papi Dim
 *
 */
public class Trip {

    /**
     * La liste des vols du voyage.
     */
    private List<Flight> flights;
    
    /**
     * L'origine du voyage.
     */
    private Airport start;
    
    /**
     * La date de d�part du voyage.
     */
    private Date startDeparture;
    
    /**
     * L'arriv�e du voyage.
     */
    private Airport end;
    
    /**
     * La date d'arriv�e du voyage.
     */
    private Date endArrival;
    
    /**
     * La dur�e total du voyage.
     */
    private int totalDuration;
    
    /**
     * Les �tapes du voyage.
     */
    private List<Airport> stages;
    
    /**
     * Les dur�es de s�jour en heures dans les �tapes.
     */
    private List<Integer> durations;
    
    /**
     * Les dates d'arriv�e et de d�part dans les �tapes.
     */
    private List<Date[]> stagesDates;
    
    /**
     * Constructeur avec param�tres.
     * @param origin L'origine du voyage.
     * @param departure La date de d�part de l'origine.
     * @param destination La destination du voyage.
     * @param arrival La date d'arriv�e dans la destination. 
     * @param totalDur La dur�e totale.
     */
    public Trip(final Airport origin, final Date departure,
            final Airport destination, final Date arrival,
            final int totalDur) {
        this.start = origin;
        this.startDeparture = departure;
        this.end = destination;
        this.endArrival = arrival;
        this.totalDuration = totalDur;
        this.stages = new ArrayList<Airport>();
        this.stagesDates = new ArrayList<Date[]>();
        this.durations = new ArrayList<Integer>();
    }
    
    /**
     * Ajoute une �tape au voyage.
     * @param stage L'�tape en question.
     * @param dates Les dates d'arriv�e et de d�part.
     * @param duration La dur�e de s�jour.
     */
    public void addStage(final Airport stage,
            final Date[] dates, final int duration){
        if(this.stages.isEmpty()){
            this.stages.add(stage);
            this.stagesDates.add(dates);
            this.durations.add(duration);
        } else{
            int i = 0;
            while (i < this.stages.size() 
                 && dates[0].after(
                        this.stagesDates.get(i)[0])) {
                i++;
            }
            this.stages.add(i, stage);
            this.stagesDates.add(i, dates);
            this.durations.add(i, duration);
        }
    }

    /**
     * @return Les vols qui composent le voyage.
     */
    public List<Flight> getFlights() {
        return flights;
    }
    
    /**
     * D�finit les vols du voyage.
     * @param vols Les vols.
     */
    public void setFlights(final List<Flight> vols) {
        this.flights = sort(vols);
    }

    /**
     * @return L'origine du voyage.
     */
    public Airport getStart() {
        return start;
    }

    /**
     * @return La date de d�part du voyage.
     */
    public Date getStartDeparture() {
        return startDeparture;
    }

    /**
     * @return La destination du voyage.
     */
    public Airport getEnd() {
        return end;
    }

    /**
     * @return La date d'arriv�e � la destination.
     */
    public Date getEndArrival() {
        return endArrival;
    }

    /**
     * @return Les �tapes (dans l'ordre).
     */
    public List<Airport> getStages() {
        return stages;
    }

    /**
     * @return Les dur�es (en heures) de s�jour dans les �tapes (dans l'ordre).
     */
    public List<Integer> getDurations() {
        return durations;
    }
    
    /**
     * @param i L'index de l'�tape.
     * @return La dur�e formatt�e sous forme de String de s�jour dans
     * l'�tape i.
     */
    public String getFormatedDuration(final long l) {
        double hours = (double) ((double) l/1000/60/60);
        double minutes = hours - (int) hours; 
        int h = (int) hours;
        int mn = (int) ((minutes)*60);
        mn = (mn != 0) ? mn+1 : mn;
//        DateFormat sf1 = new SimpleDateFormat("HH");
//        DateFormat sf2 = new SimpleDateFormat("mm");
//        DateFormat sf3 = new SimpleDateFormat("D");
//        String jours = (nbJours > 0) ? nbJours + " jours, " : "";
//        return jours + sf1.format(d) 
//                + " heures et " + sf2.format(d) + " minutes";
        return h + "h et " + mn + " minutes";
    }
    
    /**
     * @return Les dates d'arriv�e et de d�part dans les �tapes.
     */
    public List<Date[]> getStagesDates() {
        return stagesDates;
    }
    
    /**
     * Ordonne les indexes d'une liste de vols (de facon � ce que les vols se 
     * suivent dans le temps.
     * @param flights La liste � ordonner
     * @return La liste ordonn�e et sans doublons
     */
    private static List<Flight> sort(final List<Flight> flights){
        List<Flight> retour = new ArrayList<Flight>();
        for(int j = 0; j<flights.size(); j++){
            Flight f = flights.get(j);
            if(retour.isEmpty()){
                retour.add(f);
            } else{
                int i = 0;
                while (i < retour.size() 
                     && f.getDeparture().after(
                            retour.get(i).getDeparture())) {
                    i++;
                }
                retour.add(i, f);
            }
        }
        return retour;
    }
    
    /**
     * Repr�sente le Trip sous forme de r�sum� texte.
     * @return La chaine de charact�res contenant le r�sum� du voyage.
     */
    public String toString(){ 
        String retour = "";
        retour += "\n" + "Itin�raire trouv� : "  + "\n" + "\n";
        retour += "-> Vols : "  + "\n";
        for(int i = 0; i < flights.size(); i++){
            retour += "Vol " + i + " : " + flights.get(i) + "\n";
        }
        
        DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat df2 = new SimpleDateFormat("HH:mm");
        
        retour += "\n" + "-> R�sum� : "  + "\n";
        
        Date dep = this.getStartDeparture();
        retour += "D�part du voyage depuis " + this.getStart()
                + " le " + df1.format(dep) 
                + " � " + df2.format(dep) + ", " + "\n";
        
        for(int i = 0; i < getStages().size(); i++){
            retour += "Arriv�e � " + getStages().get(i)
                    + " le " + df1.format(getStagesDates().get(i)[0]) 
                    + " � " + df2.format(getStagesDates().get(i)[0]) + ","
                    + " s�jour pendant " + getFormatedDuration(
                            (long) (getDurations().get(i))) + ","
                    + " d�part le " + df1.format(getStagesDates().get(i)[1]) 
                    + " � " 
                    + df2.format(getStagesDates().get(i)[1]) + "," + "\n";
        }
        
        Date arr = this.getEndArrival();
        retour += "Fin du voyage � " + this.getEnd()
                + " le " + df1.format(arr) 
                + " � " + df2.format(arr) + "." + "\n";
        retour += "\n" + "Dur�e totale du voyage : " 
                + getFormatedDuration(totalDuration) + "\n";
        return retour;
    }
}
