package io.dao.csv;

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
    
    
    //---------------
    //File generation
    //---------------
    

    /**
     * A partir du fichier csv
     * genere tous les fichiers csv par ville de depart.
     */
    public static void generateDep(){
        generate(DaoConstants.DEP_FOLDER, 1, 2);
    }
    
    /**
     * A partir du fichier csv
     * genere tous les fichiers csv par ville d'arrivee.
     */
    public static void generateArr(){
        generate(DaoConstants.ARR_FOLDER, 2, 1);
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
            FileInputStream fstream = new FileInputStream(DaoConstants.INIT_FILE);
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
                elements = line.split(DaoConstants.SEPARATOR, length);
                
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
                    bw.write(elements[content]+DaoConstants.SEPARATOR
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
