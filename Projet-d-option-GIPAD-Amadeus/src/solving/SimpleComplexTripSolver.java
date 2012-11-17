package solving;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Flight;
import model.Trip;

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
        
        long t = System.currentTimeMillis();
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
        
        System.out.print("Ok ! "+"("+(System.currentTimeMillis()-t)+"ms)");
    }

    @Override
    public Trip getFirstTripFound() {
        List<Flight> temp = new ArrayList<Flight>();
        List<Flight> vols = new ArrayList<Flight>();
        Trip trip = null;
        
        if(this.readyToSolve){
        
            ChocoLogging.setVerbosity(Verbosity.VERBOSE);
            
            System.out.println("\n" + "\n"  + "Résolution... " + "\n");
            
            boolean b = this.solver.solve();
            if(b){

                Integer i = this.solver.getVar(
                        cxtModel.getStartIndex()).getVal();
                Flight dep = flights.get(i);
                temp.add(dep);
                
                Integer j = this.solver.getVar(
                        cxtModel.getEndIndex()).getVal();
                Flight arr = flights.get(j);
                temp.add(arr);

                trip = new Trip(cxtModel.getStartAirport(), dep.getDeparture(),
                        cxtModel.getEndAirport(), arr.getArrival());
                
                for(IntegerVariable[] v : cxtModel.getStagesIndexes()){
                    Integer k = this.solver.getVar(v[0]).getVal();
                    Integer k2 = this.solver.getVar(v[1]).getVal();
                    Flight f = flights.get(k);
                    Flight f2 = flights.get(k2);
                    
                    if(!temp.contains(f)){
                        temp.add(f);
                    }
                    if(!temp.contains(f2)){
                        temp.add(f2);
                    }
                }
                
                List<Integer> sorted = sort(temp);
                
                for (int k = 0; k < temp.size(); k++) {
                    int l1 = sorted.get(k);
                    int l2 = (k == temp.size()-1) ? sorted.get(k-1)
                        : sorted.get(k+1);
                    if (l1 < temp.size()-1) {
                        TaskVariable tv = cxtModel.getStagesTaskVariables()[l1];
                        double dur = cxtModel.unmapDuration(
                                solver.getVar(tv.duration()).getVal());
                        
                        Flight f1 = temp.get(l1);
                        Flight f2 = temp.get(l2);
                        
                        if (l1 == temp.size()-2) {
                            vols.add(f1);
                            vols.add(f2);
                        } else {
                            vols.add(f1);
                        }
                        if (k != temp.size()-1) {
                            Date d1 = f1.getArrival();
                            Date d2 = f2.getDeparture();
                            trip.addStage(f1.getDestination(),
                                    new Date[] {d1, d2}, dur);
                        } else {
                            Date d1 = f2.getArrival();
                            Date d2 = f1.getDeparture();
                            trip.addStage(f1.getOrigin(),
                                    new Date[] {d1, d2}, dur);
                        }
                    }    
                }
                trip.setFlights(vols);
            }
        }
        return trip;
    }
    
    /**
     * Ordonne les indexes d'une liste de vols (de facon à ce que les vols se 
     * suivent dans le temps.
     * @param flights La liste à ordonner
     * @return La liste ordonnée et sans doublons
     */
    private static List<Integer> sort(final List<Flight> flights){
        List<Integer> retour = new ArrayList<Integer>();
        for(int j = 0; j<flights.size(); j++){
            Flight f = flights.get(j);
            if(retour.isEmpty()){
                retour.add(j);
            } else{
                int i = 0;
                while(i < retour.size() 
                     && f.getDeparture().after(
                             flights.get(retour.get(i)).getDeparture())){
                    i++;
                }
                retour.add(i, j);
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
