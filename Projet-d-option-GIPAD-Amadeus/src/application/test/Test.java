package application.test;

import java.util.List;

import model.Flight;

import context.Client;
import context.Context;
import dao.DAO;
import dao.csv.DAOImplCSV;
import reader.RequestLoader;
import reader.RequestLoaderImp;
import solving.ComplexTripModel;
import solving.ComplexTripSolver;
import solving.SimpleComplexTripModel;
import solving.SimpleComplexTripSolver;

public class Test {

    public static void main(String[] args){
        
        ComplexTripModel model = new SimpleComplexTripModel();
        DAO dao = new DAOImplCSV();
        
        Context context = new Context(model, dao);
        
        RequestLoader rloader = new RequestLoaderImp();
        Client client = new Client(context, rloader);
        client.loadRequest("res/requests/constraint.txt");
        
        ComplexTripSolver solver = new SimpleComplexTripSolver();
        solver.read(model);
        List<Flight> trip = solver.getFirstTripFound();
        
        System.out.println("\n" + "Itinéraire trouvé : "  + "\n");
        
        for(int i = 0; i < trip.size(); i++){
            System.out.println("Vol " + i + " : " + trip.get(i));
        }
        
        System.out.println();
    }
}
