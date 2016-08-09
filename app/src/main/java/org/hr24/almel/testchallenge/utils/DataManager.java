package org.hr24.almel.testchallenge.utils;

/**
 * Created by Alexey on 09.08.16.
 */
public class DataManager {
    private static DataManager INSTANCE = null;



    private PreferenceManager mPreferenceManager;


    public DataManager() {
        this.mPreferenceManager = new PreferenceManager();
    }


    public static DataManager getINSTANCE(){
        if(INSTANCE==null){
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }
    public PreferenceManager getPreferenceManager() {
        return mPreferenceManager;
    }
}
