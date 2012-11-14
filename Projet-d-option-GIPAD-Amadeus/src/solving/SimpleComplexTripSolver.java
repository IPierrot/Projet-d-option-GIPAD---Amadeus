package solving;

import java.util.ArrayList;
import java.util.List;

import model.Flight;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

/**
 * Implémentation de ComplexTripSolver
 * @author Dimitri Justeau
 *
 */
public class SimpleComplexTripSolver implements ComplexTripSolver{

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
     * Les id des aeroports de départ.
     */
    private List<int[]> airportsDep;
    
    /**
     * Les id des aeroports d'arrivée.
     */
    private List<int[]> airportsArr;
    
    /**
     * Les vols disponibles.
     */
    private List<Flight> flights;
    
    /**
     * Les indexes des vols de la solutions trouvée.
     */
    private IntegerVariable[] flightsIndexes;
    
    /**
     * True si la méthode read a été appellée
     * sur le solveur depuis son instanciation
     * ou son dernier reset.
     */
    private boolean readyToSolve;
    
    /**
     * Constructeur sans paramètres
     */
    public SimpleComplexTripSolver(){
        this.solver = new CPSolver();
        this.departs = new ArrayList<int[]>();
        this.arrivees= new ArrayList<int[]>();
        this.airportsDep = new ArrayList<int[]>();
        this.airportsArr = new ArrayList<int[]>();
        this.flights = null;
        this.flightsIndexes = null;
        this.readyToSolve = false;
    }
    
    @Override
    public CPSolver getCPSolver() {
        return this.solver;
    }

    @Override
    public void read(final ComplexTripModel cxtmodel) {
        
        CPModel cpmodel = cxtmodel.getCPModel();
        
//        for(int i = 0; i<cpmodel.getNbIntVars(); i++){
//            System.out.println(cpmodel.getIntVar(i));
//        }
        
        this.flights = cxtmodel.getPossibleFlights();
        this.flightsIndexes = cxtmodel.getIndexes();
        
        // Initialisation des données
        for(int i=0; i<this.flights.size(); i++){
            Flight f = this.flights.get(i);
            this.departs.add(new int[] {i, cxtmodel.mapTime(f.getDeparture())});
            this.arrivees.add(new int[] {i, cxtmodel.mapTime(f.getArrival())});
            this.airportsDep.add(new int[] {i, f.getOrigin().getId()});
            this.airportsArr.add(new int[] {i, f.getDestination().getId()});
        }
        
        // Ajout des contraintes
        
        // Globales
        cpmodel.addConstraint(disjunctive(cxtmodel.getStagesTaskVariables()));
        cpmodel.addConstraint(allDifferent(cxtmodel.getIndexes()));
        
        // Depart et arrivée
        cpmodel.addConstraint(
                neq(cxtmodel.getStartIndex(), cxtmodel.getEndIndex()));
        
        List<int[]> temp = new ArrayList<int[]>();
        for(int[] j : airportsDep){
            if(j[1] == cxtmodel.getStartAirport().getId()){
                temp.add(j);
            }
        }
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
                cxtmodel.getStartVariable(), temp));
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
                cxtmodel.getStartDeparture(), departs));
        
        temp = new ArrayList<int[]>();
        for(int[] j : airportsArr){
            if(j[1] == cxtmodel.getEndAirport().getId()){
                temp.add(j);
            }
        }
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
                cxtmodel.getEndVariable(), temp));
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
                cxtmodel.getEndArrival(), arrivees));
     
        // Etapes
        for(int i = 0; i < cxtmodel.getStagesVariables().length; i++){
            IntegerVariable[] indexes = cxtmodel.getStagesIndexes()[i];
            TaskVariable task = cxtmodel.getStagesTaskVariables()[i];
            IntegerVariable stage = cxtmodel.getStagesVariables()[i];
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[0], task.start(), arrivees));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[1], task.end(), departs));
            
            temp = new ArrayList<int[]>();
            for(int[] j : airportsArr){
                if(j[1] == cxtmodel.getStages().get(i).getId()){
                    temp.add(j);
                }
            }
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[0], stage, temp));
            
            temp = new ArrayList<int[]>();
            for(int[] j : airportsDep){
                if(j[1] == cxtmodel.getStages().get(i).getId()){
                    temp.add(j);
                }
            }
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[1], stage, temp));
        }
        
        int n = cxtmodel.getStagesVariables().length;
        IntegerVariable[] allIndexes = new IntegerVariable[2*n+2];
        
        allIndexes[0] = cxtmodel.getStartIndex();
        allIndexes[1] = cxtmodel.getEndIndex();
        
        for(int i = 0; i < n; i++){
            IntegerVariable[] indexes = cxtmodel.getStagesIndexes()[i];
            allIndexes[2*i+2] = indexes[0];
            allIndexes[2*i+2+1] = indexes[1];
        }
        

        
        for(int i = 0; i < cxtmodel.getIndexes().length; i++){
            cpmodel.addConstraint(occurrenceMin(
                    cxtmodel.getIndexes()[i], allIndexes, 2));
        }
        
        this.solver.read(cpmodel);
        this.readyToSolve = true;
    }

    @Override
    public List<Flight> getFirstTripFound() {
        List<Flight> trip = new ArrayList<Flight>();
        if(this.readyToSolve){
        
            ChocoLogging.setVerbosity(Verbosity.SOLUTION);
            
            boolean b = this.solver.solve();
            if(b){
                for(IntegerVariable v : this.flightsIndexes){
                    Integer i = this.solver.getVar(v).getVal();
                    System.out.println(i);
                    trip.add(this.flights.get(i));
                }
            }
            
        }
        return trip;
    }

    @Override
    public void reset() {
        this.solver = new CPSolver();
        this.departs = new ArrayList<int[]>();
        this.arrivees= new ArrayList<int[]>();
        this.airportsDep = new ArrayList<int[]>();
        this.airportsArr = new ArrayList<int[]>();
        this.flights = null;
        this.flightsIndexes = null;
        this.readyToSolve = false;
    }

}
