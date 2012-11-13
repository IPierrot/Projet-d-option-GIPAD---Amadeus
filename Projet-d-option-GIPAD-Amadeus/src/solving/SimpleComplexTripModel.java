package solving;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Airport;
import model.Flight;
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
	 * La granularité de l'échelle de temps, ici 5 minutes soit 300 000 ms.
	 */
	private static final int GRANULARITE = 300000;

	/**
	 * Le nombre de miliseconds dans une heure.
	 */
	private static final int NB_MS_IN_ONE_HOUR = 3600000;
	
	// VARIABLES D'INSTANCE - START
	
	/**
	 * Les aéroports de départ et d'arrivée.
	 */
	private Airport start, end;
	
	/**
	 * Les étapes du voyage.
	 */
	private List<Airport> stages;
	
	/**
	 * Le départ au plus tôt et au plus tard.
	 */
	private int t0Earliest, t0Latest;
	
	/**
	 * L'arrivée au plus tôt et au plus tard.
	 */
	private int tmaxEarliest, tmaxLastest;
	
	/**
	 * La liste des intervalles de passage des étapes.
	 */
	private List<int[]> stagesIntervals;
	
	/**
	 * Les variables correspondant à l'aeroport de départ et celui de 
	 * fin du voyage.
	 */
	private IntegerVariable startVar, endVar;
	
	/**
	 * Les variables correspondant à la date de départ de l'aeroport d'origine 
	 * et à la date d'arrivée dans le dernier aeroport (dates mappées).
	 */
	private IntegerVariable startDepVar, endArrVar;
	
	/**
	 * Les taskVariables des étapes.
	 */
	private List<TaskVariable> stagesTaskVars;
	
	/**
	 * Les variables correspondant aux id des aeroports étapes.
	 */
	private List<IntegerVariable> stagesVars;
	
	/**
	 * Les vols susceptibles d'être solutions
	 */
	private List<Flight> possibleFlights;
	
	/**
	 * Les variables correspondant au indexes des vols
	 */
	private List<IntegerVariable> indexVars;
	
	// VARIABLES D'INSTANCE - END
	
	// CONSTRUCTEURS - START
	
	/**
	 * constructeur par défaut - initialise le CPModel et les listes
	 */
	public SimpleComplexTripModel(){
		this.stages = new ArrayList<Airport>();
		this.possibleFlights = new ArrayList<Flight>();
		this.indexVars = new ArrayList<IntegerVariable>();
		this.stagesTaskVars = new ArrayList<TaskVariable>();
		this.stagesVars = new ArrayList<IntegerVariable>();
	}
	
	// CONSTRUCTEURS - END
	
	@Override
	public Airport getStart() {
		return this.start;
	}

	@Override
	public void setStart(final Airport startAirport) {
		this.start = startAirport;
	}

	@Override
	public Airport getEnd() {
		return this.end;
	}

	@Override
	public void setEnd(final Airport endAirport) {
		this.end = endAirport;
	}

	@Override
	public List<Airport> getStages() {
		return this.stages;
	}

	@Override
	public void addStage(final Airport stage, final Date earliestArrival,
			final Date latestDeparture, final int durMin, final int durMax) {
		this.stages.add(stage);
		this.stagesIntervals.add(new int[] {this.mapTime(earliestArrival),
		        this.mapTime(latestDeparture)});
		
		IntegerVariable v1 = makeIntVar("arr", 0, tmaxLastest-t0Earliest);
		IntegerVariable v2 = makeIntVar("dep", 0, tmaxLastest-t0Earliest);
		int i = NB_MS_IN_ONE_HOUR/GRANULARITE;
		IntegerVariable v3 = makeIntVar("dur", durMin*i, durMax*i);
		this.addStageTaskVariable(makeTaskVar("stage", v1, v2, v3));
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
	public void setEarliestDeparture(final Date d) {
		this.t0Earliest = (int) (d.getTime()/GRANULARITE);
	}
	
	@Override
    public void setLatestDeparture(final Date d) {
        this.t0Latest = (int) (d.getTime()/GRANULARITE);
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
    public void setEarliestArrival(final Date d) {
        this.tmaxLastest = (int) (d.getTime()/GRANULARITE);
    }
	
	@Override
	public void setLatestArrival(final Date d) {
		this.tmaxLastest = (int) (d.getTime()/GRANULARITE);
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
	public List<TaskVariable> getStagesTaskVariables() {
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
	public void addStageTaskVariable(final TaskVariable stageVar) {
		this.stagesTaskVars.add(stageVar);
	}

	@Override
	public List<IntegerVariable> getStagesVariables() {
		return this.stagesVars;
	}

	@Override
	public void addStageVariable(final IntegerVariable stageVar) {
		this.stagesVars.add(stageVar);
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
	public List<IntegerVariable> getIndexVariables() {
		return this.indexVars;
	}

	@Override
	public void addIndexVariable(final IntegerVariable indexVar) {
		this.indexVars.add(indexVar);
	}

	@Override
	public boolean isValid() {
		return (getStart() != null 
				&& getEnd() != null
				&& getEarliestDeparture() != null
				&& getLatestArrival() != null
				&& getEarliestDeparture().before(getLatestArrival())
				&& getStages().size() == getStagesTaskVariables().size()
				&& getStages().size() == getStagesVariables().size()
				&& getStages().size() == getStagesIntervals().size()
				&& getStages().size() < getPossibleFlights().size());
	}

	@Override
	public boolean initialize() {
		if(this.isValid()){
			this.endArrVar = makeIntVar(
					"endArrival", 0, tmaxLastest-t0Earliest);
			this.endVar = makeIntVar("end", new int[] {end.getId()});
			this.startDepVar = makeIntVar(
					"startDeparture", 0, tmaxLastest-t0Earliest);
			this.startVar = makeIntVar("start", new int[] {start.getId()});
			
			this.addIndexVariable(makeIntVar(
					"vol 1", 0, getPossibleFlights().size()-1));
			
			for(int i=0; i<getStages().size(); i++){
				this.addStageVariable(makeIntVar("stage " + i,
						Airport.getDomain(getStages())));
				this.addIndexVariable(makeIntVar("index " + i,
						0, this.getPossibleFlights().size()-1));
			}
			return true;
		} else{
			return false;
		}
	}

	@Override
	public int mapTime(final Date d) {
		int l = (int) (d.getTime()/GRANULARITE);
		return  l - this.t0Earliest;
	}
	
	@Override
	public Date unmapTime(final int t){
	    return new Date(t*GRANULARITE);
	}

}
