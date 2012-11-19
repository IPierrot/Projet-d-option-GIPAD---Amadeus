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
 * Impl�mentation de ComplexTripModel
 * @author Dim
 *
 */
public class SimpleComplexTripModel implements ComplexTripModel{
	
    /**
     * L'option des Variable
     */
    public static final String VARIABLES_OPTION = "cp:enum";
    
	/**
	 * La granularit� de l'�chelle de temps, ici 5 minutes soit 300 000 ms.
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
	 * L'a�roports de d�part.
	 */
	private Airport start;
	
	/**
     * L'a�roports d'arriv�e.
     */
	private Airport end;
	
	/**
	 * Les �tapes du voyage.
	 */
	private List<Airport> stages;
	
	/**
	 * Le d�part au plus t�t.
	 */
	private int t0Earliest;
	
	/**
     * Le d�part au plus tard.
     */
    private int t0Latest;
	
	/**
	 * L'arriv�e au plus t�t.
	 */
	private int tmaxEarliest;
	
	/**
     * L'arriv�e au plus tard.
     */
    private int tmaxLastest;
	
	/**
	 * La liste des intervalles de passage des �tapes.
	 */
	private List<int[]> stagesIntervals;
	
	/**
     * La liste des intervalles dur�e des �tapes.
     */
    private List<int[]> stagesDurations;
	
	/**
	 * La variable correspondant � l'aeroport de d�part.
	 */
	private IntegerVariable startVar;
	
	/**
     * Les variable correspondant de fin du voyage.
     */
    private IntegerVariable endVar;
	
	/**
	 * La variable correspondant � la date de d�part de l'aeroport d'origine.
	 */
	private IntegerVariable startDepVar;
	
	/**
     * La variable correspondant � la date d'arriv�e � l'aeroport final.
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
	 * Les taskVariables des �tapes.
	 */
	private TaskVariable[] stagesTaskVars;
	
	/**
	 * Les variables correspondant aux id des aeroports �tapes.
	 */
	private IntegerVariable[] stagesVars;
	
	/**
     * Les variables correspondant au indexes des vols
     */
    private IntegerVariable[][] stageIndexes;
	
	/**
	 * Les vols susceptibles d'�tre solutions
	 */
	private List<Flight> possibleFlights;
	
	// VARIABLES D'INSTANCE - END
	
	// CONSTRUCTEURS - START
	
	/**
	 * constructeur par d�faut - initialise le CPModel et les listes
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
	    
	    // Ajout des informations (aeroport, intervalle de passage, dur�e).
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
	 * @return True si le mod�le est valide
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
     * @param t La date � demapper.
     * @return La date demapp�e.
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
            
          // Cr�ation de la variable de d�part.
          this.startDepVar = makeIntVar("start", 0, t0Latest-t0Earliest,
                  VARIABLES_OPTION);

          // Cr�ation de la variable d'index de d�part.
          this.startIndex = makeIntVar(
                  "indexDep", 0, this.getPossibleFlights().size()-1,
                  VARIABLES_OPTION);

          System.out.print("....");
          
          // Cr�ation de la variable d'arriv�e.
          this.endArrVar = makeIntVar("end", tmaxEarliest-t0Earliest,
                  tmaxLastest-t0Earliest,
                  VARIABLES_OPTION);
          
          // Cr�ation de la variable d'index de l'arriv�e.
          this.endIndex = makeIntVar(
                  "indexArr", 0, this.getPossibleFlights().size()-1,
                  VARIABLES_OPTION);       
          
          System.out.print("....");
          
          // Cr�ation des variables relatives aux �tapes.
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
              
              // Cr�ation de la variable d'aeroport
              IntegerVariable airport = constant(a.getId());
              this.stagesVars[i] = airport;
              
              // Cr�ation de la task Variable
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
              
              // Cr�ation des variables d'index.
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
