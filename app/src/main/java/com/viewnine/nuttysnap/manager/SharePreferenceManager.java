package com.viewnine.nuttysnap.manager;

import android.content.SharedPreferences;

import com.viewnine.nuttysnap.application.SafeAppApplication;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.view.CameraPreview;

/**
 * Created by user on 4/18/15.
 */
public class SharePreferenceManager {
    private static final String PREFS_NAME = "SafeApp_Frefs";
    private static final String IS_FIRST_TIME_RUNNING = "IS_FIRST_TIME_RUNNING";
    private static final String PRIMARY_EMAIL = "PRIMARY_EMAIL";
    private static final String SECONDARY_EMAIL = "SECONDARY_EMAIL";
    private static final String UNLOCK_PATTERN = "UNLOCK_PATTERN";
    private static final String ENTRY_DURATION_TIME_DEFAULT = "ENTRY_DURATION_TIME_DEFAULT";
    private static final String ENABLE_NOTIFICATION_FOR_EACH_BACKUP = "ENABLE_NOTIFICATION_FOR_EACH_BACKUP";
    private static final String IS_EXIT_APP_FROM_LOCK_SCREEN_ACTIVITY = "IS_EXIT_APP_FROM_LOCK_SCREEN_ACTIVITY";
    private static final String BACKGROUND_CAMERA_ID = "BACKGROUND_CAMERA_ID";
    private static final String ENABLE_LOCK_SCREEN = "ENABLE_LOCK_SCREEN";

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

    public void setUnlockPattern(String patternString){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UNLOCK_PATTERN, patternString);
        editor.commit();
    }

    public String getUnlockPattern(){
        return preferences.getString(UNLOCK_PATTERN, Constants.EMPTY_STRING);
    }

    public void setIndexDurationTime(int index){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ENTRY_DURATION_TIME_DEFAULT, index);
        editor.commit();
    }

    public int getIndexDurationTime(){
       return preferences.getInt(ENTRY_DURATION_TIME_DEFAULT, 0);
    }

    public void setEnableNotificationForEachBackup(boolean enable){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ENABLE_NOTIFICATION_FOR_EACH_BACKUP, enable);
        editor.commit();
    }

    public boolean isEnableNotificationForEachBackup(){
        return preferences.getBoolean(ENABLE_NOTIFICATION_FOR_EACH_BACKUP, true);
    }

    public void setIsExitAppFromLockScreenActivity(boolean isExitAppFromLockScreenActivity){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_EXIT_APP_FROM_LOCK_SCREEN_ACTIVITY, isExitAppFromLockScreenActivity);
        editor.commit();
    }

    public boolean getIsExitAppFromLockSCreenActivity(){
        return preferences.getBoolean(IS_EXIT_APP_FROM_LOCK_SCREEN_ACTIVITY, true);
    }

    public void setBackgroundCameraId(int cameraId){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(BACKGROUND_CAMERA_ID, cameraId);
        editor.commit();
    }

    public int getBackgroundCameraId(){
        return preferences.getInt(BACKGROUND_CAMERA_ID, CameraPreview.DEFAULT_CAMERA);
    }

    public void setEnableLockScreen(boolean enable){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ENABLE_LOCK_SCREEN, enable);
        editor.commit();
    }

    public boolean isEnableLockScreen(){
        return preferences.getBoolean(ENABLE_LOCK_SCREEN, true);
    }


}
