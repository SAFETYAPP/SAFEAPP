package com.viewnine.safeapp.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.viewnine.safeapp.activity.HistoryActivity;
import com.viewnine.safeapp.activity.LockScreenAppActivity;
import com.viewnine.safeapp.activity.PlayVideoActivity;
import com.viewnine.safeapp.activity.R;
import com.viewnine.safeapp.activity.RecordForegroundVideoActivity;
import com.viewnine.safeapp.activity.ScreenUnlockActivity;
import com.viewnine.safeapp.activity.SecurityActivity;
import com.viewnine.safeapp.activity.SettingsActivity;
import com.viewnine.safeapp.activity.SetupActivity;
import com.viewnine.safeapp.ulti.Constants;

/**
 * Created by user on 4/19/15.
 */
public class SwitchViewManager {
    private static SwitchViewManager ourInstance = new SwitchViewManager();

    public static SwitchViewManager getInstance() {
        return ourInstance;
    }

    private SwitchViewManager() {
    }

    public void gotoLockScreen(Context context){
        Intent intent = new Intent(context, LockScreenAppActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((Activity)context).startActivity(intent);
        ((Activity)context).finish();
    }

    public void gotoRecordForegroundVideoScreen(Context context){
        Intent intent = new Intent(context, RecordForegroundVideoActivity.class);
        ((Activity)context).startActivity(intent);
        ((Activity)context).finish();
    }

    public void gotoRecordSetupScreen(Context context){
        Intent intent = new Intent(context, SetupActivity.class);
        ((Activity)context).startActivity(intent);
        ((Activity)context).finish();
    }

    public void sendAppToBackground(Context context){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ((Activity)context).startActivity(intent);
    }

    public void gotoHistoryScreen(Context context){
        Intent intent = new Intent(context, HistoryActivity.class);
        ((Activity)context).startActivity(intent);
        ((Activity)context).finish();
    }

    public void gotoVideoScreen(Context context, String videoUrl){
        Intent intent = new Intent(context, PlayVideoActivity.class);
        intent.putExtra(Constants.VIDEO_LINK, videoUrl);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    public void gotoSettingsScreen(Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    public void gotoSecurityScreen(Context context){
        Intent intent = new Intent(context, SecurityActivity.class);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    public void gotoScreenUnlockScreen(Context context){
        Intent intent = new Intent(context, ScreenUnlockActivity.class);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    private void startPushScreenIn(Context context){
        ((Activity) context).overridePendingTransition(R.anim.push_up_in, R.anim.stay);
    }
}
