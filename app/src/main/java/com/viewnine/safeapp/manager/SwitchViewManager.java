package com.viewnine.safeapp.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.viewnine.safeapp.activity.LockScreenAppActivity;
import com.viewnine.safeapp.activity.RecordForegroundVideoActivity;
import com.viewnine.safeapp.activity.SetupActivity;

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
}
