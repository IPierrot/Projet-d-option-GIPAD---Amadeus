package application.test;

import model.Trip;

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
        client.loadRequest("res/requests/constraint 1.txt");
        
        ComplexTripSolver solver = new SimpleComplexTripSolver();
        solver.read(model);
        Trip trip = solver.getFirstTripFound();
        System.out.println(trip);
    }
}
