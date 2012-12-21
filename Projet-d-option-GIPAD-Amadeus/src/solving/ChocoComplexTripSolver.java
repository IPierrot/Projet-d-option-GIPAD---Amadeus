package solving;

import static choco.Choco.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import solving.constraints.MustBeBetweenManager;
import static solving.SolveConstants.*;

import model.Airport;
import model.Flight;
import model.Trip;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

/**
 * 
 * @author Dim
 *
 */
public class ChocoComplexTripSolver implements IComplexTripSolver{
    
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
     * La liste correspondant au information sur la facultativité des étapes.
     */
    private List<Boolean> mandatories;
    
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
    
    /**
     * l'intervalle total de la durée du voyage en heures
     */
    private int[] totalInterval;
    
    /**
     * l'ensemble du voyage défini comme une tâche
     */
    private TaskVariable totalTrip;

    /**
     * Les intervalles de presence dans l'etape pour chaque etape.
     */
    private List<int[]> stagesHours;
    
    /**
     * Les nombres de fois ou stagesHours doivent etre verifies.
     */
    private List<Integer> nbTimes;
    
    /**
     * Le solveur Choco.
     */
    private CPSolver solver;
    
    /**
     * Les dates mappées des départs et l'index du vol.
     */
    private List<int[]> departs;

    /**
     * Les dates mappées des arrivées et l'index du vol.
     */
    private List<int[]> arrivees;
    
    /**
     * True si la méthode read a été appellée
     * sur le solveur depuis son instanciation
     * ou son dernier reset.
     */
    private boolean readyToSolve;
    
    /**
     * True si on a déjà trouvé une solution.
     */
    private boolean solutionFound;
    
    /**
     * constructeur par défaut - initialise le CPModel et les listes
     */
    public ChocoComplexTripSolver(){
        this.cpmodel = new CPModel();
        this.stages = new ArrayList<Airport>();
        this.possibleFlights = new ArrayList<Flight>();
        this.stagesIntervals = new ArrayList<int[]>();
        this.stagesDurations = new ArrayList<int[]>();
        this.stagesHours = new ArrayList<int[]>();
        this.nbTimes = new ArrayList<Integer>();
        this.mandatories = new ArrayList<Boolean>();
        this.solver = new CPSolver();
        this.departs = new ArrayList<int[]>();
        this.arrivees= new ArrayList<int[]>();
        this.readyToSolve = false;
        this.solutionFound = false;
    }

    @Override
    public void addStage(final Airport stage, final Date earliestArrival,
            final Date latestDeparture, final int durMin, final int durMax, 
            final int[] h, final int nbFois, final boolean mandatory) {
        
        // Ajout des informations (aeroport, intervalle de passage, durée).
        this.stages.add(stage);
        this.stagesIntervals.add(new int[] 
                {(int) (earliestArrival.getTime()/SolveConstants.GRANULARITE),
                (int) (latestDeparture.getTime()/SolveConstants.GRANULARITE)});
        this.mandatories.add(mandatory);
        
        int i = SolveConstants.NB_MS_IN_ONE_HOUR/SolveConstants.GRANULARITE;
        
        this.stagesDurations.add(
                new int[] {durMin*i, durMax*i});
        
        //Ajout de CVE04 et CVE05
        int h1 = h[0]/SolveConstants.GRANULARITE;
        int h2 = h[1]/SolveConstants.GRANULARITE;
        this.stagesHours.add(new int[] {h1, h2});
        this.nbTimes.add(nbFois);
    }
   
    @Override
    public boolean build() {
        boolean retour;
        
        if(this.isValid()){
            retour = true;
            
          // Création de la variable de départ.
          this.startDepVar = makeIntVar("start", 0, t0Latest-t0Earliest,
                  SolveConstants.VARIABLES_OPTION);

          // Création de la variable d'index de départ.
          this.startIndex = makeIntVar(
                  "indexDep", 0, this.getPossibleFlights().size()-1,
                  SolveConstants.VARIABLES_OPTION);

          System.out.print("....");
          
          // Création de la variable d'arrivée.
          this.endArrVar = makeIntVar("end", tmaxEarliest-t0Earliest,
                  tmaxLastest-t0Earliest,
                  SolveConstants.VARIABLES_OPTION);
          
          // Création de la variable d'index de l'arrivée.
          this.endIndex = makeIntVar(
                  "indexArr", 0, this.getPossibleFlights().size()-1,
                  SolveConstants.VARIABLES_OPTION);       
          
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
              int[] aDomain;
              if (this.mandatories.get(i)) {
                  aDomain = new int[] {a.getId()};
              } else {
                  aDomain = new int[] {-1, a.getId()};
              }
              IntegerVariable airport = makeIntVar(
                      "airport", aDomain, SolveConstants.VARIABLES_OPTION);
              this.stagesVars[i] = airport;
              
              // Création de la task Variable
              int[] domainDates = new int[this.mandatories.get(i) 
                                     ? tdep-tarr+1 : tdep-tarr+2];
              int[] domainDuration = new int[this.mandatories.get(i) 
                                          ? durmax-durmin+1 : durmax-durmin+2];
              if (!this.mandatories.get(i)) {
                  domainDates[0] = -1;
                  for (int j = tarr-t0Earliest; j <= tdep-t0Earliest; j++) {
                      domainDates[j-(tarr-t0Earliest)+1] = j;
                  }
                  domainDuration[0] = 0;
                  for (int j = durmin; j <= durmax; j++) {
                      domainDuration[j-durmin+1] = j;
                  }
              } else {
                  for (int j = tarr-t0Earliest; j <= tdep-t0Earliest; j++) {
                      domainDates[j-(tarr-t0Earliest)] = j;
                  }
                  for (int j = durmin; j <= durmax; j++) {
                      domainDuration[j-durmin] = j;
                  }
              }
              IntegerVariable st = makeIntVar(
                    "start " + i + "(" + a.name()+")", 
                    domainDates, SolveConstants.VARIABLES_OPTION);
              
              IntegerVariable en = makeIntVar(
                      "end " + i + "(" + a.name()+")", 
                      domainDates, SolveConstants.VARIABLES_OPTION);
              
              IntegerVariable dur = makeIntVar(
                      "duration " + i + "(" + a.name()+")",
                      domainDuration, SolveConstants.VARIABLES_OPTION);

              TaskVariable task = makeTaskVar("stage " + i + "(" + a.name()+")",
                      st, en, dur, SolveConstants.VARIABLES_OPTION);
              
              this.stagesTaskVars[i] = task;
              
              // Création des variables d'index.
              int lowB = (this.mandatories.get(i)) ? 0 : -1;
              IntegerVariable arr = makeIntVar(
                      "indexArr " + i + " - " + a.name(),
                      lowB, this.getPossibleFlights().size()-1,
                      SolveConstants.VARIABLES_OPTION);
              IntegerVariable dep = makeIntVar(
                      "indexDep " + i + " - " + a.name(),
                      lowB, this.getPossibleFlights().size()-1,
                      SolveConstants.VARIABLES_OPTION);
              
              this.stageIndexes[i] = new IntegerVariable[] {arr, dep};
              
              totalTrip = makeTaskVar("totalTrip", startDepVar, endArrVar,
                      makeIntVar("totalDur", 0,
                              endArrVar.getUppB()-startDepVar.getLowB()));
              cpmodel.addVariable(totalTrip);
              
              System.out.print("....");
          }

          this.addVariablesToCPModel();
          System.out.print("....");  
          
        } else{
            retour = false;
        }
        return retour;
    }

    @Override
    public boolean constraint() {
        boolean retour = true;
        long t = System.currentTimeMillis();
        System.out.println("\n" + "\n"  + "APPLICATION DES CONTRAINTES");
        if(!isValid()){
            retour = false;
        }
        if (retour) {
            String feasOption = "";
            // Initialisation des données
            this.departs.add(new int[] {-1, -1, -1});
            this.arrivees.add(new int[] {-1, -1, -1});
            for(int i=0; i<possibleFlights.size(); i++){
                Flight f = possibleFlights.get(i);
                this.departs.add(
                        new int[] {i, f.getOrigin().getId(),
                                    mapTime(f.getDeparture())});
                this.arrivees.add(
                        new int[] {i, f.getDestination().getId(),
                                    mapTime(f.getArrival())});
            }
            System.out.print("....");
            // Ajout des contraintes
            // Globales - Tasks disjonctives
            cpmodel.addConstraint(
                    disjunctive(stagesTaskVars));
            System.out.print("....");
            // Depart et arrivée
            /* Ville d'origine et finale de voyage non liées */
            cpmodel.addConstraint(
                    neq(startIndex, endIndex));
            /* Vols possible */
            List<int[]> temp1 = new ArrayList<int[]>();
            List<int[]> temp2 = new ArrayList<int[]>();
            for(int j = 0; j < departs.size(); j++){
                if(departs.get(j)[1] == start.getId()
                        && departs.get(j)[2] >= startDepVar.getLowB()
                        && departs.get(j)[2] <= startDepVar.getUppB()){
                    temp1.add(departs.get(j));
                }
                if(arrivees.get(j)[1] == end.getId() 
                        && arrivees.get(j)[2] >= endArrVar.getLowB()
                        && arrivees.get(j)[2] <= endArrVar.getUppB()){
                    temp2.add(arrivees.get(j));
                }
            }
            // Version AC Tuples
            cpmodel.addConstraint(feasTupleAC(feasOption, temp1,
                        startIndex, startVar, startDepVar));
            cpmodel.addConstraint(feasTupleAC(feasOption,
                    temp2, endIndex, endVar,  endArrVar));
            System.out.print("...."); 
            // Etapes
            for(int i = 0; i < stagesTaskVars.length; i++){
                IntegerVariable[] indexes = stageIndexes[i];
                TaskVariable task = stagesTaskVars[i];
                IntegerVariable stage = stagesVars[i];
                /* Ville facultative */
                cpmodel.addConstraint(ifThenElse(
                        eq(-1, indexes[0]),
                        eq(-1, indexes[1]),
                        ifThenElse(
                                eq(-1, indexes[1]),
                                eq(-1, indexes[0]),
                                neq(indexes[0], indexes[1]))));
                /* Vols possible (feasible pairs) */
                List<int[]> temp3 = new ArrayList<int[]>();
                List<int[]> temp4 = new ArrayList<int[]>();
                for(int j = 0; j < arrivees.size(); j++){
                    if((arrivees.get(j)[1] == stages.get(i).getId() 
                            || arrivees.get(j)[1] == -1)
                            && arrivees.get(j)[2] 
                                >= task.start().getLowB()
                            && arrivees.get(j)[2] 
                                <= task.end().getUppB()){
                        temp3.add(arrivees.get(j));
                    }
                    if((departs.get(j)[1] == stages.get(i).getId()
                            || departs.get(j)[1] == -1)
                            && departs.get(j)[2] 
                                >= task.start().getLowB()
                            && departs.get(j)[2] 
                                <= task.end().getUppB()){
                        temp4.add(departs.get(j));
                    }
                }
                // Version AC Tuples
                cpmodel.addConstraint(feasTupleAC(feasOption,
                        temp3, indexes[0], stage, task.start()));
                cpmodel.addConstraint(feasTupleAC(feasOption,
                        temp4, indexes[1], stage, task.end()));
                /* Contrainte sur les intervalle d'heure */
                Object[] params = new Object[3];
                params[0] = stagesHours.get(i);
                params[1] = nbTimes.get(i);
                params[2] = (int) (getEarliestDeparture().getTime()
                        /GRANULARITE)%DUR_DAY;

                cpmodel.addConstraint(
                        new ComponentConstraint(MustBeBetweenManager.class,
                                params, new IntegerVariable[] 
                                        {task.start(), task.duration()}));
                
                System.out.print("....");
            }
            
            int n = stagesVars.length;
            IntegerVariable[] allIndexes = new IntegerVariable[2*n+2];
            
            allIndexes[0] = startIndex;
            allIndexes[1] = endIndex;
            
            for(int i = 0; i < n; i++){
                IntegerVariable[] indexes = stageIndexes[i];
                allIndexes[2*i+2] = indexes[0];
                allIndexes[2*i+2+1] = indexes[1];
            }
            
            for(int i=0; i<possibleFlights.size(); i++){
                IntegerVariable v = makeIntVar("occ-"+i, new int[] {0, 2});
                cpmodel.addConstraint(occurrence(
                        v, allIndexes, i));
            }
                        
            IntegerVariable[] v = makeIntVarArray(
                    "occ-", possibleFlights.size(), new int[] {0, 2},
                    SolveConstants.VARIABLES_OPTION);
            int[] values = new int[possibleFlights.size()];
            for(int i=0; i<possibleFlights.size(); i++){
                values[i] = i;
            }
            cpmodel.addConstraint(globalCardinality(allIndexes, values, v));
            
            System.out.print("....");
            
            this.solver.read(cpmodel);
            
            System.out.print("....");
            
            this.readyToSolve = true;
            
            System.out.print("Ok ! "+"("+(System.currentTimeMillis()-t)+"ms)");

        }
        
        return retour;
    }

    @Override
    public Trip getFirstTripFound() {
        
        Trip trip = null;

        if(this.readyToSolve){
        
            ChocoLogging.toSilent();
//            solver.setVarIntSelector(new RandomIntVarSelector(solver));
//            solver.setValIntSelector(new RandomIntValSelector());
            
            System.out.println("\n" + "\n"  + "Résolution... " + "\n");
            
            if (solutionFound) {
                boolean b = this.solver.nextSolution();
                if (b) {
                    trip = this.getSolutionFound();
                }
            } else {
                boolean b = this.solver.solve();
                if (b) {
                    trip = this.getSolutionFound();
                    solutionFound = true;
                } 
            }
        }
        return trip;
    }
    
    /**
     * @return Le trip correspondant à la solution trouvée par le solver
     */
    private Trip getSolutionFound() {
        
        List<Flight> vols = new ArrayList<Flight>();
        Trip trip = null;
        Integer i = this.solver.getVar(
                startIndex).getVal();
        Flight dep = possibleFlights.get(i);
        vols.add(dep);
        
        Integer j = this.solver.getVar(
                endIndex).getVal();
        Flight arr = possibleFlights.get(j);
        vols.add(arr);
        trip = new Trip(start, dep.getDeparture(),
                end, arr.getArrival(), 
                unmapDuration(solver.getVar(totalTrip.duration()).getVal()));
        for (int s = 0; 
                s < stagesTaskVars.length; s++) {
            IntegerVariable[] v = stageIndexes[s];
            Integer k = this.solver.getVar(v[0]).getVal();
            if (k >= 0) {
                Integer k2 = this.solver.getVar(v[1]).getVal();
                Flight f = possibleFlights.get(k);
                Flight f2 = possibleFlights.get(k2);
                if(!vols.contains(f)){
                    vols.add(f);
                }
                if(!vols.contains(f2)){
                    vols.add(f2);
                }
                TaskVariable tv = stagesTaskVars[s];
                Date d1 = unmapTime(solver.getVar(tv.start()).getVal());
                Date d2 = unmapTime(solver.getVar(tv.end()).getVal());
                int dur = unmapDuration(solver.getVar(tv.duration()).getVal());
                trip.addStage(stages.get(s),
                        new Date[] {d1, d2}, dur);
            }        
        }

        trip.setFlights(vols);
        return trip;
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
    public void setStart(final Airport startAirport, final Date earliest, 
            final Date latest) {
        
        this.start = startAirport;
        this.startVar = constant(startAirport.getId());     
        this.t0Earliest = (int) (earliest.getTime()/SolveConstants.GRANULARITE);
        this.t0Latest = (int) (latest.getTime()/SolveConstants.GRANULARITE);
        
    }

    @Override
    public void setEnd(final Airport endAirport, final Date earliest, 
            final Date latest) {
        
        this.end = endAirport;
        this.endVar = constant(endAirport.getId());
        this.tmaxEarliest = (int) (earliest.getTime()
                                        /SolveConstants.GRANULARITE);
        this.tmaxLastest = (int) (latest.getTime()/SolveConstants.GRANULARITE);
        
    }
    
    @Override
    public void setOrder(final int ant, final int pos) {
        this.cpmodel.addConstraint(implies(
                and(neq(-1, stagesVars[ant]),
                        neq(-1, stagesVars[pos])),
                endsBeforeBegin(
                        this.stagesTaskVars[ant],
                        this.stagesTaskVars[pos])));
    }
    
    @Override
    public void setTotalDuration(final int hmin, final int hmax) {
        totalInterval = new int[]{hmin, hmax};       
        int min = totalInterval[0]*SolveConstants.NB_MS_IN_ONE_HOUR
                /SolveConstants.GRANULARITE;
        int max = totalInterval[1]*SolveConstants.NB_MS_IN_ONE_HOUR
                /SolveConstants.GRANULARITE;
        
        IntegerVariable duration = makeIntVar("totalDuration", min, max);
        totalTrip = makeTaskVar("totalTrip", startDepVar, endArrVar, duration);
        cpmodel.addVariable(totalTrip);
    }
    
    /**
     * @return True si le modèle est valide
     */
    public boolean isValid() {
        return (getStartAirport() != null 
                && getEndAirport() != null
                && getEarliestDeparture() != null
                && getLatestArrival() != null
                && getEarliestDeparture().before(getLatestArrival())
                && getStages().size() == getStagesIntervals().size()
                && getStages().size() < getPossibleFlights().size());
    }
    
    @Override
    public int mapTime(final Date d) {
        return (int) (d.getTime()/SolveConstants.GRANULARITE-t0Earliest);
    }

    @Override
    public int unmapDuration(final int d) {
        int duree = (d*SolveConstants.GRANULARITE);
        return duree;
    }

    @Override
    public List<int[]> getStagesHours() {
        return this.stagesHours;
    }

    @Override
    public List<Integer> getNbTimes() {
        return this.nbTimes;
    }
    
    @Override
    public List<Airport> getStages() {
        return this.stages;
    }

    @Override
    public Airport getStartAirport() {
        return this.start;
    }
    
    @Override
    public Airport getEndAirport() {
        return this.end;
    }
    
    @Override
    public Date getEarliestDeparture() {
        return this.unmapTime(0);
    }

    @Override
    public Date getLatestDeparture() {
        return this.unmapTime(t0Latest-t0Earliest);
    }

    @Override
    public Date getEarliestArrival() {
        return this.unmapTime(tmaxEarliest-t0Earliest);
    }
    
    @Override
    public Date getLatestArrival() {
        return this.unmapTime(tmaxLastest-t0Earliest);
    }
    
    @Override
    public List<Date[]> getStagesIntervals(){
        List<Date[]> retour = new ArrayList<Date[]>();
        for(int[] t : this.stagesIntervals){
            retour.add(new Date[] {this.unmapTime(t[0]-t0Earliest),
                    this.unmapTime(t[1]-t0Earliest)});
        }
        return retour;
    }
    
    @Override
    public List<int[]> getStagesDurations(){
        List<int[]> retour = new ArrayList<int[]>();
        int i = SolveConstants.GRANULARITE/SolveConstants.NB_MS_IN_ONE_HOUR;
        for(int[] t : this.stagesDurations){
            retour.add(new int[] {t[0]*i, t[1]*i});
        }
        return retour;
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
    public Date unmapTime(final int t){
        long l = (long) (t+t0Earliest)*(long) SolveConstants.GRANULARITE;
        return new Date(l);
    }

}
