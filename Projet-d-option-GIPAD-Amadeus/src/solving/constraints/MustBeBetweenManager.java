package solving.constraints;

import java.util.List;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class MustBeBetweenManager extends IntConstraintManager{

    @Override
    public SConstraint<?> makeConstraint(final Solver solver, 
            final IntegerVariable[] variables, final Object parameters,
            final List<String> options) {
        IntDomainVar[] vars = new IntDomainVar[variables.length];
        for(int i=0; i<vars.length; i++) {
            vars[i]=solver.getVar(variables[i]);
        }
        Object[] params = (Object[]) parameters;
        return new MustBeBetween(vars[0], vars[1],
                (int[]) params[0], (int) params[1]);
    }
}
