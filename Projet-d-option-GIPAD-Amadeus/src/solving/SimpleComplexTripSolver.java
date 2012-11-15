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
     * True si la méthode read a été appellée
     * sur le solveur depuis son instanciation
     * ou son dernier reset.
     */
    private boolean readyToSolve;
    
    /**
     * Le ComplexTripModel chargé, si il y en a un.
     */
    private ComplexTripModel cxtModel;
    
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
        this.readyToSolve = false;
        this.cxtModel = null;
    }
    
    @Override
    public CPSolver getCPSolver() {
        return this.solver;
    }

    @Override
    public void read(final ComplexTripModel cxtmodel) {
        
        System.out.println("\n" + "\n"  + "Application des contraintes ");
        
        this.cxtModel = cxtmodel;
        CPModel cpmodel = cxtmodel.getCPModel();
        this.flights = cxtmodel.getPossibleFlights();
        
        // Initialisation des données
        for(int i=0; i<this.flights.size(); i++){
            Flight f = this.flights.get(i);
            this.departs.add(new int[] {i, cxtmodel.mapTime(f.getDeparture())});
            this.arrivees.add(new int[] {i, cxtmodel.mapTime(f.getArrival())});
            this.airportsDep.add(new int[] {i, f.getOrigin().getId()});
            this.airportsArr.add(new int[] {i, f.getDestination().getId()});
        }
        
        System.out.print("....");
        
        // Ajout des contraintes
        
        // Globales
        cpmodel.addConstraint(disjunctive(cxtmodel.getStagesTaskVariables()));
//        cpmodel.addConstraint(allDifferent(cxtmodel.getIndexes()));
        
        System.out.print("....");
        
        // Depart et arrivée
        cpmodel.addConstraint(
                neq(cxtmodel.getStartIndex(), cxtmodel.getEndIndex()));
        
        List<int[]> temp1 = new ArrayList<int[]>();
        List<int[]> temp2 = new ArrayList<int[]>();
        for(int j = 0; j < airportsDep.size(); j++){
            if(airportsDep.get(j)[1] == cxtmodel.getStartAirport().getId()){
                temp1.add(airportsDep.get(j));
                temp2.add(departs.get(j));
            }
        }
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
                cxtmodel.getStartVariable(), temp1));
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
                cxtmodel.getStartDeparture(), temp2));
        
        List<int[]> temp3 = new ArrayList<int[]>();
        List<int[]> temp4 = new ArrayList<int[]>();
        for(int j = 0; j < airportsArr.size(); j++){
            if(airportsArr.get(j)[1] == cxtmodel.getEndAirport().getId()){
                temp3.add(airportsArr.get(j));
                temp4.add(arrivees.get(j));
            }
        }
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
                cxtmodel.getEndVariable(), temp3));
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
                cxtmodel.getEndArrival(), temp4));
     
        System.out.print("....");
        
        // Etapes
        for(int i = 0; i < cxtmodel.getStagesVariables().length; i++){
            IntegerVariable[] indexes = cxtmodel.getStagesIndexes()[i];
            TaskVariable task = cxtmodel.getStagesTaskVariables()[i];
            IntegerVariable stage = cxtmodel.getStagesVariables()[i];
            
            cpmodel.addConstraint(neq(indexes[0], indexes[1]));
            
            List<int[]> temp5 = new ArrayList<int[]>();
            List<int[]> temp6 = new ArrayList<int[]>();
            List<int[]> temp7 = new ArrayList<int[]>();
            List<int[]> temp8 = new ArrayList<int[]>();
            for(int j = 0; j < airportsArr.size(); j++){
                if(airportsArr.get(j)[1]
                        == cxtmodel.getStages().get(i).getId()){
                    temp5.add(airportsArr.get(j));
                    temp6.add(arrivees.get(j));
                }
                if(airportsDep.get(j)[1]
                        == cxtmodel.getStages().get(i).getId()){
                    temp7.add(airportsDep.get(j));
                    temp8.add(departs.get(j));
                }
            }
                        
            cpmodel.addConstraint(feasPairAC(
                    indexes[0], stage, temp5));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[0], task.start(), temp6));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[1], stage, temp7));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[1], task.end(), temp8));
            
            System.out.print("....");
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
        
//        for(int i=0; i<flights.size(); i++){
//            IntegerVariable v = makeIntVar("occ-"+i, new int[] {0, 2});
//            cpmodel.addConstraint(occurrence(
//                    v, allIndexes, i));
//        }
        IntegerVariable[] v = makeIntVarArray(
                "occ-", flights.size(), new int[] {0, 2});
        int[] values = new int[flights.size()];
        for(int i=0; i<flights.size(); i++){
            values[i] = i;
        }
        cpmodel.addConstraint(globalCardinality(allIndexes, values, v));
        
        System.out.print("....");
        
        this.solver.read(cpmodel);
        
        System.out.print("....");
        
        this.readyToSolve = true;
        
        System.out.print("Ok !");
    }

    @Override
    public List<Flight> getFirstTripFound() {
//        List<Flight> trip = new ArrayList<Flight>();
        List<Flight> temp = new ArrayList<Flight>();
        
        if(this.readyToSolve){
        
            ChocoLogging.setVerbosity(Verbosity.SOLUTION);
            
            System.out.println("\n" + "\n"  + "Résolution... " + "\n");
            
            boolean b = this.solver.solve();
            if(b){

                Integer i = this.solver.getVar(
                        cxtModel.getStartIndex()).getVal();
                temp.add(flights.get(i));
                
                Integer j = this.solver.getVar(
                        cxtModel.getEndIndex()).getVal();
                temp.add(flights.get(j));

                for(IntegerVariable[] v : cxtModel.getStagesIndexes()){
                    Integer k = this.solver.getVar(v[0]).getVal();
                    Integer k2 = this.solver.getVar(v[1]).getVal();
                    Flight f = flights.get(k);
                    Flight f2 = flights.get(k2);

//                    if (!trip.contains(f)){
                        temp.add(f);
                        temp.add(f2);
//                    }
                }
                
//                for(Flight f : temp){
//                    if(trip.size() == 0){
//                        trip.add(f);
//                    } else{
//                        boolean inserted = false;
//                        int l=0;
//                        while(!inserted){
//                            if(f.getDeparture().before(
//                                    trip.get(i).getDeparture())){
//                                trip.add(i, f);
//                                inserted = true;
//                            } else{
//                                i++;
//                                if(i==trip.size()){
//                                    trip.add(f);
//                                    inserted = true;
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }
        return temp;
    }

    @Override
    public void reset() {
        this.solver = new CPSolver();
        this.departs = new ArrayList<int[]>();
        this.arrivees= new ArrayList<int[]>();
        this.airportsDep = new ArrayList<int[]>();
        this.airportsArr = new ArrayList<int[]>();
        this.flights = null;
        this.readyToSolve = false;
    }
}
