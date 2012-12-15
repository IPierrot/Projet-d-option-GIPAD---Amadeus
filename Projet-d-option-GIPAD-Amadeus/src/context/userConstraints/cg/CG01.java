package context.userConstraints.cg;

import model.Flight;
import context.Context;
import context.userConstraints.cve.CVE;

public class CG01 extends CG {
    
    private CVE ant;
    private CVE post;
    
    public CG01(CVE a, CVE p){
        this.ant = a;
        this.post = p;
    }

    @Override
    public void apply(Context context) {
        context.getComplexTripModel().setOrder(ant, post);
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
