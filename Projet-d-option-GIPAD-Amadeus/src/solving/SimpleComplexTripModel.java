package solving;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Airport;
import model.Flight;
import choco.cp.model.CPModel;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import static choco.Choco.*;

/**
 * Implémentation de ComplexTripModel
 * @author Dim
 *
 */
public class SimpleComplexTripModel implements ComplexTripModel{
	
    /**
     * L'option des Variable
     */
    public static final String VARIABLES_OPTION = "cp:enum";
    
	/**
	 * La granularité de l'échelle de temps, ici 5 minutes soit 300 000 ms.
	 */
	private static final int GRANULARITE = 300000;

	/**
	 * Le nombre de miliseconds dans une heure.
	 */
	private static final int NB_MS_IN_ONE_HOUR = 3600000;
	
	// VARIABLES D'INSTANCE - START
	
	/**
	 * Le CPModel.
	 */
	private CPModel cpmodel;
	
	/**
	 * L'aéroports de départ.
	 */
	private Airport start;
	
	/**
     * L'aéroports d'arrivée.
     */
	private Airport end;
	
	/**
	 * Les étapes du voyage.
	 */
	private List<Airport> stages;
	
	/**
	 * Le départ au plus tôt.
	 */
	private int t0Earliest;
	
	/**
     * Le départ au plus tard.
     */
    private int t0Latest;
	
	/**
	 * L'arrivée au plus tôt.
	 */
	private int tmaxEarliest;
	
	/**
     * L'arrivée au plus tard.
     */
    private int tmaxLastest;
	
	/**
	 * La liste des intervalles de passage des étapes.
	 */
	private List<int[]> stagesIntervals;
	
	/**
     * La liste des intervalles durée des étapes.
     */
    private List<int[]> stagesDurations;
	
	/**
	 * La variable correspondant à l'aeroport de départ.
	 */
	private IntegerVariable startVar;
	
	/**
     * Les variable correspondant de fin du voyage.
     */
    private IntegerVariable endVar;
	
	/**
	 * La variable correspondant à la date de départ de l'aeroport d'origine.
	 */
	private IntegerVariable startDepVar;
	
	/**
     * La variable correspondant à la date d'arrivée à l'aeroport final.
     */
    private IntegerVariable endArrVar;
	
	/**
	 * L'index du vol initial du voyage.
	 */
	private IntegerVariable startIndex;
	
	/**
     * L'indexes du vol final du voyage.
     */
    private IntegerVariable endIndex;
	
	/**
	 * Les taskVariables des étapes.
	 */
	private TaskVariable[] stagesTaskVars;
	
	/**
	 * Les variables correspondant aux id des aeroports étapes.
	 */
	private IntegerVariable[] stagesVars;
	
	/**
     * Les variables correspondant au indexes des vols
     */
    private IntegerVariable[][] stageIndexes;
	
	/**
	 * Les vols susceptibles d'être solutions
	 */
	private List<Flight> possibleFlights;
	
	// VARIABLES D'INSTANCE - END
	
	// CONSTRUCTEURS - START
	
	/**
	 * constructeur par défaut - initialise le CPModel et les listes
	 */
	public SimpleComplexTripModel(){
	    this.cpmodel = new CPModel();
		this.stages = new ArrayList<Airport>();
		this.possibleFlights = new ArrayList<Flight>();
		this.stagesIntervals = new ArrayList<int[]>();
		this.stagesDurations = new ArrayList<int[]>();
	}
	
	// CONSTRUCTEURS - END
	
	@Override
	public Airport getStartAirport() {
		return this.start;
	}

	@Override
	public void setStart(final Airport startAirport, final Date earliest, 
	        final Date latest) {
	    
		this.start = startAirport;
		this.startVar = constant(startAirport.getId());		
		this.t0Earliest = (int) (earliest.getTime()/GRANULARITE);
		this.t0Latest = (int) (latest.getTime()/GRANULARITE);
		
	}

	@Override
	public Airport getEndAirport() {
		return this.end;
	}

	@Override
	public void setEnd(final Airport endAirport, final Date earliest, 
            final Date latest) {
        
        this.end = endAirport;
        this.endVar = constant(endAirport.getId());
        this.tmaxEarliest = (int) (earliest.getTime()/GRANULARITE);
        this.tmaxLastest = (int) (latest.getTime()/GRANULARITE);
        
    }

	@Override
	public List<Airport> getStages() {
		return this.stages;
	}

	@Override
	public void addStage(final Airport stage, final Date earliestArrival,
			final Date latestDeparture, final int durMin, final int durMax) {
	    
	    // Ajout des informations (aeroport, intervalle de passage, durée).
		this.stages.add(stage);
		this.stagesIntervals.add(new int[] 
		        {(int) (earliestArrival.getTime()/GRANULARITE),
		            (int) (latestDeparture.getTime()/GRANULARITE)});
		
		int i = NB_MS_IN_ONE_HOUR/GRANULARITE;
		
		this.stagesDurations.add(new int[] {durMin*i, durMax*i}); 
	}

	@Override
	public Date getEarliestDeparture() {
		return this.unmapTime(t0Earliest);
	}

	@Override
	public Date getLatestDeparture() {
		return this.unmapTime(t0Latest);
	}

	@Override
	public Date getEarliestArrival() {
		return this.unmapTime(tmaxEarliest);
	}
	
	@Override
	public Date getLatestArrival() {
		return this.unmapTime(tmaxLastest);
	}

	@Override
	public IntegerVariable getStartVariable() {
		return this.startVar;
	}

	@Override
	public IntegerVariable getStartDeparture() {
		return this.startDepVar;
	}

	@Override
	public TaskVariable[] getStagesTaskVariables() {
		return this.stagesTaskVars;
	}
	
	@Override
	public List<Date[]> getStagesIntervals(){
	    List<Date[]> retour = new ArrayList<Date[]>();
	    for(int[] t : this.stagesIntervals){
	        retour.add(new Date[] {this.unmapTime(t[0]), this.unmapTime(t[1])});
	    }
		return retour;
	}
	
	@Override
    public List<int[]> getStagesDurations(){
        List<int[]> retour = new ArrayList<int[]>();
        int i = GRANULARITE/NB_MS_IN_ONE_HOUR;
        for(int[] t : this.stagesDurations){
            retour.add(new int[] {t[0]*i, t[1]*i});
        }
        return retour;
    }

	@Override
	public IntegerVariable[] getStagesVariables() {
		return this.stagesVars;
	}

	@Override
	public IntegerVariable getEndVariable() {
		return this.endVar;
	}

	@Override
	public IntegerVariable getEndArrival() {
		return this.endArrVar;
	}

	@Override
	public List<Flight> getPossibleFlights() {
		return this.possibleFlights;
	}

	@Override
	public void addPossibleFlight(final Flight flight) {
		this.possibleFlights.add(flight);
	}

	@Override
	public IntegerVariable[][] getStagesIndexes() {
		return this.stageIndexes;
	}

	/**
	 * @return True si le modèle est valide
	 */
	private boolean isValid() {
	    return (getStartAirport() != null 
				&& getEndAirport() != null
				&& getEarliestDeparture() != null
				&& getLatestArrival() != null
				&& getEarliestDeparture().before(getLatestArrival())
				&& getStages().size() == getStagesIntervals().size()
				&& getStages().size() < getPossibleFlights().size());
	}
	
	/**
     * @param t La date à demapper.
     * @return La date demappée.
     */
	private Date unmapTime(final int t){
	    long l = (long) t*(long) GRANULARITE;
	    return new Date(l);
	}

    @Override
    public CPModel getCPModel() {
        return this.cpmodel;
    }

    @Override
    public IntegerVariable getStartIndex() {
        return this.startIndex;
    }

    @Override
    public IntegerVariable getEndIndex() {
        return this.endIndex;
    }

    @Override
    public boolean build() {
        boolean retour;
        
        if(this.isValid()){
            retour = true;
            
          // Création de la variable de départ.
          this.startDepVar = makeIntVar("start", 0, t0Latest-t0Earliest,
                  VARIABLES_OPTION);

          // Création de la variable d'index de départ.
          this.startIndex = makeIntVar(
                  "indexDep", 0, this.getPossibleFlights().size()-1,
                  VARIABLES_OPTION);

          System.out.print("....");
          
          // Création de la variable d'arrivée.
          this.endArrVar = makeIntVar("end", tmaxEarliest-t0Earliest,
                  tmaxLastest-t0Earliest,
                  VARIABLES_OPTION);
          
          // Création de la variable d'index de l'arrivée.
          this.endIndex = makeIntVar(
                  "indexArr", 0, this.getPossibleFlights().size()-1,
                  VARIABLES_OPTION);       
          
          System.out.print("....");
          
          // Création des variables relatives aux étapes.
          int n = this.getStages().size();
          this.stagesTaskVars = new TaskVariable[n];
          this.stagesVars = new IntegerVariable[n];
          this.stageIndexes = new IntegerVariable[n][2];
          
          System.out.print("....");
          
          for(int i = 0; i < n; i++){
              Airport a = this.getStages().get(i);
              int tarr = this.stagesIntervals.get(i)[0];
              int tdep = this.stagesIntervals.get(i)[1];
              int durmin = this.stagesDurations.get(i)[0];
              int durmax = this.stagesDurations.get(i)[1];
              
              // Création de la variable d'aeroport
              IntegerVariable airport = constant(a.getId());
              this.stagesVars[i] = airport;
              
              // Création de la task Variable
              IntegerVariable st = makeIntVar(
                    "start " + i + "(" + a.name()+")", 
                    tarr-t0Earliest, tdep-t0Earliest, VARIABLES_OPTION);
              
              IntegerVariable en = makeIntVar(
                      "end " + i + "(" + a.name()+")", 
                      tarr-t0Earliest, tdep-t0Earliest, VARIABLES_OPTION);
              
              IntegerVariable dur = makeIntVar(
                      "duration " + i + "(" + a.name()+")",
                      durmin, durmax, VARIABLES_OPTION);
              
              TaskVariable task = makeTaskVar("stage " + i + "(" + a.name()+")",
                      st, en, dur, VARIABLES_OPTION);
              
              this.stagesTaskVars[i] = task;
              
              // Création des variables d'index.
              IntegerVariable arr = makeIntVar(
                      "indexArr " + i + " - " + a.name(),
                      0, this.getPossibleFlights().size()-1, VARIABLES_OPTION);
              
              IntegerVariable dep = makeIntVar(
                      "indexDep " + i + " - " + a.name(),
                      0, this.getPossibleFlights().size()-1, VARIABLES_OPTION);
              
              this.stageIndexes[i] = new IntegerVariable[] {arr, dep};
              
              System.out.print("....");
          }

          this.addVariablesToCPModel();
          System.out.print("....");  
          
        } else{
            retour = false;
        }
        return retour;
    }
    
    /**
     * Ajoute les variables dans le CPModel.
     */
    private void addVariablesToCPModel(){
        cpmodel.addVariables(startVar, startIndex, startDepVar,
                endVar, endIndex, endArrVar);
        cpmodel.addVariables(stagesVars);
        cpmodel.addVariables(stagesTaskVars);
        for(IntegerVariable[] vars : this.stageIndexes){
            cpmodel.addVariables(vars);
        }
    }

    @Override
    public int mapTime(final Date d) {
        return (int) (d.getTime()/GRANULARITE-t0Earliest);
    }

    @Override
    public double unmapDuration(final int d) {
        double duree = (double)(d*GRANULARITE)/NB_MS_IN_ONE_HOUR;
        int i = (int) (duree*100);
        return i/100d;
    }
}
