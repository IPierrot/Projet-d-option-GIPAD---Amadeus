package application.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.Trip;

import context.Client;
import context.Context;
import dao.DAO;
import dao.csv.DAOImplCSV;
import reader.Generate;
import reader.RequestLoader;
import reader.RequestLoaderImp;
import solving.ComplexTripModel;
import solving.ComplexTripSolver;
import solving.SimpleComplexTripModel;
import solving.SimpleComplexTripSolver;

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
     * Le nombre de requête à examiner.
     */
    public static final int NB_FILE_REQUEST=10;
    
    /**
     * Le nombre min d'étape des requêtes à générer.
     */
    public static final int NB_ETAPES_MIN = 10;
    
    /**
     * Le nombre max d'étape des requêtes à générer.
     */
    public static final int NB_ETAPES_MAX = 20;
    
    /**
     * Le nombre min de jours de voyages au plus des requêtes à générer.
     */
    public static final int NB_JOURS_MIN = 15;
    
    /**
     * Le nombre max de jours de voyages au plus des requêtes à générer.
     */
    public static final int NB_JOURS_MAX = 15;

    /**
     * Le temps de chargement de la derniere requete 
     * (ie toutes les étapes précédent la résolution).
     */
    private static long loadTime;
    
    /**
     * Le temps passé dans Choco lors de la derniere requete.
     */
    private static long chocoTime;
    
    /**
     * Le temps total de traitement de la requete.
     */
    private static long totalTime;

    /**
     * Constructeur privé sans arguments (classe utilitaire)
     */
    private Test() {
        
    }
    
    /**
     * Essaye de résoudre la requête.
     * @param dirRequest La direction de la requête.
     * @return Le Trip trouvé, ou null si il n'y a pas de solution.
     */
    public static Trip tryToSolve(final String dirRequest){

        long t = System.currentTimeMillis();

        ComplexTripModel model = new SimpleComplexTripModel();
        DAO dao = new DAOImplCSV();

        Context context = new Context(model, dao);

        RequestLoader rloader = new RequestLoaderImp();
        Client client = new Client(context, rloader);

        client.loadRequest(dirRequest);

        ComplexTripSolver solver = new SimpleComplexTripSolver();
        solver.read(model);
        Trip trip = solver.getFirstTripFound();

        totalTime = System.currentTimeMillis() - t;
        chocoTime = solver.getCPSolver().getTimeCount();
        loadTime = totalTime - chocoTime;

        return trip;
    }

    /**
     * Resout en boucle NB_FILE_REQUEST et écrit les résultats dans un CSV
     * @param baseName Le dossier et le nom de base des requêtes.
     * @param dirCSVSuccess Le CSV des résultats en cas de succes.
     * @param dirCSVFail Le CSV des résultats en cas d'échec.
     * @param dirSuccess Le dossier et le nom de base où placer les requêtes
     * traitées avec succès.
     * @param dirFail Le dossier et le nom de base où placer les requêtes
     * traitées avec échec.
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
            
            
            bw1.write("Index de la requête" + SEPARATOR 
                    + "Temps de chargement (ms)" + SEPARATOR 
                    + "Temps de résolution Choco (ms)" + SEPARATOR
                    + "Temps total (ms)");
            bw1.newLine();
            
            bw2.write("Index de la requête" + SEPARATOR 
                    + "Temps de chargement (ms)" + SEPARATOR 
                    + "Temps de résolution Choco (ms)" + SEPARATOR
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
