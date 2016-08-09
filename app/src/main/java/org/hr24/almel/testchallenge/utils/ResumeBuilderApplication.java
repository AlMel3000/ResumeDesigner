package org.hr24.almel.testchallenge.utils;

import android.app.Application;
import android.content.SharedPreferences;

import static android.preference.PreferenceManager.*;


/**
 * Created by Alexey on 09.08.16.
 */
public class ResumeBuilderApplication  extends Application {
    public static SharedPreferences sSharedPreferences;



    @Override
    public void onCreate() {
        super.onCreate();
        sSharedPreferences = getDefaultSharedPreferences(this);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }
}
