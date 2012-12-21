package io.dao.csv;

/**
 * Classe des constantes pour le package DaoCsv.
 * @author Marc
 *
 */
public final class DaoConstants {

    /**
     * Separator in csv file
     */
    static final String SEPARATOR = ";";
    
    /**
     * Index of the destination in departure or arrival files
     * (must be the same !)
     */
    static final int DESTINATION = 0;
    
    /**
     * Index of the departure time in files
     */
    static final int DEP_TIME = 1;
    
    /**
     * Index of the departure GMT in files
     */
    static final int DEP_GMT = 2;
    
    /**
     * Index of the arrival time in files
     */
    static final int ARR_TIME = 3;
    
    /**
     * Index of the arrival GMT in files
     */
    static final int ARR_GMT = 4;
    
    /**
     * Index of the arrival day offset in files
     */
    static final int ARR_OFFSET = 5;
    
    /**
     * Index of the flight ID in files
     */
    static final int ID = 6;
    
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
    
}
