package context.userConstraints.cg;

import model.Flight;
import context.Context;


//Changements totaux CG01:
//CG01
//CTM --> setOrder
//SCTM --> implémentation setOrder + méthode readIndex
//Question: quand appeler explicitement CG01?
public class CG01 extends CG {
    
    private int ant;
    private int post;
    
    public CG01(final int a, final int p){
        this.ant = a;
        this.post = p;
    }

    @Override
    public void apply(Context context) {
        context.getComplexTripSolver().setOrder(ant, post);
    }

    @Override
    public boolean remove(Flight flight) {
        return false;
    }

    @Override
    public void loadFlights(Context context) {
        // TODO Auto-generated method stub
    }

}
