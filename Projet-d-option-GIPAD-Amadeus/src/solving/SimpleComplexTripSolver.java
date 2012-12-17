package solving;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import solving.constraints.MustBeBetweenManager;

import model.Airport;
import model.Flight;
import model.Trip;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

/**
 * Impl�mentation de ComplexTripSolver
 * @author Dimitri Justeau
 *
 */
public class SimpleComplexTripSolver implements ComplexTripSolver{

    /**
     * La granularit� de l'�chelle de temps, ici 5 minutes soit 300 000 ms.
     */
    static final int GRANULARITE = 300000;

    /**
     * Le nombre de miliseconds dans une heure.
     */
    static final int NB_MS_IN_ONE_HOUR = 3600000;
    
    static final int DUR_DAY = (NB_MS_IN_ONE_HOUR*24)/GRANULARITE;
    
    /**
     * Le solveur Choco.
     */
    private CPSolver solver;
    
    /**
     * Les dates mapp�es des d�parts et l'index du vol.
     */
    private List<int[]> departs;

    /**
     * Les dates mapp�es des arriv�es et l'index du vol.
     */
    private List<int[]> arrivees;
    
    /**
     * Les id des aeroports de d�part.
     */
    private List<int[]> airportsDep;
    
    /**
     * Les id des aeroports d'arriv�e.
     */
    private List<int[]> airportsArr;
    
    /**
     * Les vols disponibles.
     */
    private List<Flight> flights;
    
    /**
     * True si la m�thode read a �t� appell�e
     * sur le solveur depuis son instanciation
     * ou son dernier reset.
     */
    private boolean readyToSolve;
    
    /**
     * True si on a d�j� trouv� une solution.
     */
    private boolean solutionFound;
    
    /**
     * Le ComplexTripModel charg�, si il y en a un.
     */
    private ComplexTripModel cxtModel;
    
    /**
     * Constructeur sans param�tres
     */
    public SimpleComplexTripSolver(){
        this.solver = new CPSolver();
        this.departs = new ArrayList<int[]>();
        this.arrivees= new ArrayList<int[]>();
        this.airportsDep = new ArrayList<int[]>();
        this.airportsArr = new ArrayList<int[]>();
        this.flights = null;
        this.readyToSolve = false;
        this.solutionFound = false;
        this.cxtModel = null;
    }
    
    @Override
    public CPSolver getCPSolver() {
        return this.solver;
    }

    @Override
    public boolean read(final ComplexTripModel cxtmodel) {
        
        boolean retour = true;
        
        long t = System.currentTimeMillis();
        System.out.println("\n" + "\n"  + "Application des contraintes ");
        
        this.cxtModel = cxtmodel;
        CPModel cpmodel = cxtmodel.getCPModel();
        this.flights = cxtmodel.getPossibleFlights();
        
        if(!cxtModel.isValid()){
            retour = false;
        }
        
        if (retour) {
            
            String feasOption = "";
        
            // Initialisation des donn�es
            this.departs.add(new int[] {-1, -1, -1});
            this.arrivees.add(new int[] {-1, -1, -1});
            this.airportsDep.add(new int[] {-1, 0});
            this.airportsArr.add(new int[] {-1, 0});
            for(int i=0; i<this.flights.size(); i++){
                Flight f = this.flights.get(i);
                this.departs.add(
                        new int[] {i, f.getOrigin().getId(),
                                cxtmodel.mapTime(f.getDeparture())});
                
                this.arrivees.add(
                        new int[] {i, f.getDestination().getId(),
                                cxtmodel.mapTime(f.getArrival())});
                
//                this.departs.add(
//                        new int[] {i, cxtmodel.mapTime(f.getDeparture())});
//                
//                this.arrivees.add(
//                        new int[] {i, cxtmodel.mapTime(f.getArrival())});
                
                this.airportsDep.add(new int[] {i, f.getOrigin().getId()});
                this.airportsArr.add(new int[] {i, f.getDestination().getId()});
            }
            
            System.out.print("....");
            
            // Ajout des contraintes
            
            // Globales - Tasks disjonctives
            cpmodel.addConstraint(
                    disjunctive(cxtmodel.getStagesTaskVariables()));
            
            // Initialisation des dates pour le allDif
            IntegerVariable[] dates = 
                    new IntegerVariable[cxtmodel.getStages().size()*2+2];
            dates[0] = cxtmodel.getStartDeparture();
            dates[1] = cxtmodel.getEndArrival();
            
            System.out.print("....");
            
            // Depart et arriv�e
            
            /* Ville d'origine et finale de voyage non li�es */
            cpmodel.addConstraint(
                    neq(cxtmodel.getStartIndex(), cxtmodel.getEndIndex()));
            
            /* Vols possible */
            
            List<int[]> temp1 = new ArrayList<int[]>();
            List<int[]> temp2 = new ArrayList<int[]>();
            List<int[]> temp3 = new ArrayList<int[]>();
            List<int[]> temp4 = new ArrayList<int[]>();
            for(int j = 0; j < departs.size(); j++){
                if(departs.get(j)[1] == cxtmodel.getStartAirport().getId()
                        && departs.get(j)[2] 
                                >= cxtmodel.getStartDeparture().getLowB()
                        && departs.get(j)[2] 
                                <= cxtmodel.getStartDeparture().getUppB()){
//                    temp1.add(airportsDep.get(j));
//                    temp2.add(departs.get(j));
                    temp1.add(departs.get(j));
                }
                if(arrivees.get(j)[1] == cxtmodel.getEndAirport().getId() 
                        && arrivees.get(j)[2] 
                                >= cxtmodel.getEndArrival().getLowB()
                        && arrivees.get(j)[2] 
                                <= cxtmodel.getEndArrival().getUppB()){
//                    temp3.add(airportsArr.get(j));
//                    temp4.add(arrivees.get(j));
                    temp2.add(arrivees.get(j));
                }
            }
            
            // Version AC Pairs
            
//            cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
//                    cxtmodel.getStartVariable(), temp1));
//            
//            cpmodel.addConstraint(feasPairAC(cxtmodel.getStartIndex(),
//                    cxtmodel.getStartDeparture(), temp2));
//
//            cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
//                    cxtmodel.getEndVariable(), temp3));
//            
//            cpmodel.addConstraint(feasPairAC(cxtmodel.getEndIndex(),
//                    cxtmodel.getEndArrival(), temp4));
            
            // Version AC Tuples
            cpmodel.addConstraint(feasTupleAC(feasOption,
                    temp1, cxtmodel.getStartIndex(),
                    cxtmodel.getStartVariable(), cxtmodel.getStartDeparture()));
            
            cpmodel.addConstraint(feasTupleAC(feasOption,
                    temp2, cxtmodel.getEndIndex(),
                    cxtmodel.getEndVariable(),  cxtmodel.getEndArrival()));

         
            System.out.print("....");
            
            
            
            // Etapes

            for(int i = 0; i < cxtmodel.getStagesVariables().length; i++){
                IntegerVariable[] indexes = cxtmodel.getStagesIndexes()[i];
                TaskVariable task = cxtmodel.getStagesTaskVariables()[i];
                IntegerVariable stage = cxtmodel.getStagesVariables()[i];
                dates[(i+1)*2] = task.start();
                dates[(i+1)*2+1] = task.end();                
                
                /* Ville facultative */
                cpmodel.addConstraint(ifThenElse(
                        eq(-1, indexes[0]),
                        eq(-1, indexes[1]),
                        ifThenElse(
                                eq(-1, indexes[1]),
                                eq(-1, indexes[0]),
                                and(neq(task.start(), task.end())))));
                

                /* Vols possible (feasible pairs) */
                List<int[]> temp5 = new ArrayList<int[]>();
                List<int[]> temp6 = new ArrayList<int[]>();
                List<int[]> temp7 = new ArrayList<int[]>();
                List<int[]> temp8 = new ArrayList<int[]>();
                for(int j = 0; j < arrivees.size(); j++){
                    if((arrivees.get(j)[1]
                            == cxtmodel.getStages().get(i).getId()
                            || arrivees.get(j)[1] == -1)
                            && arrivees.get(j)[2] 
                                >= task.start().getLowB()
                            && arrivees.get(j)[2] 
                                <= task.end().getUppB()){
//                        temp5.add(airportsArr.get(j));
//                        temp6.add(arrivees.get(j));
                        temp5.add(arrivees.get(j));
                    }
                    if((departs.get(j)[1]
                            == cxtmodel.getStages().get(i).getId()
                            || departs.get(j)[1] == -1)
                            && departs.get(j)[2] 
                                >= task.start().getLowB()
                            && departs.get(j)[2] 
                                <= task.end().getUppB()){
//                        temp7.add(airportsDep.get(j));
//                        temp8.add(departs.get(j));
                        temp6.add(departs.get(j));
                    }
                }
                
                // Version AC
                
                // Version AC Pairs
//                cpmodel.addConstraint(feasPairAC(
//                        indexes[0], stage, temp5));
//                
//                cpmodel.addConstraint(feasPairAC(
//                        indexes[0], task.start(), temp6));
//                
//                cpmodel.addConstraint(feasPairAC(
//                        indexes[1], stage, temp7));
//                
//                cpmodel.addConstraint(feasPairAC(
//                        indexes[1], task.end(), temp8));
                
                // Version AC Tuples
                cpmodel.addConstraint(feasTupleAC(feasOption,
                        temp5, indexes[0], stage, task.start()));
                cpmodel.addConstraint(feasTupleAC(feasOption,
                        temp6, indexes[1], stage, task.end()));

                
                /* Contrainte sur les intervalle d'heure */
                Object[] params = new Object[3];
                params[0] = cxtmodel.getStagesHours().get(i);
                params[1] = cxtmodel.getNbTimes().get(i);
                params[2] = (int) (cxtmodel.getEarliestDeparture().
                        getTime()/GRANULARITE)%DUR_DAY;

                cpmodel.addConstraint(
                        new ComponentConstraint(MustBeBetweenManager.class,
                                params, new IntegerVariable[] 
                                        {task.start(), task.duration()}));
                
                System.out.print("....");
            }

//            cpmodel.addConstraint(allDifferent(dates));
            
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
                        
//            IntegerVariable[] v = makeIntVarArray(
//                    "occ-", flights.size(), new int[] {0, 2},
//                    SolveConstants.VARIABLES_OPTION);
//            int[] values = new int[flights.size()];
//            for(int i=0; i<flights.size(); i++){
//                values[i] = i;
//            }
//            cpmodel.addConstraint(globalCardinality(allIndexes, values, v));
            
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
            solver.setVarIntSelector(new RandomIntVarSelector(solver));
//            solver.setValIntSelector(new RandomIntValSelector());
            
            System.out.println("\n" + "\n"  + "R�solution... " + "\n");
            
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
     * @return Le trip correspondant � la solution trouv�e par le solver
     */
    private Trip getSolutionFound() {
        
        List<Flight> vols = new ArrayList<Flight>();
        Trip trip = null;
        Integer i = this.solver.getVar(
                cxtModel.getStartIndex()).getVal();
        Flight dep = flights.get(i);
        vols.add(dep);
        
        Integer j = this.solver.getVar(
                cxtModel.getEndIndex()).getVal();
        Flight arr = flights.get(j);
        vols.add(arr);
        trip = new Trip(cxtModel.getStartAirport(), dep.getDeparture(),
                cxtModel.getEndAirport(), arr.getArrival(), 
                cxtModel.unmapDuration(solver.getVar(
                        cxtModel.getTotalTrip().duration()).getVal()));
        
        for (int s = 0; 
                s < cxtModel.getStagesTaskVariables().length; s++) {
            IntegerVariable[] v = cxtModel.getStagesIndexes()[s];
            Integer k = this.solver.getVar(v[0]).getVal();
            if (k >= 0) {
                Integer k2 = this.solver.getVar(v[1]).getVal();
                Flight f = flights.get(k);
                Flight f2 = flights.get(k2);
                
                if(!vols.contains(f)){
                    vols.add(f);
                }
                if(!vols.contains(f2)){
                    vols.add(f2);
                }
                
                TaskVariable tv = cxtModel.
                        getStagesTaskVariables()[s];
                Date d1 = cxtModel.unmapTime(
                        solver.getVar(tv.start()).getVal());
                Date d2 = cxtModel.unmapTime(
                        solver.getVar(tv.end()).getVal());
                int dur = cxtModel.unmapDuration(
                        solver.getVar(tv.duration()).getVal());

                trip.addStage(cxtModel.getStages().get(s),
                        new Date[] {d1, d2}, dur);
            }        
        }

        trip.setFlights(vols);
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
        this.readyToSolve = false;
    }
}
