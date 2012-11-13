package solving;

import java.util.ArrayList;
import java.util.List;

import model.Flight;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
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
     * Constructeur sans paramètres
     */
    public SimpleComplexTripSolver(){
        this.solver = new CPSolver();
        this.departs = new ArrayList<int[]>();
        this.arrivees= new ArrayList<int[]>();
        this.airportsDep = new ArrayList<int[]>();
        this.airportsArr = new ArrayList<int[]>();
    }
    
    @Override
    public CPSolver getCPSolver() {
        return this.solver;
    }

    @Override
    public void read(final ComplexTripModel cxtmodel) {
        
        CPModel cpmodel = cxtmodel.getCPModel();
        
        // Initialisation des données
        for(int i=0; i<cxtmodel.getPossibleFlights().size(); i++){
            Flight f = cxtmodel.getPossibleFlights().get(i);
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
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
                cxtmodel.getStartVariable(), airportsDep));
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
                cxtmodel.getStartDeparture(), departs));
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
                cxtmodel.getEndVariable(), airportsArr));
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
                cxtmodel.getEndArrival(), arrivees));
     
        // Etapes
        for(int i = 0; i < cxtmodel.getStagesVariables().length; i++){
            IntegerVariable[] indexes = cxtmodel.getStageIndexes()[i];
            TaskVariable task = cxtmodel.getStagesTaskVariables()[i];
            IntegerVariable stage = cxtmodel.getStagesVariables()[i];
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[0], task.start(), arrivees));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[1], task.end(), departs));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[0], stage, airportsArr));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[1], stage, airportsDep));
        }
        
        int n = cxtmodel.getStagesVariables().length;
        IntegerVariable[] allIndexes = new IntegerVariable[2*n+2];
        
        allIndexes[0] = cxtmodel.getStartIndex();
        allIndexes[1] = cxtmodel.getEndIndex();
        
        for(int i = 0; i < n; i++){
            IntegerVariable[] indexes = cxtmodel.getStageIndexes()[i];
            allIndexes[2*i+2] = indexes[0];
            allIndexes[2*i+2+1] = indexes[1];
        }
        
        for(int i = 0; i < cxtmodel.getIndexes().length; i++){
            cpmodel.addConstraint(occurrence(
                    cxtmodel.getIndexes()[i], allIndexes, 2));
        }
    }

}
