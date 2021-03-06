package application;

import io.dao.DAO;
import io.dao.csv.DAOImplCSV;
import io.reader.RequestLoader;
import io.reader.RequestLoaderImp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import model.Trip;

import context.ComplexTripGenerator;
import context.Context;
import solving.ChocoComplexTripSolver;
import solving.IComplexTripSolver;

/**
 * Classe de tests.
 * @author Dim
 *
 */
public final class Test {


    /**
     * Separator in csv file
     */
    static final String SEPARATOR = ";";

    /**
     * Le nombre de requ�te � examiner.
     */
    public static final int NB_FILE_REQUEST=10;
    
    /**
     * Le nombre min d'�tape des requ�tes � g�n�rer.
     */
    public static final int NB_ETAPES_MIN = 10;
    
    /**
     * Le nombre max d'�tape des requ�tes � g�n�rer.
     */
    public static final int NB_ETAPES_MAX = 20;
    
    /**
     * Le nombre min de jours de voyages au plus des requ�tes � g�n�rer.
     */
    public static final int NB_JOURS_MIN = 15;
    
    /**
     * Le nombre max de jours de voyages au plus des requ�tes � g�n�rer.
     */
    public static final int NB_JOURS_MAX = 15;

    /**
     * Le temps de chargement de la derniere requete 
     * (ie toutes les �tapes pr�c�dent la r�solution).
     */
    private static long loadTime;
    
    /**
     * Le temps pass� dans Choco lors de la derniere requete.
     */
    private static long chocoTime;
    
    /**
     * Le temps total de traitement de la requete.
     */
    private static long totalTime;

    /**
     * Constructeur priv� sans arguments (classe utilitaire)
     */
    private Test() {
        
    }
    
    /**
     * Essaye de r�soudre la requ�te.
     * @param dir La direction de la requ�te.
     * @return Le Trip trouv�, ou null si il n'y a pas de solution.
     */
    public static Trip tryToSolve(final String dir){
        return tryToSolve(new File(dir));
    }
    
    /**
     * Essaye de r�soudre la requ�te.
     * @param request La requ�te sous forme de fichier.
     * @return Le Trip trouv�, ou null si il n'y a pas de solution.
     */
    public static Trip tryToSolve(final File request){

        System.out.println("--------------------------------------------------"
        		+ "-----------------------------------------------------------"
                + "-----------------------------------------------------------"
        		+ "---------" + "\n");
        System.out.println("                                                  "
        		+ "                     COMPLEX TRIP GENERATOR V 0.1 " + "\n");
        System.out.println("--------------------------------------------------"
                + "-----------------------------------------------------------"
                + "-----------------------------------------------------------"
                + "---------" + "\n" + "\n");
        
        long t = System.currentTimeMillis();

        IComplexTripSolver solver = new ChocoComplexTripSolver();
        DAO dao = new DAOImplCSV();

        Context context = new Context(solver, dao);

        RequestLoader rloader = new RequestLoaderImp();
        ComplexTripGenerator client = 
                new ComplexTripGenerator(context, rloader);

        boolean b = client.loadRequest(request);

        if (b) {
            solver.constraint();
            long c = System.currentTimeMillis();
            Trip trip = solver.getFirstTripFound("");
            chocoTime = System.currentTimeMillis()-c;
            
            totalTime = System.currentTimeMillis() - t;
            loadTime = totalTime - chocoTime;            
            
            System.out.println("Solution trouv�e en " + totalTime + "ms");
            
            return trip;
            
        } else {
            System.out.println("Echec lors du chargement du fichier "
            		+ "de requ�te - Format invalide");
            return null;
        }
            
    }

    /**
     * Resout en boucle NB_FILE_REQUEST et �crit les r�sultats dans un CSV
     * @param baseName Le dossier et le nom de base des requ�tes.
     * @param dirCSVSuccess Le CSV des r�sultats en cas de succes.
     * @param dirCSVFail Le CSV des r�sultats en cas d'�chec.
     * @param dirSuccess Le dossier et le nom de base o� placer les requ�tes
     * trait�es avec succ�s.
     * @param dirFail Le dossier et le nom de base o� placer les requ�tes
     * trait�es avec �chec.
     */
    public static void doRequests(final String baseName,
            final String dirCSVSuccess,
            final String dirCSVFail,
            final String dirSuccess,
            final String dirFail){
        try{
            File f1 = new File(dirCSVSuccess);
            if(!f1.exists()){
                f1.createNewFile();   
            }
            
            FileWriter fw1 = new FileWriter(f1.getAbsoluteFile(), true);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            
            File f2 = new File(dirCSVFail);
            if(!f2.exists()){
                f2.createNewFile();   
            }
            FileWriter fw2 = new FileWriter(f2.getAbsoluteFile(), true);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            
            
            bw1.write("Index de la requ�te" + SEPARATOR 
                    + "Temps de chargement (ms)" + SEPARATOR 
                    + "Temps de r�solution Choco (ms)" + SEPARATOR
                    + "Temps total (ms)");
            bw1.newLine();
            
            bw2.write("Index de la requ�te" + SEPARATOR 
                    + "Temps de chargement (ms)" + SEPARATOR 
                    + "Temps de r�solution Choco (ms)" + SEPARATOR
                    + "Temps total (ms)");
            bw2.newLine();   
            
            for (int i=0; i<NB_FILE_REQUEST; i++){
                Trip trip =tryToSolve(baseName+i+".txt");
                if(trip!=null){
                    System.out.println(trip);
                    bw1.write(i+SEPARATOR + loadTime + SEPARATOR 
                            + chocoTime + SEPARATOR + totalTime);
                    bw1.newLine();
                    File f = new File(baseName+i+".txt");
                    f.renameTo(new File(dirSuccess + i + ".txt"));
                } else {
                    bw2.write(i+SEPARATOR + loadTime + SEPARATOR 
                            + chocoTime + SEPARATOR + totalTime);
                    bw2.newLine();
                    File f = new File(baseName+i+".txt");
                    f.renameTo(new File(dirFail + i + ".txt"));
                }
            }
            bw1.close();
            bw2.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Main
     * @param args arguments.
     */
    public static void main(final String[] args){
        
        // BATTERIE DE TESTS
//        for (int etapes = NB_ETAPES_MIN; etapes <= NB_ETAPES_MAX; etapes++) {
//            for (int jours = NB_JOURS_MIN; jours <= NB_JOURS_MAX; jours++) {
//                
//                String baseName = "res/requests/" + etapes + " etape sur " 
//                        + jours + " jours";
//                
//                Path myDir = Paths.get(baseName);
//                
//                File folder = new File(baseName);
//                if(!folder.exists()){
//                    folder.mkdir();
//                }
//                
//                File folderS = new File(baseName + "/success");
//                if(!folderS.exists()){
//                    folderS.mkdir();
//                }
//                
//                File folderF = new File(baseName + "/fail");
//                if(!folderF.exists()){
//                    folderF.mkdir();
//                }
//                
//                for(int i = 0; i < NB_FILE_REQUEST; i++){
//                    Generate.createFile(myDir, "request " + i + ".txt",
//                            Generate.PLAGE_DEFAULT, Generate.PLAGE_DEFAULT,
//                            etapes, jours);
//                }    
//                
//                doRequests(baseName + "/request ",
//                            baseName + "/resultsSuccess.csv",
//                            baseName + "/resultsFail.csv",
//                            baseName + "/success/request ",
//                            baseName + "/fail/request ");
//            }
//        }

        System.out.println(tryToSolve("res/requests/constraint 0.txt"));

    }
}
