package context.userConstraints.cg;

import model.Flight;
import context.Context;


//Changements totaux CG01:
//CG01
//CTM --> setOrder
//SCTM --> impl�mentation setOrder + m�thode readIndex
//Question: quand appeler explicitement CG01?
/**
 * Contrainte d'ordre de passage.
 * @author Marc Nkaoua
 *
 */
public class CG01 extends CG {
    
    /**
     * L'index de l'�tape ant�rieure.
     */
    private int ant;
    
    /**
     * L'index de l'�tape post�rieure.
     */
    private int post;
    
    /**
     * Constructeur.
     * @param a L'index de l'�tape ant�rieure.
     * @param p L'index de l'�tape post�rieure.
     */
    public CG01(final int a, final int p){
        this.ant = a;
        this.post = p;
    }

    @Override
    public void apply(final Context context) {
        context.getComplexTripSolver().setOrder(ant, post);
    }

    @Override
    public boolean remove(final Flight flight) {
        return false;
    }

    @Override
    public void loadFlights(final Context context) {
        // No flights to load.
    }

}
