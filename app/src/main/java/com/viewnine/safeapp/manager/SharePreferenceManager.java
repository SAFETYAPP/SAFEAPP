package com.viewnine.safeapp.manager;

import android.content.SharedPreferences;

import com.viewnine.safeapp.application.SafeAppApplication;
import com.viewnine.safeapp.ulti.Constants;

/**
 * Created by user on 4/18/15.
 */
public class SharePreferenceManager {
    private static final String PREFS_NAME = "SafeApp_Frefs";
    private static final String IS_FIRST_TIME_RUNNING = "IS_FIRST_TIME_RUNNING";
    private static final String PRIMARY_EMAIL = "PRIMARY_EMAIL";
    private static final String SECONDARY_EMAIL = "SECONDARY_EMAIL";

    private static SharePreferenceManager instance;
    private static SharedPreferences preferences;

    public static SharePreferenceManager getInstance() {
        if (instance == null) {
            synchronized (SharePreferenceManager.class) {
                if (instance == null) {
                    instance = new SharePreferenceManager();
                    preferences = SafeAppApplication.getInstance().getSharedPreferences(PREFS_NAME, 0 );
                }
            }
        }

        return instance;
    }

    public void setPrimaryEmail(String email){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PRIMARY_EMAIL, email);
        editor.commit();
    }

    public String getPrimaryEmail(){
        return preferences.getString(PRIMARY_EMAIL, Constants.EMPTY_STRING);
    }

    public void setSecondaryEmail(String email){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SECONDARY_EMAIL, email);
        editor.commit();
    }

    public String getSecondaryEmail(){
        return preferences.getString(SECONDARY_EMAIL, Constants.EMPTY_STRING);
    }
}
