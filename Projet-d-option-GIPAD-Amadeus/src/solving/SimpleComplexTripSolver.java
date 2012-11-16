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
        
        // Globales - Tasks disjonctives
        cpmodel.addConstraint(disjunctive(cxtmodel.getStagesTaskVariables()));
        
        System.out.print("....");
        
        // Depart et arrivée
        
        /* Ville d'origine et finale de voyage non liées */
        cpmodel.addConstraint(
                neq(cxtmodel.getStartIndex(), cxtmodel.getEndIndex()));

        /* Vols possible (feasible pairs) */
        List<int[]> temp1 = new ArrayList<int[]>();
        List<int[]> temp2 = new ArrayList<int[]>();
        for(int j = 0; j < airportsDep.size(); j++){
            if(airportsDep.get(j)[1] == cxtmodel.getStartAirport().getId()){
                temp1.add(airportsDep.get(j));
                temp2.add(departs.get(j));
            }
        }
        
        // Version AC
        cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
                cxtmodel.getStartVariable(), temp1));
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
                cxtmodel.getStartDeparture(), temp2));
 
        // Version FC
//        cpmodel.addConstraint(feasTupleFC(temp1, cxtmodel.getStartIndex(),
//                cxtmodel.getStartVariable()));
//        
//        cpmodel.addConstraint(feasTupleFC(temp2, cxtmodel.getStartIndex(),
//                cxtmodel.getStartDeparture()));
        
        List<int[]> temp3 = new ArrayList<int[]>();
        List<int[]> temp4 = new ArrayList<int[]>();
        for(int j = 0; j < airportsArr.size(); j++){
            if(airportsArr.get(j)[1] == cxtmodel.getEndAirport().getId()){
                temp3.add(airportsArr.get(j));
                temp4.add(arrivees.get(j));
            }
        }
        
        // Version AC
        cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
                cxtmodel.getEndVariable(), temp3));
        
        cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
                cxtmodel.getEndArrival(), temp4));
        
        // Version FC
//        cpmodel.addConstraint(feasTupleFC(temp3, cxtmodel.getEndIndex(),
//                cxtmodel.getEndVariable()));
//        
//        cpmodel.addConstraint(feasTupleFC(temp4, cxtmodel.getEndIndex(),
//                cxtmodel.getEndArrival()));
     
        System.out.print("....");
        
        // Etapes
        for(int i = 0; i < cxtmodel.getStagesVariables().length; i++){
            IntegerVariable[] indexes = cxtmodel.getStagesIndexes()[i];
            TaskVariable task = cxtmodel.getStagesTaskVariables()[i];
            IntegerVariable stage = cxtmodel.getStagesVariables()[i];
            
            /* On ne "survole" pas une étape :P */
            cpmodel.addConstraint(neq(indexes[0], indexes[1]));
            
            /* Vols possible (feasible pairs) */
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
            
            // Version AC
            cpmodel.addConstraint(feasPairAC(
                    indexes[0], stage, temp5));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[0], task.start(), temp6));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[1], stage, temp7));
            
            cpmodel.addConstraint(feasPairAC(
                    indexes[1], task.end(), temp8));
            
            // Version FC
//            cpmodel.addConstraint(feasTupleFC(temp5, indexes[0], stage));
//            cpmodel.addConstraint(feasTupleFC(temp6, indexes[0], task.start()));
//            
//            cpmodel.addConstraint(feasTupleFC(temp7, indexes[1], stage));
//            cpmodel.addConstraint(feasTupleFC(temp8, indexes[1], task.end()));
            
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
        
        for(int i=0; i<flights.size(); i++){
            IntegerVariable v = makeIntVar("occ-"+i, new int[] {0, 2});
            cpmodel.addConstraint(occurrence(
                    v, allIndexes, i));
        }
//        IntegerVariable[] v = makeIntVarArray(
//                "occ-", flights.size(), new int[] {0, 2},
//                SimpleComplexTripModel.VARIABLES_OPTION);
//        int[] values = new int[flights.size()];
//        for(int i=0; i<flights.size(); i++){
//            values[i] = i;
//        }
//        cpmodel.addConstraint(globalCardinality(allIndexes, values, v));
        
        System.out.print("....");
        
        this.solver.read(cpmodel);
        
        System.out.print("....");
        
        this.readyToSolve = true;
        
        System.out.print("Ok !");
    }

    @Override
    public List<Flight> getFirstTripFound() {
        List<Flight> temp = new ArrayList<Flight>();
        
        if(this.readyToSolve){
        
            ChocoLogging.setVerbosity(Verbosity.DEFAULT);
            
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

                    temp.add(f);
                    temp.add(f2);
                }
            }
        }
        return sort(temp);
    }
    
    /**
     * Ordonne et supprime les doublons d'une liste de vols
     * @param flights La liste à ordonner
     * @return La liste ordonnée et sans doublons
     */
    private static List<Flight> sort(final List<Flight> flights){
        List<Flight> retour = new ArrayList<Flight>();
        for(Flight f : flights){
            if(!retour.contains(f)){
                if(retour.isEmpty()){
                    retour.add(f);
                } else{
                    int i = 0;
                    while(i < retour.size() 
                         && f.getDeparture().after(retour.get(i).getDeparture())
                            ){
                        i++;
                    }
                    retour.add(i, f);
                }
            }
        }
        return retour;
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
