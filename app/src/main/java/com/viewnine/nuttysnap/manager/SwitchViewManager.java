package com.viewnine.nuttysnap.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.activity.BackupActivity;
import com.viewnine.nuttysnap.activity.HistoryActivity;
import com.viewnine.nuttysnap.activity.InAppBrowserActivity;
import com.viewnine.nuttysnap.activity.IntroduceActivity;
import com.viewnine.nuttysnap.activity.LockScreenAppActivity;
import com.viewnine.nuttysnap.activity.NotificationsActivity;
import com.viewnine.nuttysnap.activity.PlayVideoActivity;
import com.viewnine.nuttysnap.activity.RecordForegroundVideoActivity;
import com.viewnine.nuttysnap.activity.ScreenUnlockActivity;
import com.viewnine.nuttysnap.activity.SecurityActivity;
import com.viewnine.nuttysnap.activity.SettingsActivity;
import com.viewnine.nuttysnap.activity.SetupActivity;
import com.viewnine.nuttysnap.application.SafeAppApplication;
import com.viewnine.nuttysnap.model.VideoObject;
import com.viewnine.nuttysnap.ulti.Constants;
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
        SafeAppApplication.finishAllPreviousActivity();
        SafeAppIndexActivityManager.setCurrent(Constants.LOCK_SCREEN_ACTIVITY);
        Intent intent = new Intent(context, LockScreenAppActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        ((Activity)context).startActivity(intent);
        ((Activity)context).finish();
    }

    public void gotoRecordForegroundVideoScreen(Context context){
        SafeAppIndexActivityManager.setCurrent(Constants.RECORD_FOREGROUND_ACTIVITY);
        Intent intent = new Intent(context, RecordForegroundVideoActivity.class);
        ((Activity)context).startActivity(intent);
        ((Activity)context).finish();
    }

    public void gotoRecordSetupScreen(Context context){
        SafeAppIndexActivityManager.setCurrent(Constants.SETUP_ACTIVITY);
        Intent intent = new Intent(context, SetupActivity.class);
        ((Activity)context).startActivity(intent);
        ((Activity)context).finish();
    }

    public void gotoIntroduceScreen(Context context){
        SafeAppIndexActivityManager.setCurrent(Constants.INTRODUCE_ACTIVITY);
        Intent intent = new Intent(context, IntroduceActivity.class);
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
        SafeAppIndexActivityManager.setCurrent(Constants.HISTORY_ACTIVITY);
        Intent intent = new Intent(context, HistoryActivity.class);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
        ((Activity)context).finish();
    }

    public void gotoVideoScreen(Context context, VideoObject videoObject){
        SafeAppIndexActivityManager.setCurrent(Constants.VIDEO_ACTIVITY);
        Intent intent = new Intent(context, PlayVideoActivity.class);
//        intent.putExtra(Constants.VIDEO_LINK, videoUrl);
        intent.putExtra(Constants.VIDEO_LINK, videoObject);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    public void gotoSettingsScreen(Context context){
        SafeAppIndexActivityManager.setCurrent(Constants.SETTINGS_ACTIVITY);
        Intent intent = new Intent(context, SettingsActivity.class);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    public void gotoSecurityScreen(Context context){
        SafeAppIndexActivityManager.setCurrent(Constants.SECURITY_ACTIVITY);
        Intent intent = new Intent(context, SecurityActivity.class);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    public void gotoNotificationsScreen(Context context){
        SafeAppIndexActivityManager.setCurrent(Constants.NOTIFICATIONS_ACTIVITY);
        Intent intent = new Intent(context, NotificationsActivity.class);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    public void gotoBackupScreen(Context context){
        SafeAppIndexActivityManager.setCurrent(Constants.BACKUP_ACTIVITY);
        Intent intent = new Intent(context, BackupActivity.class);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    public void gotoBrowserScreen(Context context, String title, String url){
        SafeAppIndexActivityManager.setCurrent(Constants.BROWSER_SCREEN);
        Intent intent = new Intent(context, InAppBrowserActivity.class);
        intent.putExtra(InAppBrowserActivity.CONTENT_URL, url);
        intent.putExtra(InAppBrowserActivity.TITLE, title);
        ((Activity)context).startActivity(intent);
        startPushScreenIn(context);
    }

    public void gotoScreenUnlockScreen(Context context, int requestCode, boolean enableLockScreenChange){
        SafeAppIndexActivityManager.setCurrent(Constants.UNLOCK_PATTERN_ACTIIVTY);
        Intent intent = new Intent(context, ScreenUnlockActivity.class);
        intent.putExtra(Constants.ENABLE_LOCK_SCREEN, enableLockScreenChange);
        ((Activity)context).startActivityForResult(intent, requestCode);
        startPushScreenIn(context);
    }

    private void startPushScreenIn(Context context){
        ((Activity) context).overridePendingTransition(R.anim.push_up_in, R.anim.stay);
    }
}
