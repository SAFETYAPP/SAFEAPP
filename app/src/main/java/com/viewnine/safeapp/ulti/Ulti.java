package com.viewnine.safeapp.ulti;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;

import com.viewnine.safeapp.activity.LockScreenAppActivity;

import java.util.List;

/**
 * Created by user on 4/19/15.
 */
public class Ulti {

    private static final String TAG = Ulti.class.getName();

    public static boolean isAppRunningBackground(Context context) {
        if (context == null) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getApplicationContext()
                .getSystemService(Service.ACTIVITY_SERVICE);
        // get the info from the currently running task

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;

        // get the name of the package:
        String packageName = componentInfo.getPackageName();

        // Log.v(TAG, "TESTING value = " + "2012-2-27 10:46:00"
        // .compareTo("2012-2-29 10:14:00"));

        // check if the app's just come back from background
        LogUtils.logI(TAG , "Current packageName: " + packageName + ". Current Activity name: " + context.getClass().getSimpleName());
        if (!packageName.equals(context.getApplicationContext().getPackageName())) {
            return true;
        }
        return false;
    }

    public static boolean isAppOnTop(Context context){
        if (context == null) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getApplicationContext()
                .getSystemService(Service.ACTIVITY_SERVICE);
        // get the info from the currently running task

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;

        // get the name of the package:
        String packageName = componentInfo.getPackageName();

        LogUtils.logI(TAG , "Current packageName: " + packageName + ". Current Activity name: " + componentInfo.getShortClassName());
        if(context.getApplicationContext().getPackageName().equalsIgnoreCase(packageName) && componentInfo.getShortClassName().contains(LockScreenAppActivity.class.getSimpleName())){
            return true;
        }else {
            return false;
        }

    }
}
