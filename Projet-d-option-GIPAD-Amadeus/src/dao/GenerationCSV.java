package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * Genere les fichiers csv decomposes pour une meilleure selection des vols.
 */
public final class GenerationCSV {

    /**
     * Constructeur prive
     */
    private GenerationCSV(){
    }
    
    /**
     * Initial csv file containing all data
     */
    public static final String INIT_FILE = "flight_data.csv";
    
    /**
     * Name of folder where files per departure airport are copied
     */
    public static final String DEP_FOLDER = "departure";
    
    /**
     * Name of folder where files per arrival airport are copied
     */
    public static final String ARR_FOLDER = "arrival";
    
    //---------------
    //File generation
    //---------------
    

    /**
     * A partir du fichier csv
     * genere tous les fichiers csv par ville de depart.
     */
    public static void generateDep(){
        generate(DEP_FOLDER, 1, 2);
    }
    
    /**
     * A partir du fichier csv
     * genere tous les fichiers csv par ville d'arrivee.
     */
    public static void generateArr(){
        generate(ARR_FOLDER, 2, 1);
    }
    
    /**
     * Methode generale de generation csv
     * @param folder le nom du fichier dans lequel ecrire
     * @param title le parametre utilise comme titre de csv
     * @param content le parametre passe dans le fichier
     */
    private static void generate(final String folder, final int title, 
            final int content){
        
        final int length = 4;
        try{
            //open document
            FileInputStream fstream = new FileInputStream(INIT_FILE);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            //Delete old files
            deleteFolder(new File(folder));
            
            String line;
            String[] elements = new String[length];
            
            boolean nline = true;
            //Lines loop
            while((line = br.readLine()) != null){
                nline = true;
                elements = line.split(DAO.SEPARATOR, length);
                
                //Writing if not the same airport for departure and arrival
                if(!elements[1].equals(elements[2])){
                    File f = new File(folder+"/"+elements[title]+".csv");
                    if(!f.exists()){
                        f.createNewFile();
                        nline = false;
                    }
                    FileWriter fw = new FileWriter(f.getAbsoluteFile(), true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    if(nline){
                        bw.newLine();
                    }
                    bw.write(elements[content]+DAO.SEPARATOR
                            +elements[length-1]);
            
                    bw.close();
                }
            }
            br.close();
            
        } catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Efface tous les fichiers d'un dossier
     * Attention: efface uniquement les fichiers, pas les dossiers
     * @param folder le dossier dont on veut effacer le contenu
     */
    private static void deleteFolder(final File folder){
        File[] files = folder.listFiles();
        if(files!=null) {
            for(File f: files){
                f.delete();
            }
        }
    }
    
    /**
     * main pour generer les fichiers
     * @param args param.
     */
    public static void main(final String[] args){
        long t = System.currentTimeMillis();
        generateDep();
        generateArr();
        System.out.println("Données découpées en  " 
                + (System.currentTimeMillis()-t) + " ms");
    }
}
