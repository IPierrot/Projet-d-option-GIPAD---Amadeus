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
     * Le modèle à résoudre.
     */
    private ComplexTripModel cxtmodel;
    
    /**
     * Le solveur Choco.
     */
    private CPSolver solver;
    
    /**
     * Le modele Choco.
     */
    private CPModel cpmodel;
    
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
        this.cpmodel = new CPModel();
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
    public void read(final ComplexTripModel cxtModel) {
        this.cxtmodel = cxtModel;
        
        // Ajout des variables
        cpmodel.addVariable(cxtmodel.getEndArrival());
        cpmodel.addVariable(cxtmodel.getEndVariable());
        cpmodel.addVariable(cxtmodel.getStartDeparture());
        cpmodel.addVariable(cxtmodel.getStartVariable());
        for(IntegerVariable v : cxtmodel.getIndexVariables()){
            cpmodel.addVariable(v);
        }
        for(int i=0; i<cxtmodel.getStagesTaskVariables().size(); i++){
            cpmodel.addVariable(cxtmodel.getStagesTaskVariables().get(i));
            cpmodel.addVariable(cxtmodel.getStagesVariables().get(i));
        }
        
        // Initialisation des données
        for(int i=0; i<cxtmodel.getPossibleFlights().size(); i++){
            Flight f = cxtModel.getPossibleFlights().get(i);
            this.departs.add(new int[] {i, cxtModel.mapTime(f.getDeparture())});
            this.arrivees.add(new int[] {i, cxtModel.mapTime(f.getArrival())});
            this.airportsDep.add(new int[] {i, f.getOrigin().getId()});
            this.airportsArr.add(new int[] {i, f.getDestination().getId()});
        }
        
        // Ajout des contraintes
        
        // Disjonctive
        TaskVariable[] tasks = 
                new TaskVariable[cxtmodel.getStagesTaskVariables().size()];
        for(int i=0; i<cxtmodel.getStagesTaskVariables().size(); i++){
            tasks[i] = cxtmodel.getStagesTaskVariables().get(i);
        }
        this.cpmodel.addConstraint(disjunctive(tasks));
        
        // Depart
        this.cpmodel.addConstraint(feasPairAC(
                cxtModel.getIndexVariables().get(0),
                cxtmodel.getStartDeparture(), this.departs));
    }

    @Override
    public CPModel getCPModel() {
        return this.cpmodel;
    }

}
