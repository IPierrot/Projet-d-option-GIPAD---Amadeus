package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Représente un voyage complet avec une origine, une
 * arrivée et des étapes.
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
     * La date de départ du voyage.
     */
    private Date startDeparture;
    
    /**
     * L'arrivée du voyage.
     */
    private Airport end;
    
    /**
     * La date d'arrivée du voyage.
     */
    private Date endArrival;
    
    /**
     * Les étapes du voyage.
     */
    private List<Airport> stages;
    
    /**
     * Les durées de séjour en heures dans les étapes.
     */
    private List<Double> durations;
    
    /**
     * Les dates d'arrivée et de départ dans les étapes.
     */
    private List<Date[]> stagesDates;
    
    /**
     * Constructeur avec paramètres.
     * @param origin L'origine du voyage.
     * @param departure La date de départ de l'origine.
     * @param destination La destination du voyage.
     * @param arrival La date d'arrivée dans la destination.
     */
    public Trip(final Airport origin, final Date departure,
            final Airport destination, final Date arrival){
        this.start = origin;
        this.startDeparture = departure;
        this.end = destination;
        this.endArrival = arrival;
        this.stages = new ArrayList<Airport>();
        this.stagesDates = new ArrayList<Date[]>();
        this.durations = new ArrayList<Double>();
    }
    
    /**
     * Ajoute une étape au voyage.
     * @param stage L'étape en question.
     * @param dates Les dates d'arrivée et de départ.
     * @param duration La durée de séjour.
     */
    public void addStage(final Airport stage,
            final Date[] dates, final double duration){
        this.stages.add(stage);
        this.stagesDates.add(dates);
        this.durations.add(duration);
    }

    /**
     * @return Les vols qui composent le voyage.
     */
    public List<Flight> getFlights() {
        return flights;
    }
    
    /**
     * Définit les vols du voyage.
     * @param vols Les vols.
     */
    public void setFlights(final List<Flight> vols) {
        this.flights = vols;
    }

    /**
     * @return L'origine du voyage.
     */
    public Airport getStart() {
        return start;
    }

    /**
     * @return La date de départ du voyage.
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
     * @return La date d'arrivée à la destination.
     */
    public Date getEndArrival() {
        return endArrival;
    }

    /**
     * @return Les étapes (dans l'ordre).
     */
    public List<Airport> getStages() {
        return stages;
    }

    /**
     * @return Les durées (en heures) de séjour dans les étapes (dans l'ordre).
     */
    public List<Double> getDurations() {
        return durations;
    }

    /**
     * @return Les dates d'arrivée et de départ dans les étapes.
     */
    public List<Date[]> getStagesDates() {
        return stagesDates;
    }
    
    /**
     * Représente le Trip sous forme de résumé texte.
     * @return La chaine de charactères contenant le résumé du voyage.
     */
    public String toString(){ 
        String retour = "";
        retour += "\n" + "Itinéraire trouvé : "  + "\n" + "\n";
        retour += "-> Vols : "  + "\n";
        for(int i = 0; i < flights.size(); i++){
            retour += "Vol " + i + " : " + flights.get(i) + "\n";
        }
        
        DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat df2 = new SimpleDateFormat("HH:mm");
        
        retour += "\n" + "-> Résumé : "  + "\n";
        
        Date dep = this.getStartDeparture();
        retour += "Départ du voyage depuis " + this.getStart()
                + " le " + df1.format(dep) 
                + " à " + df2.format(dep) + ", " + "\n";
        
        for(int i = 0; i < getStages().size(); i++){
            retour += "Arrivée à " + getStages().get(i)
                    + " le " + df1.format(getStagesDates().get(i)[0]) 
                    + " à " + df2.format(getStagesDates().get(i)[0]) + ","
                    + " séjour pendant " + getDurations().get(i) + "h,"
                    + " départ le " + df1.format(getStagesDates().get(i)[1]) 
                    + " à " 
                    + df2.format(getStagesDates().get(i)[1]) + "," + "\n";
        }
        
        Date arr = this.getEndArrival();
        retour += "Fin du voyage à " + this.getEnd()
                + " le " + df1.format(arr) 
                + " à " + df2.format(arr) + "." + "\n";
        return retour;
    }
}
