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
     * Les �tapes du voyage.
     */
    private List<Airport> stages;
    
    /**
     * Les dur�es de s�jour en heures dans les �tapes.
     */
    private List<Double> durations;
    
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
     * Ajoute une �tape au voyage.
     * @param stage L'�tape en question.
     * @param dates Les dates d'arriv�e et de d�part.
     * @param duration La dur�e de s�jour.
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
     * D�finit les vols du voyage.
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
    public List<Double> getDurations() {
        return durations;
    }

    /**
     * @return Les dates d'arriv�e et de d�part dans les �tapes.
     */
    public List<Date[]> getStagesDates() {
        return stagesDates;
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
                    + " s�jour pendant " + getDurations().get(i) + "h,"
                    + " d�part le " + df1.format(getStagesDates().get(i)[1]) 
                    + " � " 
                    + df2.format(getStagesDates().get(i)[1]) + "," + "\n";
        }
        
        Date arr = this.getEndArrival();
        retour += "Fin du voyage � " + this.getEnd()
                + " le " + df1.format(arr) 
                + " � " + df2.format(arr) + "." + "\n";
        return retour;
    }
}
